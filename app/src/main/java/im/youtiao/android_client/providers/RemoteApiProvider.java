package im.youtiao.android_client.providers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.IOException;

import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.ui.activity.LoginActivity;

public class RemoteApiProvider implements Provider<RemoteApi> {
    private static final String TAG = RemoteApiProvider.class.getCanonicalName();



    @Override
    public RemoteApi get() {
        Log.e(TAG, "Inject get RemoteApi");
        return RemoteApiFactory.getApi();
    }
}
