package im.youtiao.android_client.ui.activity;

import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;

import im.youtiao.android_client.AccountDescriptor;
import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.providers.RemoteApiFactory;
import im.youtiao.android_client.rest.LoginApi;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.rest.responses.TokenResponse;
import im.youtiao.android_client.util.Logger;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends RoboActionBarActivity {

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private static final String TAG = LoginActivity.class
            .getCanonicalName();
    public static final String PARAM_ACCOUNT_TYPE = "im.youtiao.android_client";
    public static final String PARAM_AUTHTOKEN_TYPE = "im.youtiao.android_client.authtoken";
    public static final String AUTHTOKEN_TYPE = "bearer";

    public static final String PARAM_USER = "user";
    public static final String PARAM_ACCOUNT = "account";
    public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";

    private View mProgressView;
    private View mLoginFormView;

    private String mPassword;
    @InjectView(R.id.password)
    private EditText mPasswordEdit;
    private String mUsername;
    @InjectView(R.id.email)
    private EditText mUsernameEdit;

    @Inject
    private LoginApi loginApi;

    @Inject
    private DaoSession daoSession;

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    /**
     * Was the original caller asking for an entirely new account?
     */
    protected boolean mRequestNewAccount = false;
    private AccountDescriptor mAccount;


    YTApplication getApp() {
        return (YTApplication) getApplication();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        mAccount = (AccountDescriptor) intent.getSerializableExtra(PARAM_ACCOUNT);
        if (mAccount != null) {
            mUsernameEdit.setText(mAccount.getName());
            mPasswordEdit.setText(mAccount.getPassword());
        }

        mPasswordEdit = (EditText) findViewById(R.id.password);
        mPasswordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);
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

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mUsernameEdit.setError(null);
        mPasswordEdit.setError(null);

        // Store values at the time of the login attempt.
        mUsername = mUsernameEdit.getText().toString();
        mPassword = mPasswordEdit.getText().toString();

        boolean cancel = false;
        // Check for a valid password, if the user entered one.
        String errorString = "";
        if (TextUtils.isEmpty(mPassword) || !isPasswordValid(mPassword)) {
            errorString = getString(R.string.error_invalid_password);
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mUsername)) {
            errorString = getString(R.string.error_user_name_required);
            cancel = true;
        } else if (!isEmailValid(mUsername)) {
            errorString = getString(R.string.error_invalid_email);
            cancel = true;
        }

        if (cancel) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(errorString)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            showProgress(true);
            new AsyncTask<String, Void, Intent>() {

                @Override
                protected Intent doInBackground(String... params) {
                    Log.d("udinic", TAG + "> Started authenticating");
                    Bundle data = new Bundle();
                    try {
                        TokenResponse tokenResponse = loginApi.getToken("password", mUsername, mPassword);
                        data.putString(AccountManager.KEY_AUTHTOKEN, tokenResponse.accessToken);
                        data.putString(PARAM_AUTHTOKEN_TYPE, tokenResponse.tokenType);
                    } catch (Exception e) {
                        data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                    }

                    final Intent res = new Intent();
                    res.putExtras(data);
                    return res;
                }

                @Override
                protected void onPostExecute(Intent intent) {
                    if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                        showProgress(false);
                        Log.e(TAG, "onAuthenticationResult: failed to authenticate");
                        String errorString = getString(R.string.error_incorrect_password);
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(errorString)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    } else {
                        finishLogin(intent);
                    }
                }
            }.execute();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    private void finishLogin(Intent intent) {
        String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String tokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);

        RemoteApiFactory.setApiToken(tokenType, authToken);
        RemoteApi remoteApi = RemoteApiFactory.getApi();
        remoteApi.getAuthenticatedUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    showProgress(false);
                    getApp().onPostSignIn(res, mPassword, tokenType, authToken);
                    Intent newIntent = getIntent();
                    LoginActivity.this.setResult(BootstrapActivity.EXISTING_ACCOUNT, intent);
                    LoginActivity.this.finish();
                }, Logger::logThrowable);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

