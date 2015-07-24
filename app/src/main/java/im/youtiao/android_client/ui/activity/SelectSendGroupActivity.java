package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.GroupArrayAdapter;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.dao.GroupDao;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.ui.widget.ProgressHUD;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import im.youtiao.android_client.wrap.GroupWrap;
import im.youtiao.android_client.util.Log;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import com.umeng.analytics.MobclickAgent;

public class SelectSendGroupActivity extends RoboActionBarActivity {

    private static final String TAG = SelectSendGroupActivity.class.getCanonicalName();

    public static final String PARAM_GROUP = "current_group";

    @InjectView(R.id.lv_group_list)
    ListView groupsLv;

    @Inject
    RemoteApi remoteApi;

    @Inject
    DaoSession daoSession;

    GroupArrayAdapter mAdapter;

    LinkedList<Group> groups = new LinkedList<Group>();

    Group sendToGroup;

    PtrClassicFrameLayout mPtrFrame;

    @Override
    protected void onStart() {
        super.onStart();
        loadOwnerGroups();
    }

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
        setContentView(R.layout.activity_select_send_group);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        sendToGroup = (Group) intent.getSerializableExtra(PARAM_GROUP);


        mAdapter = new GroupArrayAdapter(this, R.layout.row_group_select, groups);
        groupsLv.setAdapter(mAdapter);
        groupsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = (Group) mAdapter.getItem(position);
                sendToGroup = group;
                selectedFinish();
            }
        });

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshGroups();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        mPtrFrame.setPullToRefresh(false);
        mPtrFrame.setKeepHeaderWhenRefresh(true);
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
        SelectSendGroupActivity.this.setResult(1, intent);
        SelectSendGroupActivity.this.finish();
    }

    void refreshGroups() {
        Log.i(TAG, "refreshGroups");
        AppObservable.bindActivity(this, remoteApi.listGroups())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    Log.i(TAG, "groups size = " + resp.size());
                    groups.clear();
                    for (Group group : resp) {
                        if (group.membership.role.equalsIgnoreCase(Group.Role.OWNER.toString())) {
                            groups.add(group);
                        }
                    }
                    mPtrFrame.refreshComplete();
                    mAdapter.notifyDataSetChanged();
                }, error -> {
                    mPtrFrame.refreshComplete();
                    NetworkExceptionHandler.handleThrowable(error, this);
                });
    }

    protected void loadOwnerGroups() {
        Log.i(TAG, "updateData");
        ProgressHUD progressDialog = ProgressHUD.show(this, "", true, true, null);
        GroupDao groupDao = daoSession.getGroupDao();
        List<im.youtiao.android_client.dao.Group> groupsOnDb = groupDao.loadAll();
        if (groupsOnDb.size() > 0) {
            groups.clear();
            for (im.youtiao.android_client.dao.Group group : groupsOnDb) {
                groups.add(GroupWrap.wrap(group));
            }
            progressDialog.dismiss();
            mAdapter.notifyDataSetChanged();
        } else {
            AppObservable.bindActivity(this, remoteApi.listGroups())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resp -> {
                        Log.i(TAG, "groups size = " + resp.size());
                        groups.clear();
                        for (Group group : resp) {
                            if (group.membership.role.equalsIgnoreCase(Group.Role.OWNER.toString())) {
                                groups.add(group);
                            }
                        }
                        progressDialog.dismiss();
                        mAdapter.notifyDataSetChanged();
                    }, error -> {
                        progressDialog.dismiss();
                        NetworkExceptionHandler.handleThrowable(error, this) ;
                    });
        }
    }
}
