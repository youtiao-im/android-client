package im.youtiao.android_client.adapter;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.DaoSession;

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
        viewHolder.creatorNameTv = (TextView) view.findViewById(R.id.tv_creator_name);
        viewHolder.createdAtTv = (TextView) view.findViewById(R.id.tv_created_at);
        viewHolder.feedContentTv = (TextView) view.findViewById(R.id.tv_feed_text);
        viewHolder.channelNameTv = (TextView) view.findViewById(R.id.tv_channel_name);
        viewHolder.commentCountTv = (TextView) view.findViewById(R.id.tv_feed_comment_count);
        viewHolder.stampImgBtn = (ImageButton) view.findViewById(R.id.imgBtn_feed_stamp);
        viewHolder.starImgBtn = (ImageButton) view.findViewById(R.id.imgBtn_feed_star);
        viewHolder.commentImgBtn = (ImageButton) view.findViewById(R.id.imgBtn_feed_comment);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
//        Log.i(TAG, "bindView: " + cursor.getPosition());
//        final ViewHolder viewHolder = (ViewHolder) view.getTag();
//        final Feed feed = FeedHelper.fromCursor(cursor);
//        feed.__setDaoSession(daoSession);
//        //TODO: set feed item view
//
//        String email = feed.getUser().getEmail();
//        viewHolder.creatorNameTv.setText(email.substring(0, email.indexOf("@")));
//        viewHolder.feedContentTv.setText(feed.getText());
//        //viewHolder.createdAtTv.setText(TimeWrap.wrapTimeDisplyValue(feed.getCreatedAt().getTime()));
//        viewHolder.createdAtTv.setText("3 mins ago");
//        viewHolder.channelNameTv.setText("#" + feed.getChannel().getName());
//        viewHolder.commentCountTv.setText("" + feed.getComments().size());
//        if (feed.getIsStarred()) {
//            viewHolder.starImgBtn.setImageResource(R.mipmap.ic_feed_star_true);
//        } else {
//            viewHolder.starImgBtn.setImageResource(R.mipmap.ic_feed_star_false);
//        }
//        viewHolder.starImgBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "starImgBtn clicked");
//                EventBus.getDefault().post(new FeedStarEvent(feed));
//            }
//        });
//
//        viewHolder.commentImgBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "commentImgBtn clicked");
//            }
//        });
//
//        State.Mark mark = feed.getSymbol() != null ? State.Mark.valueOf(feed.getSymbol()) : State.Mark.DEFAULT;
//        switch(mark) {
//            case CHECK:
//                viewHolder.stampImgBtn.setImageResource(R.mipmap.ic_feed_stamp_check);
//                break;
//            case CROSS:
//                viewHolder.stampImgBtn.setImageResource(R.mipmap.ic_feed_stamp_cross);
//                break;
//            case QUESTION:
//                viewHolder.stampImgBtn.setImageResource(R.mipmap.ic_feed_stamp_question);
//                break;
//            default:
//                viewHolder.stampImgBtn.setImageResource(R.mipmap.ic_feed_stamp_default);
//        }
//
//        viewHolder.stampImgBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "stampImgBtn clicked");
//                EventBus.getDefault().post(new FeedStampEvent(v, feed));
//            }
//        });
    }

    static class ViewHolder {
        public TextView creatorNameTv;
        public TextView feedContentTv;
        public TextView channelNameTv;
        public TextView createdAtTv;
        public TextView commentCountTv;
        public ImageButton starImgBtn;
        public ImageButton commentImgBtn;
        public ImageButton stampImgBtn;
    }


}
