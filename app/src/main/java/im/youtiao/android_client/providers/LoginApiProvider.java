package im.youtiao.android_client.providers;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.squareup.okhttp.OkHttpClient;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import im.youtiao.android_client.AccountDescriptor;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.rest.JacksonConverter;
import im.youtiao.android_client.rest.OAuthApi;
import im.youtiao.android_client.rest.RemoteApiErrorHandler;
import im.youtiao.android_client.rest.RemoteEndPoint;
import im.youtiao.android_client.util.Log;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class LoginApiProvider implements Provider<OAuthApi> {
    static final String TAG = LoginApiProvider.class.getCanonicalName();
    @Inject
    private RestAdapter.Builder builder;

    @Inject private RemoteEndPoint endPoint;

    @Inject private JacksonConverter converter;

    @Inject
    Context mContext;

    @Override
    public OAuthApi get() {
        Log.e(TAG, "OAuthApi get() from:" + mContext.getClass().toString());
        RequestInterceptor interceptor = (request) -> {
            Log.e(TAG, "set Header:");
            request.addHeader("Accept", "application/vnd.youtiao.im+json; version=1");
            YTApplication application = (YTApplication)mContext.getApplicationContext();
            AccountDescriptor account = application.getCurrentAccount();
            Log.e(TAG, "current account:" + account.getEmail());
            if (account != null) {
                Log.e(TAG, "init oauth api with token");
                request.addHeader("Authorization", account.getTokenType() + " " + account.getToken());
            }
        };

        Executor executor = Executors.newSingleThreadExecutor();
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(5, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(5, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(5, TimeUnit.SECONDS);
        endPoint.setRemoteEndPoint(((YTApplication) mContext.getApplicationContext()).getYTHost());
        RestAdapter restAdapter = builder.setEndpoint(endPoint)
                .setExecutors(executor, executor)
                .setRequestInterceptor(interceptor)
                .setErrorHandler(new RemoteApiErrorHandler())
                .setConverter(new JacksonConverter(new ObjectMapper()))
                .setClient(new OkClient(okHttpClient))
                .build();
        return restAdapter.create(OAuthApi.class);
    }
}
