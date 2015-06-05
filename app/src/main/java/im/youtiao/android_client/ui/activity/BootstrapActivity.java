package im.youtiao.android_client.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.data.DaoHelper;
import im.youtiao.android_client.greendao.DaoSession;
import im.youtiao.android_client.greendao.User;
import im.youtiao.android_client.greendao.UserDao;
import im.youtiao.android_client.providers.RemoteApiFactory;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.Logger;
import roboguice.activity.RoboActivity;
import rx.schedulers.Schedulers;

public class BootstrapActivity extends RoboActivity {
    private static final String TAG = BootstrapActivity.class
            .getCanonicalName();
    private static final int NEW_ACCOUNT = 0;
    private static final int EXISTING_ACCOUNT = 1;
    private AccountManager mAccountManager;
    @Inject
    DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bootstrap);

        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager
                .getAccountsByType(LoginActivity.PARAM_ACCOUNT_TYPE);

        if (accounts.length == 0) {
            // There are no androidHacks accounts! We need to create one.
            Log.d(TAG, "No accounts found. Starting login...");
            final Intent intent = new Intent(this,
                    LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            startActivityForResult(intent, NEW_ACCOUNT);
        } else {
            // For now we assume that there's only one account.
            Account account = accounts[0];
            String password = mAccountManager.getPassword(accounts[0]);
            Log.d(TAG, "Using account with name " + accounts[0].name);
            if (password == null) {
                Log.d(TAG, "The password is empty, launching login");
                final Intent intent = new Intent(this,
                        LoginActivity.class);
                intent.putExtra(LoginActivity.PARAM_USER,
                        accounts[0].name);
                startActivityForResult(intent, EXISTING_ACCOUNT);
            } else {
                Log.d(TAG, "User and password found, no need for manual login");
                //get existing account authToken
                final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, LoginActivity.PARAM_AUTHTOKEN_TYPE, null, this, null, null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bundle bnd = future.getResult();
                            final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                            RemoteApiFactory.setApiToken(LoginActivity.AUTHTOKEN_TYPE, authtoken);
                            RemoteApi remoteApi = RemoteApiFactory.getApi();
                            remoteApi.getAuthenticatedUser().subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                                    .subscribe(res -> {
                                        UserDao userDao = daoSession.getUserDao();
                                        User user = new User();
                                        user.setServerId(res.id);
                                        user.setEmail(res.email);
                                        user.setServerId(res.id);
                                        user.setCreatedAt(res.createdAt);
                                        user.setUpdatedAt(res.updatedAt);
                                        user = DaoHelper.insertOrUpdate(daoSession, user);
                                        ((YTApplication) getApplication()).setCurrentUser(user);
                                    }, Logger::logThrowable);
                            // The user is already logged in. Go ahead!
                            startActivity(new Intent(BootstrapActivity.this, MainActivity.class));
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {

                        }
                    }
                }).start();


            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mAccountManager
                .getAccountsByType(LoginActivity.PARAM_ACCOUNT_TYPE).length > 0) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            finish();
        }
    }
}
