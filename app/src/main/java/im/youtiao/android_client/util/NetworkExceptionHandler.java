package im.youtiao.android_client.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import im.youtiao.android_client.R;
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

        RetrofitError retrofitError = (RetrofitError) throwable;

        Throwable cause = retrofitError.getCause();
        if (cause == null) {
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
        } else {

        }
    }

    public static void handleThrowable(Throwable throwable, Context context) {
        ServerError serverError = new ServerError();

        RetrofitError retrofitError = (RetrofitError) throwable;
        Throwable cause = retrofitError.getCause();
        if (cause == null) {
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
            handleServerError(serverError, context);
        } else {
            handleNetWorkError(cause, context);
        }
    }


    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int UNPROCESSABLE_ENTITY = 422;
    public static final int SERVER_ERROR = 500;

    public static void handleServerError(ServerError serverError, Context context) {
        int status = serverError.status;
        String displayMessage = "";
        switch(status){
            case UNAUTHORIZED:
                YTApplication application = (YTApplication) context.getApplicationContext();
                application.signOutAccount(application.getCurrentAccount().getId());
                Intent intent = new Intent(context, BootstrapActivity.class);
                context.startActivity(intent);
                return;
            case BAD_REQUEST:
                displayMessage = context.getString(R.string.error_bad_request);
                break;
            case FORBIDDEN:
                displayMessage = context.getString(R.string.error_forbidden);
                break;
            case NOT_FOUND:
                displayMessage = context.getString(R.string.error_not_found);
                break;
            case METHOD_NOT_ALLOWED:
                displayMessage = context.getString(R.string.error_method_not_allowed);
                break;
            case SERVER_ERROR:
                displayMessage = context.getString(R.string.error_server_error);
                break;
            case UNPROCESSABLE_ENTITY:
                displayMessage = getDisplayMessageFromErrorMessage(serverError.errorMessage, context);
                break;
            default:

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(displayMessage)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private static String getDisplayMessageFromErrorMessage(String errorMessage, Context context) {
        String displayMessage = "";
        String[] items = errorMessage.split(":");
        int resourceId = R.string.error_server_error;
        if (items.length == 2) {
            String attribute = items[0];
            String reason = items[1];
            switch(attribute) {
                case "email":
                    switch(reason) {
                        case "too_long":
                            resourceId = R.string.error_email_too_long;
                            break;
                        case "too_short":
                            resourceId = R.string.error_email_too_short;
                            break;
                        case "blank":
                            resourceId = R.string.error_email_blank;
                            break;
                        case "invalid":
                            resourceId = R.string.error_email_invalid;
                            break;
                        case "taken":
                            resourceId = R.string.error_email_taken;
                            break;
                    }
                case "password":
                    switch(reason) {
                        case "too_long":
                            resourceId = R.string.error_password_too_long;
                            break;
                        case "too_short":
                            resourceId = R.string.error_password_too_short;
                            break;
                        case "blank":
                            resourceId = R.string.error_password_blank;
                            break;
                        case "invalid":
                            resourceId = R.string.error_password_invalid;
                            break;
                        case "taken":
                            resourceId = R.string.error_password_taken;
                            break;
                    }
                case "name":
                    switch(reason) {
                        case "too_long":
                            resourceId = R.string.error_group_name_too_long;
                            break;
                        case "too_short":
                            resourceId = R.string.error_group_name_too_short;
                            break;
                        case "blank":
                            resourceId = R.string.error_group_name_blank;
                            break;
                        case "invalid":
                            resourceId = R.string.error_group_name_invalid;
                            break;
                        case "taken":
                            resourceId = R.string.error_group_name_taken;
                            break;
                    }
                case "code":
                    switch(reason) {
                        case "too_long":
                            resourceId = R.string.error_group_code_too_long;
                            break;
                        case "too_short":
                            resourceId = R.string.error_group_code_too_short;
                            break;
                        case "blank":
                            resourceId = R.string.error_group_code_blank;
                            break;
                        case "invalid":
                            resourceId = R.string.error_group_code_invalid;
                            break;
                        case "taken":
                            resourceId = R.string.error_group_code_taken;
                            break;
                    }
                default:

            }
        }
        return context.getString(resourceId);
    }

    public static void handleNetWorkError(Throwable error, Context context) {
        try {
            if (error instanceof SocketTimeoutException) {
                Log.e(TAG, "show alert on:" + context.getClass().toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Network Connect Error")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                builder.show();
                //Toast.makeText(context, "NewWork Connect Error", Toast.LENGTH_LONG).show();
                Log.e(TAG, error.getMessage());
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(error.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}