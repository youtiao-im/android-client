package im.youtiao.android_client.ui.activity;

import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.providers.RemoteApiFactory;
import im.youtiao.android_client.rest.LoginApi;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.rest.RemoteApiErrorHandler;
import im.youtiao.android_client.rest.responses.TokenResponse;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterActivity extends RoboActionBarActivity {
    @InjectView(R.id.edtTxt_email)
    private EditText mEmailEdtTxt;
    @InjectView(R.id.edtTxt_name)
    private EditText mNameEdtTxt;
    @InjectView(R.id.edtTxt_password)
    private EditText mPasswordEdtTxt;

    @InjectView(R.id.email_register_form)
    private View mProgressView;

    @InjectView(R.id.btn_sign_up)
    private Button signUpBtn;

    @Inject
    LoginApi loginApi;

    YTApplication getApp() {
        return (YTApplication) getApplication();
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

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
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
        ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage(getString(R.string.progress_message_sing_up));
        progressDialog.show();
        loginApi.signUpUser(email, name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    progressDialog.dismiss();
                    loginApi.getTokenSync("password", email, password)
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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
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
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
