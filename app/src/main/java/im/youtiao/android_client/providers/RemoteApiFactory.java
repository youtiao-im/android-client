package im.youtiao.android_client.providers;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import com.google.inject.Inject;
import com.squareup.okhttp.OkHttpClient;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.rest.JacksonConverter;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.rest.RemoteEndPoint;
import im.youtiao.android_client.rest.RemoteApiErrorHandler;
import im.youtiao.android_client.ui.activity.LoginActivity;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class RemoteApiFactory {
    private static final String TAG = RemoteApiFactory.class.getCanonicalName();
    private static RemoteEndPoint endPoint = new RemoteEndPoint();

    private static RestAdapter.Builder builder =  new RestAdapter.Builder();

    private static RemoteApi instance;

    public static RemoteApi getApi() {
        //Log.e(TAG, "get Api");
        if (instance == null) {
            Log.e(TAG, " RemoteApi is null");
        }
        return instance;
    }

    public static void setApiToken(Context context, String tokenType, String token) {
        RequestInterceptor interceptor = (request) -> {
            request.addHeader("Accept", "application/vnd.youtiao.im+json; version=1");
            request.addHeader("Authorization", tokenType + " " + token);
        };
        endPoint.setRemoteEndPoint(((YTApplication)context.getApplicationContext()).getApiHost());
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(5, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(5, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(5, TimeUnit.SECONDS);
        Executor executor = Executors.newSingleThreadExecutor();
        RestAdapter restAdapter = builder.setEndpoint(endPoint)
                .setExecutors(executor, executor)
                .setRequestInterceptor(interceptor)
                .setErrorHandler(new RemoteApiErrorHandler())
                .setConverter(new JacksonConverter(new ObjectMapper()))
                .setClient(new OkClient(okHttpClient))
                .build();
        instance = restAdapter.create(RemoteApi.class);
    }
}
