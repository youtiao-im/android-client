package im.youtiao.android_client.providers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.squareup.okhttp.OkHttpClient;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import im.youtiao.android_client.rest.JacksonConverter;
import im.youtiao.android_client.rest.LoginApi;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.rest.RemoteApiErrorHandler;
import im.youtiao.android_client.rest.RemoteEndPoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class LoginApiProvider implements Provider<LoginApi> {

    @Inject
    private RestAdapter.Builder builder;

    @Inject private RemoteEndPoint endPoint;

    @Inject private JacksonConverter converter;

    @Override
    public LoginApi get() {
        RequestInterceptor interceptor = request -> request.addHeader("Accept", "application/vnd.youtiao.im+json; version=1");
        Executor executor = Executors.newSingleThreadExecutor();
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(5, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(5, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(5, TimeUnit.SECONDS);
        RestAdapter restAdapter = builder.setEndpoint(endPoint)
                .setExecutors(executor, executor)
                .setRequestInterceptor(interceptor)
                .setErrorHandler(new RemoteApiErrorHandler())
                .setConverter(new JacksonConverter(new ObjectMapper()))
                .setClient(new OkClient(okHttpClient))
                .build();
        return restAdapter.create(LoginApi.class);
    }
}
