package im.youtiao.android_client.rest;

import android.util.Log;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RemoteApiErrorHandler implements ErrorHandler{
    private static final String TAG = RemoteApiErrorHandler.class.getCanonicalName();
    @Override
    public Throwable handleError(RetrofitError retrofitError) {
        Response r = retrofitError.getResponse();
        Log.e(TAG, r.getReason() + " : " + r.getStatus());
        return retrofitError;
    }
}
