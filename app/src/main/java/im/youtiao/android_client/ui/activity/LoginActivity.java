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
import im.youtiao.android_client.model.Token;
import im.youtiao.android_client.providers.RemoteApiFactory;
import im.youtiao.android_client.rest.LoginApi;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import im.youtiao.android_client.util.Utility;
import im.youtiao.android_client.util.Log;
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
    @InjectView(R.id.edtTxt_password)
    private EditText mPasswordEdtTxt;
    private String mUsername;
    @InjectView(R.id.edtTxt_email)
    private EditText mUsernameEdtTxt;

    @InjectView(R.id.tv_forgot_password)
    TextView forgotPasswordTv;

    @Inject
    private LoginApi loginApi;

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
            mUsernameEdtTxt.setText(mAccount.getEmail());
            mPasswordEdtTxt.setText(mAccount.getPassword());
        }

        mPasswordEdtTxt = (EditText) findViewById(R.id.edtTxt_password);
        mPasswordEdtTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.btn_sign_in);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        forgotPasswordTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
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
        mUsernameEdtTxt.setError(null);
        mPasswordEdtTxt.setError(null);

        // Store values at the time of the login attempt.
        mUsername = mUsernameEdtTxt.getText().toString();
        mPassword = mPasswordEdtTxt.getText().toString();

        boolean cancel = false;
        // Check for a valid password, if the user entered one.
        String errorString = "";
        if (TextUtils.isEmpty(mPassword) || !Utility.isPasswordValid(mPassword)) {
            errorString = getString(R.string.error_invalid_password);
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mUsername)) {
            errorString = getString(R.string.error_user_name_required);
            cancel = true;
        } else if (!Utility.isEmailValid(mUsername)) {
            errorString = getString(R.string.error_invalid_email);
            cancel = true;
        }

        if (cancel) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(errorString)
                    .setPositiveButton(getString(R.string.tip_btn_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage(getString(R.string.progress_message_sign_in));
            progressDialog.show();
            loginApi.getTokenSync("password", mUsername, mPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(token -> {
                        progressDialog.dismiss();
                        finishLogin(token);
                    }, error -> {
                        progressDialog.dismiss();
                        NetworkExceptionHandler.handleThrowable(error, this);
                    });
        }
    }

    private void finishLogin(Token token) {
        String authToken = token.accessToken;
        String tokenType = token.tokenType;

        RemoteApiFactory.setApiToken(this, tokenType, authToken);
        RemoteApi remoteApi = RemoteApiFactory.getApi();
        remoteApi.getAuthenticatedUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    getApp().onPostSignIn(res, mPassword, tokenType, authToken);
                    Intent newIntent = getIntent();
                    LoginActivity.this.setResult(1, newIntent);
                    LoginActivity.this.finish();
                }, error -> NetworkExceptionHandler.handleThrowable(error, this));
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
}

