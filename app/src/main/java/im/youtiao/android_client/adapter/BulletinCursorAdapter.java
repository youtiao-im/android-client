package im.youtiao.android_client.adapter;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;

import de.greenrobot.event.EventBus;
import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.BulletinHelper;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.event.BulletinCommentClickEvent;
import im.youtiao.android_client.event.BulletinStampEvent;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Stamp;
import im.youtiao.android_client.wrap.BulletinWrap;

public class BulletinCursorAdapter extends CursorAdapter {
    private static final String TAG = BulletinCursorAdapter.class
            .getCanonicalName();
    private LayoutInflater mInflater;
    private Activity mActivity;
    private DaoSession daoSession;

    @Inject
    public BulletinCursorAdapter(Activity activity, DaoSession daoSession) {
        super(activity, null, false);
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        this.daoSession = daoSession;
    }

    @Override
    public void changeCursor(Cursor cursor) {
        Log.i(TAG, "changeCursor");
        super.changeCursor(cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.i(TAG, "newView");
        final View view = mInflater.inflate(R.layout.row_bulletin, parent,
                false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.creatorAvatarIv = (ImageView) view.findViewById(R.id.iv_creator_avatar);
        viewHolder.creatorNameTv = (TextView) view.findViewById(R.id.tv_user_name);
        viewHolder.createdAtTv = (TextView) view.findViewById(R.id.tv_created_at);
        viewHolder.feedContentTv = (TextView) view.findViewById(R.id.tv_bulletin_text);
        viewHolder.channelNameTv = (TextView) view.findViewById(R.id.tv_group_name);
        viewHolder.commentsCountTv = (TextView) view.findViewById(R.id.tv_bulletin_comment_count);
        viewHolder.checkImgBtn = (ImageButton) view.findViewById(R.id.imgBtn_bulletin_check);
        viewHolder.crossImgBtn = (ImageButton) view.findViewById(R.id.imgBtn_bulletin_cross);
        viewHolder.commentImgBtn = (ImageButton) view.findViewById(R.id.imgBtn_bulletin_comment);
        viewHolder.checksCountTv = (TextView) view.findViewById(R.id.tv_bulletin_checks_count);
        viewHolder.crossesCountTv = (TextView) view.findViewById(R.id.tv_bulletin_crosses_count);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.i(TAG, "bindView:" + this.getCount());
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        final Bulletin bulletin = BulletinWrap.wrap(BulletinHelper.fromCursor(cursor));
        viewHolder.creatorAvatarIv.setImageResource(R.mipmap.user_avatar);
        viewHolder.creatorNameTv.setText(bulletin.createdBy.name);
        viewHolder.feedContentTv.setText(bulletin.text);
        viewHolder.createdAtTv.setText("3 mins ago");
        viewHolder.channelNameTv.setText("#" + bulletin.group.name);
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
    }

    static class ViewHolder {
        public ImageView creatorAvatarIv;
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
