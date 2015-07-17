package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.MemberArrayAdapter;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.model.Membership;
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

public class GroupMemberActivity extends RoboActionBarActivity {

    private static final String TAG = GroupMemberActivity.class.getCanonicalName();

    public static final String PARAM_GROUP = "current_group";

    @InjectView(R.id.lv_group_members)
    ListView groupMembersLv;

    @Inject
    RemoteApi remoteApi;

    Group group;

    MemberArrayAdapter mAdapter = null;
    LinkedList<Membership> memberships = new LinkedList<Membership>();

    ProgressHUD progressDialog;

    @Override
    protected void onStart() {
        Log.i(TAG, "OnStart");
        super.onStart();
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
        setContentView(R.layout.activity_group_member);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra(PARAM_GROUP);

        mAdapter = new MemberArrayAdapter(this, R.layout.row_membership, memberships);
        groupMembersLv.setAdapter(mAdapter);
        groupMembersLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "OnItemClick");
                Membership membership = (Membership) mAdapter.getItem(position);
                Bundle data = new Bundle();
                data.putSerializable(GroupMemberProfileActivity.PARAM_MEMBER, membership);
                data.putSerializable(GroupMemberProfileActivity.PARAM_GROUP, group);
                Intent intent = new Intent(GroupMemberActivity.this, GroupMemberProfileActivity.class);
                intent.putExtras(data);
                startActivityForResult(intent, 0);
            }
        });

        startMembershipsSyncing();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_member, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == 0 && resultCode == 0 && intent != null) {
            Log.i(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
            Membership membership = (Membership) intent.getSerializableExtra(GroupMemberProfileActivity.PARAM_MEMBER);
            if (membership != null) {
                for (Membership item : memberships) {
                    if (item.id.equalsIgnoreCase(membership.id)) {
                        memberships.remove(item);
                        break;
                    }
                }
                memberships.remove(membership);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    void startMembershipsSyncing() {
        progressDialog = ProgressHUD.show(this, "", true, true, null);
        memberships.clear();
        AppObservable.bindActivity(this, remoteApi.listGroupMemberships(group.id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    progressDialog.dismiss();
                    processMemberships(resp);
                }, error -> {
                    progressDialog.dismiss();
                    NetworkExceptionHandler.handleThrowable(error, this);
                });
    }

    void processMemberships(List<Membership> membershipList) {
        for (Membership item : membershipList) {
            memberships.add(item);
        }
        mAdapter.notifyDataSetChanged();
    }
}
