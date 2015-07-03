package im.youtiao.android_client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.dao.LibraryProvider;
import im.youtiao.android_client.model.User;
import im.youtiao.android_client.providers.DaoSessionFactory;
import im.youtiao.android_client.providers.DaoSessionProvider;
import im.youtiao.android_client.ui.activity.LoginActivity;
import im.youtiao.android_client.ui.activity.MainActivity;


public class YTApplication extends Application {
    private SharedPreferences mPreferences;
    private User currentUser;

    private ArrayList<AccountDescriptor> mAccounts;
    private Integer mCurrentAccountIndex;

    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true); 	// just for debug, close it when startup
        JPushInterface.init(this);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mAccounts = new ArrayList<AccountDescriptor>();
        updateAccounts();
    }

    void updateAccounts() {
        mAccounts.clear();
        String currentAccountId = mPreferences.getString(
                SharedPreferencesConstants.CURRENT_ACCOUNT_ID, null);
        String accountIndices = mPreferences.getString(
                SharedPreferencesConstants.ACCOUNT_INDICES, null);
        if (accountIndices != null) {
            try {
                JSONArray jsonArray = new JSONArray(accountIndices);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String id = jsonArray.getString(i);

                    String key = getAccountDescriptorKey(id);
                    String jsonAsString = mPreferences.getString(key, null);
                    if (jsonAsString != null) {
                        AccountDescriptor account = new AccountDescriptor(jsonAsString);
                        mAccounts.add(account);
                        if (currentAccountId != null
                                && account.getId().equalsIgnoreCase(currentAccountId)) {
                            mCurrentAccountIndex = i;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (mCurrentAccountIndex == null && mAccounts.size() > 0) {
            mCurrentAccountIndex = 0;
        }
    }

    public void onPostSignIn(User user, String password, String tokenType, String token) {
        if (user != null) {
            try {
                final SharedPreferences.Editor edit = mPreferences.edit();
                String userIdAsString = user.id;

                AccountDescriptor account = new AccountDescriptor(user, password, tokenType, token);
                edit.putString(getAccountDescriptorKey(user.id),
                        account.toString());

                String accountIndices = mPreferences.getString(
                        SharedPreferencesConstants.ACCOUNT_INDICES, null);
                JSONArray jsonArray;

                if (accountIndices == null) {
                    jsonArray = new JSONArray();
                    jsonArray.put(0, user.id);
                    mAccounts.add(account);
                } else {
                    jsonArray = new JSONArray(accountIndices);
                    boolean exists = false;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String c = jsonArray.getString(i);
                        if (c.equalsIgnoreCase(userIdAsString)) {
                            exists = true;
                            mAccounts.set(i, account);
                            break;
                        }
                    }
                    if (!exists) {
                        jsonArray.put(userIdAsString);
                        mAccounts.add(account);
                    }
                }
                accountIndices = jsonArray.toString();
                edit.putString(SharedPreferencesConstants.ACCOUNT_INDICES,
                        accountIndices);
                edit.commit();
                setCurrentAccount(user.id);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        updateAccounts();
    }

    public void signOutAccount(String accountId) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        String accountIndices = mPreferences.getString(SharedPreferencesConstants.ACCOUNT_INDICES, null);
        if (accountIndices != null) {
            try {
                JSONArray jsonArray = new JSONArray(accountIndices);
                JSONArray newIndicies = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String id = jsonArray.getString(i);

                    String key = getAccountDescriptorKey(id);
                    String jsonAsString = mPreferences.getString(key, null);
                    if (jsonAsString != null) {
                        AccountDescriptor account = new AccountDescriptor(jsonAsString);

                        if (!account.getId().equals(accountId)) {
                            newIndicies.put(id);
                        } else {
                            account.setToken(null);
                            edit.putString(getAccountDescriptorKey(account.getId()),
                                    account.toString());
                            newIndicies.put(id);
                        }
                    }
                }

                accountIndices = newIndicies.toString();
                edit.putString(SharedPreferencesConstants.ACCOUNT_INDICES,
                        accountIndices);
                edit.remove(SharedPreferencesConstants.CURRENT_ACCOUNT_ID);   //sign out
                edit.commit();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        updateAccounts();
    }

    private Integer getAccountIndexById(String id) {
        if (id == null) {
            return null;
        }
        for (int i = 0; i < mAccounts.size(); i++) {
            if (mAccounts.get(i).getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return null;
    }

    public void setCurrentAccount(String id) {
        mCurrentAccountIndex = getAccountIndexById(id);
        if (mCurrentAccountIndex == null) {
            return;
        } else {
            AccountDescriptor account = mAccounts.get(mCurrentAccountIndex);
            if (account != null) {
                //TODO: do something?
                final SharedPreferences.Editor edit = mPreferences.edit();
                edit.putString(SharedPreferencesConstants.CURRENT_ACCOUNT_ID,
                        account.getId());
                edit.commit();
            } else {
                // TODO: Handle me
            }
        }
    }

    public AccountDescriptor getCurrentAccount() {
        return mCurrentAccountIndex != null ? mAccounts
                .get(mCurrentAccountIndex) : null;
    }

    public static String getAccountDescriptorKey(String id) {
        return SharedPreferencesConstants.ACCOUNT_DESCRIPTOR_PREFIX + id;
    }


//    public User getCurrentUser() {
//        return currentUser;
//    }
//
//    public void setCurrentUser(User user) {
//        this.currentUser = user;
//    }

//    public Account getCurrentAccount() {
//        AccountManager accountManager = AccountManager.get(this);
//        Account[] accounts = accountManager
//                .getAccountsByType(LoginActivity.PARAM_ACCOUNT_TYPE);
//
//        if (accounts.length > 0) {
//            return accounts[0];
//        } else {
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            return null;
//        }
//    }
}
