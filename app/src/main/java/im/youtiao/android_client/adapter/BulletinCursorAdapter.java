package im.youtiao.android_client.adapter;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.BulletinHelper;
import im.youtiao.android_client.event.BulletinCommentClickEvent;
import im.youtiao.android_client.event.BulletinGroupNameClickEvent;
import im.youtiao.android_client.event.BulletinStampEvent;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Stamp;
import im.youtiao.android_client.ui.activity.fragment.BulletinsFragment;
import im.youtiao.android_client.ui.widget.LoadMoreView;
import im.youtiao.android_client.util.TimeWrap;
import im.youtiao.android_client.wrap.BulletinWrap;
import im.youtiao.android_client.util.Log;

public class BulletinCursorAdapter extends CursorAdapter {
    private static final String TAG = BulletinCursorAdapter.class
            .getCanonicalName();
    private LayoutInflater mInflater;
    private Activity mActivity;
    private Fragment mFragment;

    public BulletinCursorAdapter(Activity activity, Fragment fragment) {
        super(activity, null, false);
        mActivity = activity;
        mFragment = fragment;
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View resultView;
        if (position == getCount() - 1 ) {
            resultView  = getLoadMoreView();
        } else {
            if (convertView != null) {
                ViewHolder viewHolder = (ViewHolder) convertView.getTag();
                if (viewHolder == null) {
                    convertView = null;
                }
            }
            resultView = super.getView(position, convertView, parent);
        }
        return resultView;
    }

    View getLoadMoreView() {
        LayoutInflater mInflater = LayoutInflater.from(mActivity);
        View convertView = mInflater.inflate(R.layout.widget_load_more, null);
        LoadMoreView loadMoreView = (LoadMoreView) convertView.findViewById(R.id.loadMoreView);

        LoadMoreView.Mode mode;

        if (getCount() == 1 ) {
            if (((BulletinsFragment)mFragment).isInit()) {
                mode = LoadMoreView.Mode.LOADING;
            } else {
                mode = LoadMoreView.Mode.NONE_FOUND;
            }
        } else {
            mode = ((BulletinsFragment)mFragment).hasMoreDate() ? LoadMoreView.Mode.LOADING : LoadMoreView.Mode.NO_MORE;
        }
        loadMoreView.configure(mode);
        return loadMoreView;
    }

    View inflateBulletinItem() {
        ViewHolder viewHolder = new ViewHolder();
        View convertView = mInflater.inflate(R.layout.row_bulletin, null);
        viewHolder.createdInfoTv = (TextView) convertView.findViewById(R.id.tv_created_info);
        viewHolder.feedContentTv = (TextView) convertView.findViewById(R.id.tv_bulletin_text);
        viewHolder.groupNameTv = (TextView) convertView.findViewById(R.id.tv_group_name);
        viewHolder.checkImgBtn = (ImageView) convertView.findViewById(R.id.imgBtn_bulletin_check);
        viewHolder.crossImgBtn = (ImageView) convertView.findViewById(R.id.imgBtn_bulletin_cross);
        viewHolder.checksCountTv = (TextView) convertView.findViewById(R.id.tv_bulletin_checks_count);
        viewHolder.crossesCountTv = (TextView) convertView.findViewById(R.id.tv_bulletin_crosses_count);
        viewHolder.checkLayout = (LinearLayout) convertView.findViewById(R.id.layout_check);
        viewHolder.crossLayout = (LinearLayout) convertView.findViewById(R.id.layout_cross);
        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Log.i(TAG, "newView");
        return inflateBulletinItem();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Log.i(TAG, "bindView:" + this.getCount());
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            view = inflateBulletinItem();
            viewHolder = (ViewHolder) view.getTag();
        }

        final Bulletin bulletin = BulletinWrap.wrap(BulletinHelper.fromCursor(cursor));

        viewHolder.feedContentTv.setText(Html.fromHtml("<b>" + bulletin.createdBy.name + ": " + "</b>" + bulletin.text));
        String createdAt = (TimeWrap.wrapTimeDisplyValue(Math.round(1000*Double.parseDouble(bulletin.createdAt)), mActivity));
        String creatorName = bulletin.createdBy.name;
        viewHolder.createdInfoTv.setText(createdAt);
        viewHolder.groupNameTv.setText(bulletin.group.name);
        viewHolder.checksCountTv.setText("" + bulletin.checksCount);
        viewHolder.crossesCountTv.setText("" + bulletin.crossesCount);

        viewHolder.checkImgBtn.setColorFilter(mActivity.getResources().getColor(R.color.icon_unselected_color));
        viewHolder.crossImgBtn.setColorFilter(mActivity.getResources().getColor(R.color.icon_unselected_color));
        viewHolder.checksCountTv.setTextColor(mActivity.getResources().getColor(R.color.icon_unselected_color));
        viewHolder.crossesCountTv.setTextColor(mActivity.getResources().getColor(R.color.icon_unselected_color));
        if (bulletin.stamp != null && bulletin.stamp.symbol != null) {
            switch (Stamp.Mark.valueOf(bulletin.stamp.symbol.toUpperCase())) {
                case CHECK:
                    viewHolder.checkImgBtn.setColorFilter(mActivity.getResources().getColor(R.color.icon_stamp_check_selected_color));
                    viewHolder.checksCountTv.setTextColor(mActivity.getResources().getColor(R.color.icon_stamp_check_selected_color));
                    break;
                case CROSS:
                    viewHolder.crossImgBtn.setColorFilter(mActivity.getResources().getColor(R.color.icon_stamp_cross_selected_color));
                    viewHolder.crossesCountTv.setTextColor(mActivity.getResources().getColor(R.color.icon_stamp_cross_selected_color));
                    break;
                default:
            }
        }

        viewHolder.checkLayout.setOnClickListener(v -> {
            EventBus.getDefault().post(new BulletinStampEvent(bulletin, Stamp.Mark.CHECK.toString().toLowerCase()));

        });

        viewHolder.crossLayout.setOnClickListener(v -> {
            EventBus.getDefault().post(new BulletinStampEvent(bulletin, Stamp.Mark.CROSS.toString().toLowerCase()));
        });

        viewHolder.groupNameTv.setOnClickListener(v -> {
            Log.i(TAG, "groupNameTv click");
            EventBus.getDefault().post(new BulletinGroupNameClickEvent(bulletin));
        });
    }

    static class ViewHolder {
        public TextView feedContentTv;
        public TextView groupNameTv;
        public TextView createdInfoTv;
        public ImageView checkImgBtn;
        public TextView checksCountTv;
        public ImageView crossImgBtn;
        public TextView crossesCountTv;
        public LinearLayout checkLayout;
        public LinearLayout crossLayout;
    }
}
