package im.youtiao.android_client.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import im.youtiao.android_client.R;
import im.youtiao.java_sdk.core.Feed;

public class FeedAdapter extends ArrayAdapter<Feed> {

    public static interface FeedAdapterDelegate {
        void toggleStar(View v, Feed feed);

        void clickStamp(View v, Feed feed);

        void extendComment();
    }

    private int res;
    private LayoutInflater layoutInflater;
    private FeedAdapterDelegate feedAdapterDelegate;
    public static final String ADAPTER_TAG = "FeedAdapter";

    public FeedAdapter(Context context, int resource, List<Feed> objects) {
        super(context, resource, objects);
        this.res = resource;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(res, null);
            viewHolder = new ViewHolder();
            viewHolder.creatorNameTextView = (TextView) convertView.findViewById(R.id.creator_name);
            viewHolder.createdAtTextView = (TextView) convertView.findViewById(R.id.created_at);
            viewHolder.feedContentTextView = (TextView) convertView.findViewById(R.id.feed_text);
            viewHolder.stampButton = (ImageButton) convertView.findViewById(R.id.feed_stamp);
            viewHolder.starButton = (ImageButton) convertView.findViewById(R.id.feed_star);
            viewHolder.commentButton = (ImageButton) convertView.findViewById(R.id.feed_comment);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Feed feed = getItem(position);
        if (feed == null) {
            Log.d("FeedAdapter:", position + "");
        }

        //TODO: set feed item view
        viewHolder.creatorNameTextView.setText(feed.getCreatorId());
        viewHolder.feedContentTextView.setText(feed.getContent());
        viewHolder.createdAtTextView.setText("3 mins ago");

        if (feed.isStarred()) {
            viewHolder.starButton.setImageResource(R.mipmap.ic_feed_star_true);
        } else {
            viewHolder.starButton.setImageResource(R.mipmap.ic_feed_star_false);
        }

        viewHolder.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(ADAPTER_TAG, "starButton clicked");
                feedAdapterDelegate.toggleStar(v, feed);
            }
        });

        viewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(ADAPTER_TAG, "commentButton clicked");
                feedAdapterDelegate.extendComment();
            }
        });

        if (feed.isChecked()) {
            viewHolder.stampButton.setImageResource(R.mipmap.ic_feed_stamp_check);
        } else if (feed.isCrossed()) {
            viewHolder.stampButton.setImageResource(R.mipmap.ic_feed_stamp_cross);
        } else if (feed.isQuestioned()) {
            viewHolder.stampButton.setImageResource(R.mipmap.ic_feed_stamp_question);
        } else {
            viewHolder.stampButton.setImageResource(R.mipmap.ic_feed_stamp_default);
        }
        viewHolder.stampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(ADAPTER_TAG, "stampButton clicked");
                feedAdapterDelegate.clickStamp(v, feed);
            }
        });
        return convertView;
    }

    public void setDelegate(FeedAdapterDelegate feedAdapterDelegate) {
        this.feedAdapterDelegate = feedAdapterDelegate;
    }

    static class ViewHolder {
        public TextView creatorNameTextView;
        public TextView feedContentTextView;
        public TextView createdAtTextView;
        public ImageButton starButton;
        public ImageButton commentButton;
        public ImageButton stampButton;
    }
}
