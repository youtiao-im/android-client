package im.youtiao.android_client;


import android.app.Application;

import com.google.inject.AbstractModule;

import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.providers.DaoSessionProvider;
import im.youtiao.android_client.providers.LoginApiProvider;
import im.youtiao.android_client.providers.RemoteApiProvider;
import im.youtiao.android_client.rest.LoginApi;
import im.youtiao.android_client.rest.RemoteApi;

public class YouTiaoModule extends AbstractModule {

    private Application application;

    public YouTiaoModule(Application application) {
        this.application = application;
    }

    @Override
    protected void configure() {
        bind(DaoSession.class).toProvider(DaoSessionProvider.class).asEagerSingleton();
        bind(LoginApi.class).toProvider(LoginApiProvider.class).asEagerSingleton();
        bind(RemoteApi.class).toProvider(RemoteApiProvider.class);
    }
}
