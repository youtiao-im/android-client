package im.youtiao.android_client.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.content_providers.ChannelContentProvider;
import im.youtiao.android_client.greendao.DaoSession;
import im.youtiao.android_client.greendao.User;
import im.youtiao.android_client.greendao.UserDao;
import im.youtiao.android_client.providers.RemoteApiFactory;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.rest.responses.TokenResponse;
import im.youtiao.android_client.util.Logger;
import roboguice.activity.RoboAccountAuthenticatorActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends RoboAccountAuthenticatorActivity implements LoaderCallbacks<Cursor> {

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private static final String TAG = LoginActivity.class
            .getCanonicalName();
    public static final String PARAM_ACCOUNT_TYPE = "im.youtiao.android_client";
    public static final String PARAM_AUTHTOKEN_TYPE = "bearer";

    public static final String PARAM_USER = "user";
    public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";

    private View mProgressView;
    private View mLoginFormView;

    private String mPassword;
    private EditText mPasswordEdit;
    private String mUsername;
    private AutoCompleteTextView mUsernameEdit;
    private Button mSignInButton;
    private final Handler mHandler = new Handler();
    private Thread mAuthThread;
    private String mAuthToken;
    private String mAuthTokenType;
    private Boolean mConfirmCredentials = false;

    @Inject
    private RemoteApi remoteApi;

    @Inject
    private DaoSession daoSession;

    private AccountManager mAccountManager;

    /**
     * Was the original caller asking for an entirely new account?
     */
    protected boolean mRequestNewAccount = false;
    private String mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAccountManager = AccountManager.get(this);
        //checkMaximumNumberOfAccounts();

        final Intent intent = getIntent();

        mUser = intent.getStringExtra(PARAM_USER);
        mAuthTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
        mRequestNewAccount = mUser == null;
        mConfirmCredentials = intent.getBooleanExtra(
                PARAM_CONFIRMCREDENTIALS, false);
        Log.i(TAG, "    request new: " + mRequestNewAccount);


        // Set up the login form.
        mUsernameEdit = (AutoCompleteTextView) findViewById(R.id.email);
        if (mUser != null) {
            mUsernameEdit.setText(mUser);
        }
        populateAutoComplete();

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

        Button mEamilSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEamilSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(myIntent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthThread != null) {
            return;
        }

        // Reset errors.
        mUsernameEdit.setError(null);
        mPasswordEdit.setError(null);

        // Store values at the time of the login attempt.
        mUsername = mUsernameEdit.getText().toString();
        mPassword = mPasswordEdit.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mPassword) && !isPasswordValid(mPassword)) {
            mPasswordEdit.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEdit;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mUsername)) {
            mUsernameEdit.setError(getString(R.string.error_field_required));
            focusView = mUsernameEdit;
            cancel = true;
        } else if (!isEmailValid(mUsername)) {
            mUsernameEdit.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameEdit;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            remoteApi.getToken("password", mUsername, mPassword).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onAuthenticationSuccess, this::onAuthenticationFailed);
        }
    }

    private boolean isEmailValid(String email) {
        //return email.contains("@");
        return true;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    public void onAuthenticationSuccess(TokenResponse res) {
        showProgress(false);
        this.mAuthTokenType = res.tokenType;
        this.mAuthToken = res.accessToken;
        if (!mConfirmCredentials) {
            finishLogin();
        }
    }

    public void onAuthenticationFailed(Throwable throwable) {
        Log.e(TAG, "onAuthenticationResult: failed to authenticate");
        mPasswordEdit.setError(getString(R.string.error_incorrect_password));
        mPasswordEdit.requestFocus();
    }

    private void finishLogin() {
        final Account account = new Account(mUsername, PARAM_ACCOUNT_TYPE);
        mAccountManager.setAuthToken(account, mAuthTokenType, mAuthToken);
        if (mRequestNewAccount) {
            mAccountManager.addAccountExplicitly(account, mPassword, null);
            ContentResolver.setSyncAutomatically(account, ChannelContentProvider.AUTHORITY, true);
        } else {
            mAccountManager.setPassword(account, mPassword);
        }

        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, PARAM_ACCOUNT_TYPE);

        if (mAuthTokenType != null
                && mAuthTokenType.equals(PARAM_AUTHTOKEN_TYPE)) {
            intent.putExtra(AccountManager.KEY_AUTHTOKEN, mAuthToken);
            RemoteApiFactory.setApiToken(mAuthTokenType, mAuthToken);
            remoteApi = RemoteApiFactory.getApi();
            remoteApi.getAuthenticatedUser().subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                    .subscribe(res -> {
                        UserDao userDao = daoSession.getUserDao();
                        User user = new User();
                        user.setEmail(res.email);
                        user.setServerId(res.id);
                        user.setCreatedAt(res.createdAt);
                        user.setUpdatedAt(res.updatedAt);
                        userDao.insertOrReplace(user);
                        ((YTApplication) getApplication()).setCurrentUser(user);
                    }, Logger::logThrowable);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUsernameEdit.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

    /*
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                YTRequestConfig config = new YTRequestConfig("test");
                YTHost host = new YTHost("192.168.200.183:3000");
                YTClient client = new YTClient(config, null, host);
                client.signInUser(mEmail, mPassword);
            } catch (YTException e) {
                e.printStackTrace();
                //TODO: show exception
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(myIntent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    */
}

