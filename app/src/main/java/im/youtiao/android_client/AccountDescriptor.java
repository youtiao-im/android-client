package im.youtiao.android_client;


import org.json.JSONException;
import org.json.JSONObject;

import im.youtiao.android_client.model.User;

public class AccountDescriptor implements java.io.Serializable {
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_TOKEN = "token";


    private String mId;
    private String mName;
    private String mPassword;
    private String mTokenType;
    private String mToken;

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getTokenType() {
        return mTokenType;
    }

    public String getToken() {
        return mToken;
    }

    public String getPassword() {
        return this.mPassword;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public AccountDescriptor(User user, String password, String tokenType, String token) {
        this.mId = user.id;
        this.mName = user.email;
        this.mToken = token;
        this.mTokenType = tokenType;
        this.mPassword = password;
    }

    public AccountDescriptor(String jsonAsString) {
        try {
            JSONObject object = new JSONObject(jsonAsString);
            mId = object.getString(KEY_ID);
            if (object.has(KEY_NAME)) {
                mName = object.getString(KEY_NAME);
            }
            if (object.has(KEY_TOKEN)) {
                mToken = object.getString(KEY_TOKEN);
            }
            if (object.has(KEY_TOKEN_TYPE)) {
                mTokenType = object.getString(KEY_TOKEN_TYPE);
            }
            if (object.has(KEY_PASSWORD)) {
                mPassword = object.getString(KEY_PASSWORD);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put(KEY_ID, mId);
            object.put(KEY_NAME, mName);
            object.put(KEY_PASSWORD, mPassword);
            object.put(KEY_TOKEN, mToken);
            object.put(KEY_TOKEN_TYPE, mTokenType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
