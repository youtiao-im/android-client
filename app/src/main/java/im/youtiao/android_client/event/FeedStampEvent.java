package im.youtiao.android_client.event;


import android.view.View;

import im.youtiao.android_client.greendao.Feed;

public class FeedStampEvent {
    public Feed feed;
    public View view;

    public FeedStampEvent(View v, Feed feed) {
        this.view = v;
        this.feed = feed;
    }
}
