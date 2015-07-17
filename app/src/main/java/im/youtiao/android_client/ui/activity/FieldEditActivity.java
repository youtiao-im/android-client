package im.youtiao.android_client.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;
import im.youtiao.android_client.AccountDescriptor;
import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.ui.widget.ProgressHUD;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import im.youtiao.android_client.util.Log;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import com.umeng.analytics.MobclickAgent;

public class FieldEditActivity extends RoboActionBarActivity {
    private static final String TAG = GroupProfileActivity.class.getCanonicalName();

//    @InjectView(R.id.tv_hint)
//    TextView hintTv;

    @InjectView(R.id.edtTxt_field)
    EditText fieldEdtTxt;

    @Inject
    RemoteApi remoteApi;

    MenuItem saveMenu;

    int editType;
    String field;
    String hint;
    String title;
    AccountDescriptor account;
    Group group;
    public static final String PARAM_USER = "user";
    public static final String PARAM_GROUP = "group";
    public static final String PARAM_EDIT_TYPE = "edit_type";
    public static final int TYPE_ACCOUNT_NAME = 0;
    public static final int TYPE_GROUP_NAME = 1;
    public static final int TYPE_GROUP_CODE = 2;


    YTApplication getApp() {
        return (YTApplication)getApplication();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_edit);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        editType = intent.getIntExtra(PARAM_EDIT_TYPE, 0);
        switch(editType) {
            case TYPE_ACCOUNT_NAME:
                account = getApp().getCurrentAccount();
                field = account.getName();
                hint = getString(R.string.hint_pick_account_name);
                title = getString(R.string.title_activity_edit_account_name);
                break;
            case TYPE_GROUP_NAME:
                group = (Group)intent.getSerializableExtra(PARAM_GROUP);
                field = group.name;
                hint = getString(R.string.hint_pick_group_name);
                title = getString(R.string.title_activity_edit_group_name);
                break;
            case TYPE_GROUP_CODE:
                group = (Group)intent.getSerializableExtra(PARAM_GROUP);
                field = group.code;
                hint = getString(R.string.hint_pick_group_code);
                title = getString(R.string.title_activity_edit_group_code);
                break;
            default:
        }
        setTitle(title);
        fieldEdtTxt.setText(field);
        fieldEdtTxt.setSelection(field.length());
        fieldEdtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newField = fieldEdtTxt.getText().toString();
                if (newField == null || newField.length() == 0 || field.equalsIgnoreCase(newField)) {
                    saveMenu.setEnabled(false);
                } else {
                    saveMenu.setEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_field_edit, menu);
        saveMenu = menu.findItem(R.id.action_save);
        saveMenu.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_save:
                return save();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    boolean save() {
//        ProgressDialog progressDialog = new ProgressDialog(FieldEditActivity.this);
//        progressDialog.setMessage(getString(R.string.progress_message_save));
//        progressDialog.show();
        ProgressHUD progressDialog = ProgressHUD.show(this, "", true, true, null);
        String fieldContent = fieldEdtTxt.getText().toString();
        switch(editType) {
            case TYPE_ACCOUNT_NAME:
                AppObservable.bindActivity(this, remoteApi.updateUser(fieldContent, null))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> {
                            progressDialog.dismiss();
                            Bundle data = new Bundle();
                            data.putSerializable(PARAM_USER, resp);
                            Intent intent = getIntent();
                            intent.putExtras(data);
                            FieldEditActivity.this.setResult(1, intent);
                            FieldEditActivity.this.finish();
                        }, error -> {
                            progressDialog.dismiss();
                            NetworkExceptionHandler.handleThrowable(error, this);
                        });
                break;
            case TYPE_GROUP_NAME:
                AppObservable.bindActivity(this, remoteApi.updateGroup(group.id, fieldContent, null))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> {
                            progressDialog.dismiss();
                            Log.i(TAG, resp.name);
                            Bundle data = new Bundle();
                            data.putSerializable(PARAM_GROUP, resp);
                            Intent intent = getIntent();
                            intent.putExtras(data);
                            FieldEditActivity.this.setResult(1, intent);
                            FieldEditActivity.this.finish();
                        }, error -> {
                            progressDialog.dismiss();
                            NetworkExceptionHandler.handleThrowable(error, this);
                        });
                break;
            case TYPE_GROUP_CODE:
                AppObservable.bindActivity(this, remoteApi.updateGroup(group.id, null, fieldContent))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> {
                            progressDialog.dismiss();
                            Log.i(TAG, resp.code);
                            Bundle data = new Bundle();
                            data.putSerializable(PARAM_GROUP, resp);
                            Intent intent = getIntent();
                            intent.putExtras(data);
                            FieldEditActivity.this.setResult(1, intent);
                            FieldEditActivity.this.finish();
                        }, error -> {
                            progressDialog.dismiss();
                            NetworkExceptionHandler.handleThrowable(error, this);
                        });
                break;
            default:
                progressDialog.dismiss();
                break;
        }
        return true;
    }
}
