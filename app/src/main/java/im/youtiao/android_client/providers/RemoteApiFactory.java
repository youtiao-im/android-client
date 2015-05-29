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
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.rest.RemoteEndPoint;
import im.youtiao.android_client.rest.RemoteApiErrorHandler;
import im.youtiao.android_client.ui.activity.LoginActivity;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class RemoteApiFactory {
    private static RemoteEndPoint endPoint = new RemoteEndPoint();

    private static RestAdapter.Builder builder =  new RestAdapter.Builder();

    private static RemoteApi instance;

    @Inject
    static Context  mContext;

    public static RemoteApi getApi() {
        if (instance == null) {
            Account account = ((YTApplication)mContext.getApplicationContext()).getCurrentAccount();
            AccountManager accountManager = AccountManager.get(mContext);
            String authtoken =  accountManager.peekAuthToken(account, LoginActivity.PARAM_AUTHTOKEN_TYPE);
            String authorization = LoginActivity.PARAM_AUTHTOKEN_TYPE + " " + authtoken;
            RequestInterceptor interceptor = request -> {
                request.addHeader("Accept", "application/vnd.youtiao.im+json; version=1");
                request.addHeader("Authorization", authorization);
                //request.addHeader("Authorization", "bearer e2ebd31cbe7ce72fd2e3c9ac49746ef922b90bc30cf88fcc7abc7e453cf6f7a6");
            };
            Executor executor = Executors.newSingleThreadExecutor();
            RestAdapter restAdapter = builder.setEndpoint(endPoint)
                    .setExecutors(executor, executor)
                    .setRequestInterceptor(interceptor)
                    .setErrorHandler(new RemoteApiErrorHandler())
                    .setConverter(new JacksonConverter(new ObjectMapper()))
                    .build();
            instance = restAdapter.create(RemoteApi.class);
        }
        return instance;
    }

    public static void setApiToken(String tokenType, String token) {
        RequestInterceptor interceptor = (request) -> {
            request.addHeader("Accept", "application/vnd.youtiao.im+json; version=1");
            request.addHeader("Authorization", tokenType + " " + token);
            //request.addHeader("Authorization", "bearer e2ebd31cbe7ce72fd2e3c9ac49746ef922b90bc30cf88fcc7abc7e453cf6f7a6");
        };
        Executor executor = Executors.newSingleThreadExecutor();
        RestAdapter restAdapter = builder.setEndpoint(endPoint)
                .setExecutors(executor, executor)
                .setRequestInterceptor(interceptor)
                .setErrorHandler(new RemoteApiErrorHandler())
                .setConverter(new JacksonConverter(new ObjectMapper()))
                .build();
        instance = restAdapter.create(RemoteApi.class);
    }
}
