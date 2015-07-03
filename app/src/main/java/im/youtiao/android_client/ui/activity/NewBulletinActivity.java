package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewBulletinActivity extends RoboActionBarActivity {
    private static final String TAG = NewBulletinActivity.class.getCanonicalName();
    public static final String PARAM_GROUP = "current_group";
    public static final String PARAM_NEW_BULLETIN = "new_bulletin";

    @InjectView(R.id.tv_bulletin_recevier)
    TextView bulletinReceiverTv;

    @InjectView(R.id.edtTxt_bulletin_content)
    EditText bulletinContentEdtTxt;

    private Group group;

    @Inject
    RemoteApi remoteApi;

    @Inject
    DaoSession daoSession;


    MenuItem sendMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bulletin);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra(PARAM_GROUP);
        if (group != null) {
            bulletinReceiverTv.setText(group.name);
        }

        bulletinContentEdtTxt.addTextChangedListener(textWatcher);

        bulletinReceiverTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putSerializable(SelectSendGroupActivity.PARAM_GROUP, group);
                Intent intent = new Intent(NewBulletinActivity.this, SelectSendGroupActivity.class);
                intent.putExtras(data);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == 0 && resultCode == 0) {
            if (intent != null) {
                Group sendToGroup = (Group) intent.getSerializableExtra(GroupEditActivity.PARAM_GROUP);
                if (sendToGroup != null) {
                    bulletinReceiverTv.setText(sendToGroup.name);
                    group = sendToGroup;
                }
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_bulletin, menu);


        sendMenu = menu.findItem(R.id.action_send);
        sendMenu.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_send:
                return send();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean send() {
        String content = bulletinContentEdtTxt.getText().toString().trim();
        if (content != null && content.length() != 0) {
            remoteApi.createBulletin(group.id, content)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resp -> {
                        Bundle data = new Bundle();
                        data.putSerializable(PARAM_NEW_BULLETIN, resp);
                        Intent intent = getIntent();
                        intent.putExtras(data);
                        NewBulletinActivity.this.setResult(1, intent);
                        NewBulletinActivity.this.finish();
                    }, error -> NetworkExceptionHandler.handleThrowable(error, this));
        }
        return true;
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            String content = bulletinContentEdtTxt.getText().toString();
            if (content == null || content.length() == 0) {
                sendMenu.setEnabled(false);
                bulletinContentEdtTxt.setHint(getString(R.string.hint_type_bulletin_content));
            } else {
                sendMenu.setEnabled(true);
            }
        }
    };
}
