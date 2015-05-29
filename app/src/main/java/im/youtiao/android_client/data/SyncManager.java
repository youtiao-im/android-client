package im.youtiao.android_client.data;


import android.content.Context;
import android.util.Log;

import com.google.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

import im.youtiao.android_client.greendao.Channel;
import im.youtiao.android_client.greendao.ChannelDao;
import im.youtiao.android_client.greendao.ChannelHelper;
import im.youtiao.android_client.greendao.DaoSession;
import im.youtiao.android_client.greendao.User;
import im.youtiao.android_client.greendao.UserDao;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.rest.responses.ChannelResponse;
import im.youtiao.android_client.rest.responses.UserChannelMembershipResponse;
import im.youtiao.android_client.util.Logger;
import roboguice.util.Ln;
import rx.Observable;
import rx.schedulers.Schedulers;

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

    public void startChannelsSync() {
        getChannels(STARTING_OFFSET, LIMIT).subscribe(this::processPlaylists, Logger::logThrowable);
    }

    private Observable<List<UserChannelMembershipResponse>> getChannels(int offset, int limit) {
        return api.getUserChannelMemberships().observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    }

    private void processPlaylists(List<UserChannelMembershipResponse> data) {
        Log.i(TAG, "size=" + data.size());
        ChannelDao channelDao = daoSession.getChannelDao();
        UserDao userDao = daoSession.getUserDao();
        daoSession.runInTx(() -> {
            for (UserChannelMembershipResponse item : data) {
                String role = item.role;
                ChannelResponse cr = item.channelResponse;

                User user = new User(null, cr.createdBy.id, cr.createdBy.email, cr.createdBy.createdAt, cr.createdBy.updatedAt);

                long userId = userDao.insertOrReplace(user);

                Channel channel = new Channel(null,cr.id, cr.name, role, cr.membershipsCount, cr.createdAt, cr.updatedAt, userId);
                channelDao.insertOrReplace(channel);
            }
        });
        mContext.getContentResolver().notifyChange(ChannelHelper.CONTENT_URI, null);
    }
}
