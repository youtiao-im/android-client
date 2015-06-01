package im.youtiao.android_client.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import im.youtiao.android_client.providers.LoginApiFactory;
import im.youtiao.android_client.rest.responses.TokenResponse;
import im.youtiao.android_client.ui.activity.LoginActivity;
import im.youtiao.android_client.api.LoginServiceImpl;
import im.youtiao.android_client.exception.AndroidHacksException;
import im.youtiao.android_client.content_providers.ChannelContentProvider;
import im.youtiao.android_client.content_providers.DatabaseHelper;
import rx.schedulers.Schedulers;

public class Authenticator extends AbstractAccountAuthenticator {
    private static final String TAG = Authenticator.class.getCanonicalName();
    public String[] authoritiesToSync = {ChannelContentProvider.AUTHORITY};
    private final Context mContext;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             String accountType, String authTokenType,
                             String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {

        final Intent intent = new Intent(mContext,
                LoginActivity.class);
        intent.putExtra(LoginActivity.PARAM_AUTHTOKEN_TYPE,
                authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;

    }

    @Override
    public Bundle confirmCredentials(
            AccountAuthenticatorResponse response, Account account,
            Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response,
                                 String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
                               Account account, String authTokenType, Bundle options)
            throws NetworkErrorException {

        if (!authTokenType
                .equals(LoginActivity.PARAM_AUTHTOKEN_TYPE)) {

            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE,
                    "invalid authTokenType");

            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = am.peekAuthToken(account, authTokenType);

        Log.d("udinic", TAG + "> peekAuthToken returned - " + authToken);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                try {
                    Log.d(TAG, " > re-authenticating with the existing password");
                    TokenResponse tokenResponse = LoginApiFactory.getLoginApi().getToken("password", account.name, password);
                    authToken = tokenResponse.accessToken;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(LoginActivity.PARAM_ACCOUNT_TYPE, account.type);
        intent.putExtra(LoginActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
        intent.putExtra(LoginActivity.PARAM_USER, account.name);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account, String[] features) throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle updateCredentials(
            AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle options)
            throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle getAccountRemovalAllowed(
            AccountAuthenticatorResponse response, Account account)
            throws NetworkErrorException {
        Bundle result = super.getAccountRemovalAllowed(response, account);

        if (result != null
                && result.containsKey(AccountManager.KEY_BOOLEAN_RESULT)
                && !result.containsKey(AccountManager.KEY_INTENT)) {
            boolean allowed = result
                    .getBoolean(AccountManager.KEY_BOOLEAN_RESULT);

            if (allowed) {
                for (int i = 0; i < authoritiesToSync.length; i++) {
                    ContentResolver.cancelSync(account, authoritiesToSync[i]);
                }

                mContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
            }
        }

        return result;
    }
}
