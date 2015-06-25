package im.youtiao.android_client.util;

import android.util.Log;

import roboguice.util.Ln;

public class Logger {
    private static final String TAG = Logger.class.getCanonicalName();
    public static void logThrowable(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
        Ln.d(throwable);
    }
}
