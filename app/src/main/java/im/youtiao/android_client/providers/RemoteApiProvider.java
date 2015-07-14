package im.youtiao.android_client.providers;


import com.google.inject.Provider;
import im.youtiao.android_client.rest.RemoteApi;

public class RemoteApiProvider implements Provider<RemoteApi> {
    private static final String TAG = RemoteApiProvider.class.getCanonicalName();

    @Override
    public RemoteApi get() {
        return RemoteApiFactory.getApi();
    }
}
