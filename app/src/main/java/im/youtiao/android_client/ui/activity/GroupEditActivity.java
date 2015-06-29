package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.GroupDao;
import im.youtiao.android_client.dao.GroupHelper;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.Logger;
import im.youtiao.android_client.wrap.GroupWrap;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupEditActivity extends RoboActionBarActivity {

    public static final String PARAM_GROUP = "current_group";

    @InjectView(R.id.edtTxt_group_name)
    EditText groupNameEdtTxt;

    @InjectView(R.id.edtTxt_group_code)
    EditText groupCodeEdtTxt;

    @Inject
    RemoteApi remoteApi;

    MenuItem saveMenu;

    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra(PARAM_GROUP);

        groupNameEdtTxt.setText(group.name);
        groupCodeEdtTxt.setText(group.code);

        groupNameEdtTxt.addTextChangedListener(textWatcher);
        groupCodeEdtTxt.addTextChangedListener(textWatcher);

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
            if (group.name.equalsIgnoreCase(groupNameEdtTxt.getText().toString()) &&
                    group.code.equalsIgnoreCase(groupCodeEdtTxt.getText().toString())) {
                saveMenu.setEnabled(false);
            } else {
                saveMenu.setEnabled(true);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_edit, menu);

        saveMenu = menu.findItem(R.id.action_save);
        saveMenu.setEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_save:
                return save();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean save() {
        String groupName = groupNameEdtTxt.getText().toString().trim();
        String groupCode = groupCodeEdtTxt.getText().toString().trim();
        if (groupName != null && groupCode != null ) {
            remoteApi.updateGroup(group.id, groupName, groupCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( resp -> {
                        Bundle data = new Bundle();
                        data.putSerializable(PARAM_GROUP, resp);
                        Intent intent = getIntent();
                        intent.putExtras(data);
                        GroupEditActivity.this.setResult(0, intent);
                        GroupEditActivity.this.finish();
                    }, Logger::logThrowable);
        }
        return true;
    }
}
