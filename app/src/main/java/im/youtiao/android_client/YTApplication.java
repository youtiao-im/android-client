package im.youtiao.android_client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Intent;

import im.youtiao.android_client.activity.LoginActivity;
import im.youtiao.android_client.activity.MainActivity;

public class YTApplication extends Application {

    public Account getCurrentAccount() {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager
                .getAccountsByType(LoginActivity.PARAM_ACCOUNT_TYPE);

        if (accounts.length > 0) {
            return accounts[0];
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return null;
        }
    }
}
