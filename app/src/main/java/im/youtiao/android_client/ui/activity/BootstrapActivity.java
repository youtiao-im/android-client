package im.youtiao.android_client.ui.activity;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import cn.jpush.android.api.JPushInterface;
import im.youtiao.android_client.AccountDescriptor;
import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.dao.LibraryProvider;
import im.youtiao.android_client.providers.DaoSessionFactory;
import im.youtiao.android_client.providers.RemoteApiFactory;
import im.youtiao.android_client.util.Log;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class BootstrapActivity extends RoboActivity {
    private static final String TAG = BootstrapActivity.class
            .getCanonicalName();
    public static final int NEW_ACCOUNT = 0;
    public static final int EXISTING_ACCOUNT = 1;
    private AccountManager mAccountManager;

    @InjectView(R.id.btn_login)
    Button loginBtn;

    @InjectView(R.id.btn_sign_up)
    Button signUpBtn;

    YTApplication getApp() {
        return (YTApplication) getApplication();
    }


    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onStop();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bootstrap);

        AccountDescriptor currentAccount = getApp().getCurrentAccount();
        if (currentAccount != null && currentAccount.getToken() != null) {
            RemoteApiFactory.setApiToken(this, currentAccount.getTokenType(), currentAccount.getToken());
            DaoSessionFactory.setDaoSession(this);
            LibraryProvider.daoSession = DaoSessionFactory.getDaoSession();
            startActivity(new Intent(BootstrapActivity.this, MainActivity.class));
            finish();
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putSerializable(LoginActivity.PARAM_ACCOUNT, currentAccount);
                final Intent intent = new Intent(BootstrapActivity.this, LoginActivity.class);
                intent.putExtras(data);
                startActivityForResult(intent, EXISTING_ACCOUNT);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(BootstrapActivity.this, RegisterActivity.class);
                startActivityForResult(intent, NEW_ACCOUNT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        Log.i(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            DaoSessionFactory.setDaoSession(this);
            LibraryProvider.daoSession = DaoSessionFactory.getDaoSession();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
