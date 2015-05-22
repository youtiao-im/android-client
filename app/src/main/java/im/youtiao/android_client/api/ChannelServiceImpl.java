package im.youtiao.android_client.api;


import android.util.Log;

import org.apache.http.auth.AuthenticationException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import im.youtiao.android_client.exception.AndroidHacksException;
import im.youtiao.android_client.model.Channel;
import im.youtiao.android_client.net.HttpHelper;

public class ChannelServiceImpl {
    private static final String TAG = ChannelServiceImpl.class.getCanonicalName();

    private static Type getToken() {
        return new TypeToken<List<Channel>>() {
        }.getType();
    }

    public static List<Channel> fetchChannels() throws AuthenticationException,
            JsonParseException, IOException, AndroidHacksException {
        Log.d(TAG, "Fetching Todo's...");
        String url = AndroidHacksUrlFactory.getInstance().getTodoUrl();
        String response = HttpHelper.getHttpResponseAsString(url, null);
        Gson gson = new Gson();
        List<Channel> lists = gson.fromJson(response, getToken());
        return lists;
    }

    public static Channel createChannel(String title)
            throws AuthenticationException, JsonParseException, IOException,
            AndroidHacksException {
        Log.d(TAG, "Creating Todo list " + title);
        String urlFmt = AndroidHacksUrlFactory.getInstance()
                .getTodoAddUrlFmt();
        String url = String.format(urlFmt, title);
        String response = HttpHelper.getHttpResponseAsString(url, null);

        Gson gson = new Gson();
        List<Channel> lists = gson.fromJson(response, getToken());

        if (lists.size() != 1) {
            throw new AndroidHacksException("Error creating Todo " + title);
        }

        return lists.get(0);
    }

    public static void deleteChannel(long id)
            throws AuthenticationException, AndroidHacksException {
        Log.d(TAG, "Deleting Todo with id " + id);
        String urlFmt = AndroidHacksUrlFactory.getInstance()
                .getTodoDeleteUrlFmt();
        String url = String.format(urlFmt, id);
        HttpHelper.getHttpResponseAsString(url, null);
    }
}

