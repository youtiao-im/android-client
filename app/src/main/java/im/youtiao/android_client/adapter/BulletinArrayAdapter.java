package im.youtiao.android_client.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

import de.greenrobot.event.EventBus;
import im.youtiao.android_client.R;
import im.youtiao.android_client.event.BulletinCommentClickEvent;
import im.youtiao.android_client.event.BulletinStampEvent;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Stamp;
import im.youtiao.android_client.ui.activity.GroupDetailActivity;
import im.youtiao.android_client.ui.widget.LoadMoreView;

public class BulletinArrayAdapter extends ArrayAdapter<Bulletin> {
    private static final String TAG = BulletinArrayAdapter.class
            .getCanonicalName();
    private int resourceId;
    private Context mContext;

    public BulletinArrayAdapter(Context context, int resource, LinkedList<Bulletin> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
        this.mContext = context;
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
            resultView = getBulletinView(convertView, position);
        }
        return resultView;
    }

    View getLoadMoreView() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View convertView = mInflater.inflate(R.layout.widget_load_more, null);
        LoadMoreView loadMoreView = (LoadMoreView) convertView.findViewById(R.id.loadMoreView);

        LoadMoreView.Mode mode;

        if (getCount() == 1 ) {
            if (((GroupDetailActivity)mContext).isInit()) {
                mode = LoadMoreView.Mode.LOADING;
            } else {
                mode = LoadMoreView.Mode.NONE_FOUND;
            }
        } else {
            mode = ((GroupDetailActivity)mContext).hasMoreDate() ? LoadMoreView.Mode.LOADING : LoadMoreView.Mode.NO_MORE;
        }
        loadMoreView.configure(mode);
        return loadMoreView;
    }

    View inflateBulletinItem() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        ViewHolder viewHolder = new ViewHolder();
        View convertView = mInflater.inflate(R.layout.row_bulletin, null);
        viewHolder.creatorNameTv = (TextView) convertView.findViewById(R.id.tv_user_name);
        viewHolder.createdAtTv = (TextView) convertView.findViewById(R.id.tv_created_at);
        viewHolder.feedContentTv = (TextView) convertView.findViewById(R.id.tv_bulletin_text);
        viewHolder.channelNameTv = (TextView) convertView.findViewById(R.id.tv_group_name);
        viewHolder.commentsCountTv = (TextView) convertView.findViewById(R.id.tv_bulletin_comment_count);
        viewHolder.checkImgBtn = (ImageButton) convertView.findViewById(R.id.imgBtn_bulletin_check);
        viewHolder.crossImgBtn = (ImageButton) convertView.findViewById(R.id.imgBtn_bulletin_cross);
        viewHolder.commentImgBtn = (ImageButton) convertView.findViewById(R.id.imgBtn_bulletin_comment);
        viewHolder.checksCountTv = (TextView) convertView.findViewById(R.id.tv_bulletin_checks_count);
        viewHolder.crossesCountTv = (TextView) convertView.findViewById(R.id.tv_bulletin_crosses_count);
        convertView.setTag(viewHolder);
        return convertView;
    }

    View getBulletinView(View convertView, int position) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = inflateBulletinItem();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            if (viewHolder == null) {
                convertView = inflateBulletinItem();
                viewHolder = (ViewHolder) convertView.getTag();
            }
        }

        Bulletin bulletin = getItem(position);
        viewHolder.creatorNameTv.setText(bulletin.createdBy.name);
        viewHolder.feedContentTv.setText(bulletin.text);
        viewHolder.createdAtTv.setText("3 mins ago");
        viewHolder.channelNameTv.setText("#" + bulletin.group.name);
        viewHolder.commentsCountTv.setText("" + bulletin.comments_count);
        viewHolder.checksCountTv.setText("" + bulletin.checksCount);
        viewHolder.crossesCountTv.setText("" + bulletin.crossesCount);

        viewHolder.checkImgBtn.setColorFilter(mContext.getResources().getColor(R.color.icon_unselected_color));
        viewHolder.crossImgBtn.setColorFilter(mContext.getResources().getColor(R.color.icon_unselected_color));
        viewHolder.commentImgBtn.setColorFilter(mContext.getResources().getColor(R.color.icon_unselected_color));
        if (bulletin.stamp != null && bulletin.stamp.symbol != null) {
            switch (Stamp.Mark.valueOf(bulletin.stamp.symbol.toUpperCase())) {
                case CHECK:
                    viewHolder.checkImgBtn.setColorFilter(mContext.getResources().getColor(R.color.icon_selected_color));
                    break;
                case CROSS:
                    viewHolder.crossImgBtn.setColorFilter(mContext.getResources().getColor(R.color.icon_selected_color));
                    break;
                default:
            }
        }

        viewHolder.checkImgBtn.setOnClickListener(v -> {
            Log.i(TAG, "checkImgBtn clicked");
            EventBus.getDefault().post(new BulletinStampEvent(bulletin, Stamp.Mark.CHECK.toString().toLowerCase()));

        });

        viewHolder.crossImgBtn.setOnClickListener(v -> {
            Log.i(TAG, "crossImgBtn clicked");
            EventBus.getDefault().post(new BulletinStampEvent(bulletin, Stamp.Mark.CROSS.toString().toLowerCase()));

        });

        viewHolder.commentImgBtn.setOnClickListener( v -> {
            Log.i(TAG, "commentsImgBtn clicked");
            EventBus.getDefault().post(new BulletinCommentClickEvent(bulletin));
        });

        return convertView;
    }

    static class ViewHolder {
        public TextView creatorNameTv;
        public TextView feedContentTv;
        public TextView channelNameTv;
        public TextView createdAtTv;
        public ImageButton checkImgBtn;
        public TextView checksCountTv;
        public ImageButton commentImgBtn;
        public TextView commentsCountTv;
        public ImageButton crossImgBtn;
        public TextView crossesCountTv;
    }
}
