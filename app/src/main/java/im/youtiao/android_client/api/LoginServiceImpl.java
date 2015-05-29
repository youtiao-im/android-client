package im.youtiao.android_client.api;

import android.util.Log;

import im.youtiao.android_client.exception.AndroidHacksException;

public class LoginServiceImpl {

    private static final String TAG = LoginServiceImpl.class.getCanonicalName();

    public static boolean login(String username, String password)
            throws AndroidHacksException {
        String response = sendCredentials(username, password);
        return hasLoggedIn(response);
    }

    public static String sendCredentials(String username,
                                         String password) throws AndroidHacksException {
//        String fmt = AndroidHacksUrlFactory.getInstance().getLoginUrlFmt();
//        String url = String.format(fmt, username, password);
//        String ret = HttpHelper.getHttpResponseAsString(url, null);
//
//        return ret;
          return null;
    }

    public static boolean hasLoggedIn(String response) {
        Log.d(TAG, "response: " + response);
        return "{\"result\": \"ok\"}".equals(response);
    }

    public boolean logout() {
        return false;
    }
}
