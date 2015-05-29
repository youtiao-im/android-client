package im.youtiao.android_client.util;

import roboguice.util.Ln;

public class Logger {
    public static void logThrowable(Throwable throwable) {
        Ln.d(throwable);
    }
}
