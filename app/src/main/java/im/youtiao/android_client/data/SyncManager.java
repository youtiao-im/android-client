package im.youtiao.android_client.data;


import android.content.Context;

import com.google.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;

import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.rest.RemoteApi;

public class SyncManager {
    private static final String TAG = SyncManager.class.getCanonicalName();

    private static final int STARTING_OFFSET = 0;
    private static final int LIMIT = 10;
    private final DaoSession daoSession;
    private RemoteApi api;
    private ObjectMapper mapper;
    @Inject private Context mContext;

    @Inject
    public SyncManager(RemoteApi api, ObjectMapper mapper, DaoSession daoSession) {
        this.api = api;
        this.mapper = mapper;
        this.daoSession = daoSession;
    }

    /*
    public void startSync(){
        Log.i(TAG, "startSync");
        startChannelsSync();
        startFeedsSync();
    }

    public void startFeedSyncForChannel(Channel channel, int page, int perPage) {
        getChannelFeeds(channel.getServerId(), page, perPage).subscribe(resp -> processFeeds(resp, channel), Logger::logThrowable);
    }

    public void startCommentSyncForFeed(Feed feed) {
        getComments(feed.getServerId(),STARTING_OFFSET, LIMIT)
                .subscribe(resp -> processComments(resp, feed), Logger::logThrowable);
    }

    private Observable<List<CommentResponse>> getComments(String feedId, int offset, int limit) {
        return api.getFeedComments(feedId).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    }

    public void processComments(List<CommentResponse> data, Feed feed) {
        Log.i(TAG, "comment size=" + data.size());
        CommentDao commentDao = daoSession.getCommentDao();
        daoSession.runInTx(() -> {
            for (CommentResponse item : data) {
                UserResponse uRes = item.createdBy;
                User user = new User(null, uRes.id, uRes.email, uRes.createdAt, uRes.updatedAt);
                user = DaoHelper.insertOrUpdate(daoSession, user);
                Comment comment = new Comment();
                comment.setServerId(item.id);
                comment.setText(item.text);
                comment.setCreatedAt(item.createdAt);
                comment.setUpdatedAt(item.updatedAt);
                comment.setCreatedBy(user.getId());
                comment.setFeedId(feed.getId());
                DaoHelper.insertOrUpdate(daoSession, comment);
            }
        });
        Log.i(TAG, "commentDao size =" + commentDao.count());
        mContext.getContentResolver().notifyChange(CommentHelper.CONTENT_URI, null);

    }

    public void startChannelsSync() {
        Log.i(TAG, "startChannelsSync");
        getChannels(STARTING_OFFSET, LIMIT).subscribe(this::processChannels, Logger::logThrowable);
    }

    private Observable<List<UserChannelMembershipResponse>> getChannels(int page, int perPage) {
        return api.getUserChannelMemberships(page, perPage).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    }

    private void processChannels(List<UserChannelMembershipResponse> data) {
        Log.i(TAG, "channel size=" + data.size());
        ChannelDao channelDao = daoSession.getChannelDao();
        UserDao userDao = daoSession.getUserDao();
        daoSession.runInTx(() -> {
            for (UserChannelMembershipResponse item : data) {
                String role = item.role;
                ChannelResponse cr = item.channelResponse;

                UserResponse uRes = cr.createdBy;
                User user = new User(null, uRes.id, uRes.email, uRes.createdAt, uRes.updatedAt);
                user = DaoHelper.insertOrUpdate(daoSession, user);

                Channel channel = new Channel(null, cr.id, cr.name, role, cr.membershipsCount, cr.createdAt, cr.updatedAt, user.getId());
                channel = DaoHelper.insertOrUpdate(daoSession, channel);
            }
        });
        Log.i(TAG, "channelDao size =" + channelDao.count());
        mContext.getContentResolver().notifyChange(ChannelHelper.CONTENT_URI, null);
    }

    public void startFeedsSync() {
        Log.i(TAG, "startFeedsSync");
        Observable.create((Subscriber<? super List<Channel>> subscriber) -> {
            ChannelDao channelDao = daoSession.getChannelDao();
            final List<Channel> channels = channelDao.loadAll();
            subscriber.onNext(channels);
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(this::startFeedsForChannels, Logger::logThrowable);
    }

    private void startFeedsForChannels(List<Channel> list) {
        Observable.from(list)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(i -> getChannelFeeds(i.getServerId(), STARTING_OFFSET, LIMIT)
                                .subscribe(resp -> processFeeds(resp, i), Logger::logThrowable), Logger::logThrowable);
    }

    private Observable<List<FeedResponse>> getChannelFeeds(String channelId, int offset,
                                                                int limit) {
        return api.getChannelFeeds(channelId, offset, limit)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io());
    }

    private void processFeeds(List<FeedResponse> data, Channel channel) {
        Log.i(TAG, "feed size=" + data.size() + ", channelName=" + channel.getName());
        ChannelDao channelDao = daoSession.getChannelDao();
        FeedDao feedDao = daoSession.getFeedDao();
        UserDao userDao = daoSession.getUserDao();
        daoSession.runInTx(() -> {
            Log.i(TAG, "After Sleep");
            for (FeedResponse item : data) {
                UserResponse uRes = item.createdBy;
                User user = new User(null, uRes.id, uRes.email, uRes.createdAt, uRes.updatedAt);
                user = DaoHelper.insertOrUpdate(daoSession, user);
                Feed feed = new Feed();
                feed.setServerId(item.id);
                feed.setCreatedAt(item.createdAt);
                feed.setChannelId(channel.getId());
                feed.setIsStarred(item.starResponse != null);
                feed.setSymbol(item.markResponse != null ? item.markResponse.symbol : null);
                feed.setText(item.text);
                feed.setCreatedBy(user.getId());
                DaoHelper.insertOrUpdate(daoSession, feed);
            }
        });
        Log.i(TAG, "feedDao size =" + feedDao.count());
        mContext.getContentResolver().notifyChange(FeedHelper.CONTENT_URI, null);
    }
    */
}
