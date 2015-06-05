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
import im.youtiao.android_client.data.State;
import im.youtiao.android_client.greendao.DaoSession;
import im.youtiao.android_client.greendao.Feed;
import im.youtiao.android_client.greendao.FeedDao;
import im.youtiao.android_client.greendao.FeedHelper;
import im.youtiao.android_client.util.TimeWrap;

public class FeedCursorAdapter extends CursorAdapter {
    private static final String TAG = FeedCursorAdapter.class
            .getCanonicalName();
    private LayoutInflater mInflater;
    private Activity mActivity;
    private FeedAdapterDelegate feedAdapterDelegate;
    private DaoSession daoSession;

    @Inject
    public FeedCursorAdapter(Activity activity, DaoSession daoSession) {
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
        final View view = mInflater.inflate(R.layout.row_feed, parent,
                false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.creatorNameTextView = (TextView) view.findViewById(R.id.creator_name);
        viewHolder.createdAtTextView = (TextView) view.findViewById(R.id.created_at);
        viewHolder.feedContentTextView = (TextView) view.findViewById(R.id.feed_text);
        viewHolder.channelNameTextView = (TextView) view.findViewById(R.id.channel_name);
        viewHolder.stampButton = (ImageButton) view.findViewById(R.id.feed_stamp);
        viewHolder.starButton = (ImageButton) view.findViewById(R.id.feed_star);
        viewHolder.commentButton = (ImageButton) view.findViewById(R.id.feed_comment);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        final Feed feed = FeedHelper.fromCursor(cursor);
        feed.__setDaoSession(daoSession);
        //TODO: set feed item view

        String email = feed.getUser().getEmail();
        viewHolder.creatorNameTextView.setText(email.substring(0, email.indexOf("@")));
        viewHolder.feedContentTextView.setText(feed.getText());
        //viewHolder.createdAtTextView.setText(TimeWrap.wrapTimeDisplyValue(feed.getCreatedAt().getTime()));
        viewHolder.createdAtTextView.setText("3 mins ago");
        viewHolder.channelNameTextView.setText("#" + feed.getChannel().getName());
        if (feed.getIsStarred()) {
            viewHolder.starButton.setImageResource(R.mipmap.ic_feed_star_true);
        } else {
            viewHolder.starButton.setImageResource(R.mipmap.ic_feed_star_false);
        }
        viewHolder.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "starButton clicked");
                feedAdapterDelegate.toggleStar(v, feed);
            }
        });

        viewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "commentButton clicked");
                feedAdapterDelegate.extendComment();
            }
        });

        State.Mark mark = feed.getSymbol() != null ? State.Mark.valueOf(feed.getSymbol()) : State.Mark.DEFAULT;
        switch(mark) {
            case CHECK:
                viewHolder.stampButton.setImageResource(R.mipmap.ic_feed_stamp_check);
                break;
            case CROSS:
                viewHolder.stampButton.setImageResource(R.mipmap.ic_feed_stamp_cross);
                break;
            case QUESTION:
                viewHolder.stampButton.setImageResource(R.mipmap.ic_feed_stamp_question);
                break;
            default:
                viewHolder.stampButton.setImageResource(R.mipmap.ic_feed_stamp_default);
        }

        viewHolder.stampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "stampButton clicked");
                feedAdapterDelegate.clickStamp(v, feed);
            }
        });
    }

    public void setDelegate(FeedAdapterDelegate feedAdapterDelegate) {
        this.feedAdapterDelegate = feedAdapterDelegate;
    }

    static class ViewHolder {
        public TextView creatorNameTextView;
        public TextView feedContentTextView;
        public TextView channelNameTextView;
        public TextView createdAtTextView;
        public ImageButton starButton;
        public ImageButton commentButton;
        public ImageButton stampButton;
    }

    public static interface FeedAdapterDelegate {
        void toggleStar(View v, Feed feed);
        void clickStamp(View v, Feed feed);
        void extendComment();
    }
}
