package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.GroupArrayAdapter;
import im.youtiao.android_client.dao.BulletinHelper;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.dao.GroupDao;
import im.youtiao.android_client.dao.GroupHelper;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.Logger;
import im.youtiao.android_client.wrap.BulletinWrap;
import im.youtiao.android_client.wrap.GroupWrap;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectSendGroupActivity extends RoboActionBarActivity {

    private static final String TAG = SelectSendGroupActivity.class.getCanonicalName();

    public static final String PARAM_GROUP = "current_group";

    @InjectView(R.id.lv_group_list)
    ListView groupsLv;

    @InjectView(R.id.edtTxt_send_to_group_name)
    EditText sendToGroupNameEdtTxt;

    @InjectView(R.id.btn_done)
    Button doneBtn;

    @Inject
    RemoteApi remoteApi;

    @Inject
    DaoSession daoSession;

    GroupArrayAdapter mAdapter;

    LinkedList<Group> groups = new LinkedList<Group>();

    Group sendToGroup;

    @Override
    protected void onStart() {
        super.onStart();
        loadOwnerGroups();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_send_group);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        sendToGroup = (Group) intent.getSerializableExtra(PARAM_GROUP);
        if (sendToGroup != null) {
            sendToGroupNameEdtTxt.setText(sendToGroup.name);
        }

        mAdapter = new GroupArrayAdapter(this, R.layout.row_group_select, groups);
        groupsLv.setAdapter(mAdapter);
        groupsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = (Group) mAdapter.getItem(position);
                sendToGroupNameEdtTxt.setText(group.name);
                sendToGroup = group;
                mAdapter.setSelectedIndex(position);
                mAdapter.notifyDataSetChanged();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFinish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_send_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                selectedFinish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void selectedFinish() {
        Bundle data = new Bundle();
        data.putSerializable(PARAM_GROUP, sendToGroup);
        Intent intent = getIntent();
        intent.putExtras(data);
        SelectSendGroupActivity.this.setResult(0, intent);
        SelectSendGroupActivity.this.finish();
    }

    protected void loadOwnerGroups() {
        Log.i(TAG, "updateData");

        GroupDao groupDao = daoSession.getGroupDao();
        List<im.youtiao.android_client.dao.Group> groupsOnDb = groupDao.loadAll();
        if (groups.size() > 0) {
            for (im.youtiao.android_client.dao.Group group : groupsOnDb) {
                groups.add(GroupWrap.wrap(group));
            }
            if (sendToGroup != null) {
                mAdapter.setSelectedIndex(indexOfSendToGroup());
            }
            mAdapter.notifyDataSetChanged();
        } else {
            remoteApi.listGroups()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resp -> {
                        Log.i(TAG, "groups size = " + resp.size());
                        groups.clear();
                        for (Group group : resp) {
                            if (group.membership.role.equalsIgnoreCase(Group.Role.OWNER.toString())) {
                                groups.add(group);
                            }
                            if (sendToGroup != null) {
                                mAdapter.setSelectedIndex(indexOfSendToGroup());
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }, Logger::logThrowable);
        }
    }

    private int indexOfSendToGroup() {
        int index = -1;
        int counter = 0;
        for (Group group : groups) {
            if (group.id.equalsIgnoreCase(sendToGroup.id)) {
                index = counter;
                break;
            }
            counter ++;
        }
        return index;
    }
}
