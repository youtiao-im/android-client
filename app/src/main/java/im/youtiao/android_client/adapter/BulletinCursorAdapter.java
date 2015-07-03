package im.youtiao.android_client.adapter;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
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
import im.youtiao.android_client.wrap.BulletinWrap;

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
        viewHolder.creatorNameTv = (TextView) convertView.findViewById(R.id.tv_user_name);
        viewHolder.createdAtTv = (TextView) convertView.findViewById(R.id.tv_created_at);
        viewHolder.feedContentTv = (TextView) convertView.findViewById(R.id.tv_bulletin_text);
        viewHolder.groupNameTv = (TextView) convertView.findViewById(R.id.tv_group_name);
        viewHolder.commentsCountTv = (TextView) convertView.findViewById(R.id.tv_bulletin_comment_count);
        viewHolder.checkImgBtn = (ImageButton) convertView.findViewById(R.id.imgBtn_bulletin_check);
        viewHolder.crossImgBtn = (ImageButton) convertView.findViewById(R.id.imgBtn_bulletin_cross);
        viewHolder.commentImgBtn = (ImageButton) convertView.findViewById(R.id.imgBtn_bulletin_comment);
        viewHolder.checksCountTv = (TextView) convertView.findViewById(R.id.tv_bulletin_checks_count);
        viewHolder.crossesCountTv = (TextView) convertView.findViewById(R.id.tv_bulletin_crosses_count);
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
        viewHolder.creatorNameTv.setText("#" + bulletin.createdBy.name);
        viewHolder.feedContentTv.setText(bulletin.text);
        viewHolder.createdAtTv.setText("5/6/15");
        viewHolder.groupNameTv.setText(bulletin.group.name);
        viewHolder.commentsCountTv.setText("" + bulletin.comments_count);
        viewHolder.checksCountTv.setText("" + bulletin.checksCount);
        viewHolder.crossesCountTv.setText("" + bulletin.crossesCount);

        viewHolder.checkImgBtn.setColorFilter(mActivity.getResources().getColor(R.color.icon_unselected_color));
        viewHolder.crossImgBtn.setColorFilter(mActivity.getResources().getColor(R.color.icon_unselected_color));
        viewHolder.commentImgBtn.setColorFilter(mActivity.getResources().getColor(R.color.icon_unselected_color));
        if (bulletin.stamp != null && bulletin.stamp.symbol != null) {
            switch (Stamp.Mark.valueOf(bulletin.stamp.symbol.toUpperCase())) {
                case CHECK:
                    viewHolder.checkImgBtn.setColorFilter(mActivity.getResources().getColor(R.color.icon_selected_color));
                    break;
                case CROSS:
                    viewHolder.crossImgBtn.setColorFilter(mActivity.getResources().getColor(R.color.icon_selected_color));
                    break;
                default:
            }
        }

        viewHolder.checkImgBtn.setOnClickListener(v -> {
            EventBus.getDefault().post(new BulletinStampEvent(bulletin, Stamp.Mark.CHECK.toString().toLowerCase()));

        });

        viewHolder.crossImgBtn.setOnClickListener(v -> {
            EventBus.getDefault().post(new BulletinStampEvent(bulletin, Stamp.Mark.CROSS.toString().toLowerCase()));

        });

        viewHolder.commentImgBtn.setOnClickListener( v -> {
            EventBus.getDefault().post(new BulletinCommentClickEvent(bulletin));
        });

        viewHolder.groupNameTv.setOnClickListener(v -> {
            Log.i(TAG, "groupNameTv click");
            EventBus.getDefault().post(new BulletinGroupNameClickEvent(bulletin));
        });
    }

    static class ViewHolder {
        public TextView creatorNameTv;
        public TextView feedContentTv;
        public TextView groupNameTv;
        public TextView createdAtTv;
        public ImageButton checkImgBtn;
        public TextView checksCountTv;
        public ImageButton commentImgBtn;
        public TextView commentsCountTv;
        public ImageButton crossImgBtn;
        public TextView crossesCountTv;
    }
}
