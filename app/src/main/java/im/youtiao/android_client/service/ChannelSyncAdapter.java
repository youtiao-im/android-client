package im.youtiao.android_client.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;
import org.codehaus.jackson.JsonParseException;

import java.io.IOException;
import java.util.List;

import im.youtiao.android_client.activity.LoginActivity;
import im.youtiao.android_client.api.ChannelServiceImpl;
import im.youtiao.android_client.dao.ChannelDAO;
import im.youtiao.android_client.exception.AndroidHacksException;
import im.youtiao.android_client.model.Channel;
import im.youtiao.android_client.provider.StatusFlag;

public class ChannelSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = ChannelSyncAdapter.class
            .getCanonicalName();
    private final ContentResolver mContentResolver;
    private AccountManager mAccountManager;
    private final static ChannelDAO mChannelDAO = ChannelDAO.getInstance();

    public ChannelSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "onPerformSync...");
        String authtoken = null;
        try {
            authtoken = mAccountManager.blockingGetAuthToken(account,
                    LoginActivity.PARAM_AUTHTOKEN_TYPE, true);
            Log.i(TAG, "After get authtoken");
            List<Channel> data = fetchData();
            syncRemoteDeleted(data);
            syncFromServerToLocalStorage(data);
            syncDirtyToServer(mChannelDAO.getDirtyList(mContentResolver));

        } catch (Exception e) {
            handleException(authtoken, e, syncResult);
        }
    }

    protected List<Channel> fetchData() throws AuthenticationException,
            AndroidHacksException, JsonParseException, IOException {
        List<Channel> list = ChannelServiceImpl.fetchChannels();
        return list;
    }

    protected void syncRemoteDeleted(List<Channel> remoteData) {
        Log.d(TAG, "Syncing remote deleted lists...");

        List<Channel> localClean = mChannelDAO.getCleanChannels(mContentResolver);
        for (Channel cleanChannel : localClean) {
            boolean exist = false;
            for (Channel remoteChannel : remoteData) {
                if (remoteChannel.equals(cleanChannel)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                Log.d(TAG, "Channel with id " + cleanChannel.getId()
                        + " has been deleted remotely.");
                mChannelDAO.forcedDeleteChannel(mContentResolver, cleanChannel.getId());
            }
        }
    }

    protected void syncDirtyToServer(List<Channel> dirtyList)
            throws AuthenticationException, IOException,
            AndroidHacksException {
        for (Channel channel : dirtyList) {
            Log.d(TAG, "Dirty list: " + channel);

            switch (channel.getStatus()) {
                case StatusFlag.ADD:
                    pushNewChannel(channel);
                    break;
                case StatusFlag.MOD:
                    throw new AndroidHacksException(
                            "Todo title modification is not supported");
                case StatusFlag.DELETE:
                    pushDeleteChannel(channel);
                    break;
                default:
                    throw new RuntimeException("Invalid status: "
                            + channel.getStatus());
            }
        }
    }

    private void pushNewChannel(Channel channel) throws AuthenticationException,
            IOException, AndroidHacksException {
        Channel serverChannel = ChannelServiceImpl.createChannel(channel.getName());
        mChannelDAO.clearAdd(mContentResolver, channel.getId(), serverChannel);
    }

    private void pushDeleteChannel(Channel channel)
            throws AuthenticationException, AndroidHacksException {
        ChannelServiceImpl.deleteChannel(channel.getId());
        mChannelDAO.deleteChannelForced(mContentResolver, channel.getId());
    }


    protected void syncFromServerToLocalStorage(List<Channel> data) {
        for (Channel channelFromServer : data) {
            Channel todoInDb = mChannelDAO.isTodoInDb(mContentResolver,
                    channelFromServer.getId());

            if (todoInDb == null) {
                Log.d(TAG, "Adding new channel from server: " + channelFromServer);
                mChannelDAO.addNewChannel(mContentResolver, channelFromServer,
                        StatusFlag.CLEAN);

            } else if (todoInDb.getStatus() == StatusFlag.CLEAN) {
                Log.d(TAG, "Modifying list from server: " + todoInDb);
                mChannelDAO.modifyChannelFromServer(mContentResolver, channelFromServer);
            }

        }
    }

    private void handleException(String authtoken, Exception e,
                                 SyncResult syncResult) {
        if (e instanceof AuthenticatorException) {
            syncResult.stats.numParseExceptions++;
            Log.e(TAG, "AuthenticatorException", e);
        } else if (e instanceof OperationCanceledException) {
            Log.e(TAG, "OperationCanceledExcepion", e);
        } else if (e instanceof IOException) {
            Log.e(TAG, "IOException", e);
            syncResult.stats.numIoExceptions++;
        } else if (e instanceof AuthenticationException) {
            mAccountManager.invalidateAuthToken(
                    LoginActivity.PARAM_ACCOUNT_TYPE, authtoken);
            // The numAuthExceptions require user intervention and are
            // considered hard errors.
            // We automatically get a new hash, so let's make SyncManager retry
            // automatically.
            syncResult.stats.numIoExceptions++;
            Log.e(TAG, "AuthenticationException", e);
        } else if (e instanceof ParseException) {
            syncResult.stats.numParseExceptions++;
            Log.e(TAG, "ParseException", e);
        } else if (e instanceof JsonParseException) {
            syncResult.stats.numParseExceptions++;
            Log.e(TAG, "JSONException", e);
        } else if (e instanceof AndroidHacksException) {
            Log.e(TAG, "AndroidHacksException", e);
        }
    }
}
