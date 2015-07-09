package im.youtiao.android_client.providers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.google.inject.Inject;
import com.squareup.okhttp.OkHttpClient;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.rest.JacksonConverter;
import im.youtiao.android_client.rest.LoginApi;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.rest.RemoteApiErrorHandler;
import im.youtiao.android_client.rest.RemoteEndPoint;
import im.youtiao.android_client.ui.activity.LoginActivity;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class LoginApiFactory {
    private static RemoteEndPoint endPoint = new RemoteEndPoint();

    private static RestAdapter.Builder builder = new RestAdapter.Builder();

    private static LoginApi instance;

    @Inject
    static Context mContext;

    public static LoginApi getLoginApi() {
        if (instance == null) {
            RequestInterceptor interceptor = request -> {
                request.addHeader("Accept", "application/vnd.youtiao.im+json; version=1");
            };
            final OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setReadTimeout(5, TimeUnit.SECONDS);
            okHttpClient.setConnectTimeout(2, TimeUnit.SECONDS);
            Executor executor = Executors.newSingleThreadExecutor();
            RestAdapter restAdapter = builder.setEndpoint(endPoint)
                    .setExecutors(executor, executor)
                    .setRequestInterceptor(interceptor)
                    .setErrorHandler(new RemoteApiErrorHandler())
                    .setConverter(new JacksonConverter(new ObjectMapper()))
                    .setClient(new OkClient(okHttpClient))
                    .build();
            instance = restAdapter.create(LoginApi.class);
        }
        return instance;
    }
}
