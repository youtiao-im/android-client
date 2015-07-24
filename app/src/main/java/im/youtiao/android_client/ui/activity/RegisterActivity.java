package im.youtiao.android_client.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.providers.RemoteApiFactory;
import im.youtiao.android_client.rest.OAuthApi;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.ui.widget.ProgressHUD;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import com.umeng.analytics.MobclickAgent;

public class RegisterActivity extends RoboActionBarActivity {
    @InjectView(R.id.edtTxt_email)
    private EditText mEmailEdtTxt;
    @InjectView(R.id.edtTxt_name)
    private EditText mNameEdtTxt;
    @InjectView(R.id.edtTxt_password)
    private EditText mPasswordEdtTxt;
    @Inject
    OAuthApi oAuthApi;

    YTApplication getApp() {
        return (YTApplication) getApplication();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPasswordEdtTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    register();
                    return true;
                }
                return false;
            }
        });
    }

    public void register() {
        // Store values at the time of the login attempt.
        String email = mEmailEdtTxt.getText().toString();
        String name = mNameEdtTxt.getText().toString();
        String password = mPasswordEdtTxt.getText().toString();

        boolean cancel = false;
        String errorString = "";
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            errorString = getString(R.string.error_invalid_password);
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            errorString = getString(R.string.error_user_name_required);
            cancel = true;
        } else if (!isEmailValid(email)) {
            errorString = getString(R.string.error_invalid_email);
            cancel = true;
        }

        if (cancel) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage(errorString)
                    .setPositiveButton(getString(R.string.tip_btn_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            remoteSignUp(email, name, password);
        }
    }

    void remoteSignUp(String email, String name, String password) {
//        ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
//        progressDialog.setMessage(getString(R.string.progress_message_sing_up));
//        progressDialog.show();
        ProgressHUD progressDialog = ProgressHUD.show(this, "", true, true, null);
        RemoteApiFactory.setApiToken(this, null, null);
        RemoteApi remoteApi = RemoteApiFactory.getApi();
        AppObservable.bindActivity(this, remoteApi.signUpUser(email, name, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    progressDialog.dismiss();
                    AppObservable.bindActivity(this, oAuthApi.getTokenSync("password", email, password))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(token -> {
                                String authToken = token.accessToken;
                                String tokenType = token.tokenType;
                                RemoteApiFactory.setApiToken(this, tokenType, authToken);
                                getApp().onPostSignIn(resp, password, tokenType, authToken);
                                Intent newIntent = getIntent();
                                RegisterActivity.this.setResult(1, newIntent);
                                RegisterActivity.this.finish();
                            }, error -> {
                                NetworkExceptionHandler.handleThrowable(error, this);
                            });
                }, error -> {
                    progressDialog.dismiss();
                    NetworkExceptionHandler.handleThrowable(error, this);
                });
    }



    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_sign_up:
                register();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
