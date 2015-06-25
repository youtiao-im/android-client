package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.Logger;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewBulletinActivity extends RoboActionBarActivity {

    public static final String PARAM_GROUP = "current_group";

    @InjectView(R.id.tv_bulletin_recevier)
    TextView bulletinReceiverTv;

    @InjectView(R.id.edtTxt_bulletin_content)
    EditText feedContentEdtTxt;

    @InjectView(R.id.btn_add_bulletin)
    Button addBulletinBtn;

    private Group group;

    @Inject
    RemoteApi remoteApi;

    @Inject
    DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bulletin);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra(PARAM_GROUP);
        bulletinReceiverTv.setText(group.name);

        addBulletinBtn.setOnClickListener(v -> {
            String content = feedContentEdtTxt.getText().toString().trim();
            if (content != null && content.length() != 0) {
                remoteApi.createBulletin(group.id, content)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> {
                            //TODO: notify new bulletin event
                            finish();
                        }, Logger::logThrowable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_bulletin, menu);
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
}
