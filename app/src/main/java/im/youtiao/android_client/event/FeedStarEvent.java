package im.youtiao.android_client.event;


import im.youtiao.android_client.greendao.Feed;

public class FeedStarEvent {
    public Feed feed;

    public FeedStarEvent(Feed feed) {
        this.feed = feed;
    }
}
