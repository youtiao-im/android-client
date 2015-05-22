package im.youtiao.android_client.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ChannelSyncService extends Service {
    private static final String TAG = ChannelSyncService.class
            .getCanonicalName();
    private static final Object sSyncAdapterLock = new Object();
    private static ChannelSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.e(TAG, "ChannelSyncService OnCreate");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new ChannelSyncAdapter(getApplicationContext(),
                        true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "ChannelSyncService OnBind");
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
