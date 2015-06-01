package im.youtiao.android_client.providers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.google.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.rest.JacksonConverter;
import im.youtiao.android_client.rest.LoginApi;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.rest.RemoteApiErrorHandler;
import im.youtiao.android_client.rest.RemoteEndPoint;
import im.youtiao.android_client.ui.activity.LoginActivity;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

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
            Executor executor = Executors.newSingleThreadExecutor();
            RestAdapter restAdapter = builder.setEndpoint(endPoint)
                    .setExecutors(executor, executor)
                    .setRequestInterceptor(interceptor)
                    .setErrorHandler(new RemoteApiErrorHandler())
                    .setConverter(new JacksonConverter(new ObjectMapper()))
                    .build();
            instance = restAdapter.create(LoginApi.class);
        }
        return instance;
    }
}
