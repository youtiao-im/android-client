package im.youtiao.android_client.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.dao.GroupDao;
import im.youtiao.android_client.dao.GroupHelper;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.ui.widget.ProgressHUD;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import im.youtiao.android_client.wrap.GroupWrap;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import com.umeng.analytics.MobclickAgent;

public class JoinGroupActivity extends RoboActionBarActivity {

    private static final String TAG = JoinGroupActivity.class.getCanonicalName();

    @InjectView(R.id.edtTxt_group_code) private EditText groupCodeEdtTxt;
    @Inject private RemoteApi remoteApi;
    @Inject
    private DaoSession daoSession;

    MenuItem joinMenu;

    @Override
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
        setContentView(R.layout.activity_join_group);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        groupCodeEdtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newField = groupCodeEdtTxt.getText().toString();
                if (newField == null || newField.length() == 0) {
                    joinMenu.setEnabled(false);
                } else {
                    joinMenu.setEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_join_group, menu);
        joinMenu = menu.findItem(R.id.action_join);
        joinMenu.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_join:
                return joinNew();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean joinNew() {
        String code = groupCodeEdtTxt.getText().toString().trim();
        if (code != null && code.length() != 0) {
//            ProgressDialog progressDialog = new ProgressDialog(JoinGroupActivity.this);
//            progressDialog.setMessage(getString(R.string.progress_message_join));
//            progressDialog.show();
            ProgressHUD progressDialog = ProgressHUD.show(this, "", true, true, null);
            AppObservable.bindActivity(this, remoteApi.joinGroup(code))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resp -> {
                        progressDialog.dismiss();
                        GroupDao groupDao = daoSession.getGroupDao();
                        groupDao.insertOrReplace(GroupWrap.validate(resp));
                        getContentResolver().notifyChange(GroupHelper.CONTENT_URI, null);
                        finish();
                    }, error -> {
                        progressDialog.dismiss();
                        NetworkExceptionHandler.handleThrowable(error, this, NetworkExceptionHandler.ACTION_GROUP);
                    });
        }
        return true;
    }
}
