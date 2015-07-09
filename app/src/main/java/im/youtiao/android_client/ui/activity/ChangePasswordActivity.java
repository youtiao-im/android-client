package im.youtiao.android_client.ui.activity;
import android.app.ProgressDialog;
import android.support.annotation.MainThread;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChangePasswordActivity extends RoboActionBarActivity {

    @InjectView(R.id.edtTxt_old_password)
    EditText oldPasswordEdtTxt;

    @InjectView(R.id.edtTxt_new_password)
    EditText newPasswordEdtTxt;

    @InjectView(R.id.btn_confirm)
    Button confirmBtn;

    @Inject
    RemoteApi remoteApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
        return true;
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

    void changePassword() {
        String password = oldPasswordEdtTxt.getText().toString();
        String newPassword = newPasswordEdtTxt.getText().toString();
        ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Change Password...");
        progressDialog.show();
        remoteApi.changePassword(password, newPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    progressDialog.dismiss();
                    finish();
                }, error -> {
                    progressDialog.dismiss();
                    NetworkExceptionHandler.handleThrowable(error, this);
                });
    }
}
