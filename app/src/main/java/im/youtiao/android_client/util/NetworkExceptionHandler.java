package im.youtiao.android_client.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.model.ServerError;
import im.youtiao.android_client.ui.activity.BootstrapActivity;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NetworkExceptionHandler {
    private static final String TAG = NetworkExceptionHandler.class.getCanonicalName();

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void handleThrowable(Throwable throwable) {
        ServerError serverError = new ServerError();

        RetrofitError retrofitError = (RetrofitError)throwable;
        Response r = retrofitError.getResponse();
        serverError.status = r.getStatus();
        String errorBody = "";
        try {
            errorBody = IOUtils.toString(r.getBody().in(), "UTF-8");
            JSONObject object = new JSONObject(errorBody);
            if (object.has("error")) {
                serverError.errorMessage = object.getString("error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, r.getStatus() + ", " + r.getReason() + "," + errorBody);
    }

    public static void handleThrowable(Throwable throwable, Context context) {
        ServerError serverError = new ServerError();

        RetrofitError retrofitError = (RetrofitError)throwable;
        Response r = retrofitError.getResponse();
        serverError.status = r.getStatus();
        String errorBody = "";
        try {
            errorBody = IOUtils.toString(r.getBody().in(), "UTF-8");
            JSONObject object = new JSONObject(errorBody);
            if (object.has("error")) {
                serverError.errorMessage = object.getString("error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, r.getStatus() + ", " + r.getReason() + "," + errorBody);
        handleError(serverError, context);
    }


    public static int BAE_REQUEST = 400;
    public static int UNAUTHORIZED = 401;
    public static int NOT_FOUND = 404;
    public static int METHOD_NOT_ALLOWED = 405;
    public static int UNPROCESSABLE_ENTITY = 422;
    public static int SERVER_ERROR = 500;

    public static void handleError(ServerError serverError, Context context) {
        int status = serverError.status;
        if (status == UNAUTHORIZED) {
            YTApplication application = (YTApplication)context.getApplicationContext();
            application.signOutAccount(application.getCurrentAccount().getId());
            Intent intent = new Intent(context, BootstrapActivity.class);
            context.startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(serverError.errorMessage)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }
}
