package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.ArrayList;

import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.CommentArrayAdapter;
import im.youtiao.android_client.adapter.MemberArrayAdapter;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Comment;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.model.Membership;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.Logger;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupMemberActivity extends RoboActionBarActivity {

    private static final String TAG = GroupMemberActivity.class.getCanonicalName();

    public static final String PARAM_GROUP = "current_group";

    @InjectView(R.id.lv_group_members)
    ListView groupMembersLv;

    @Inject
    RemoteApi remoteApi;

    Group group;

    MemberArrayAdapter mAdapter = null;
    ArrayList<Membership> memberships = new ArrayList<Membership>();

    @Override
    protected void onStart() {
        Log.i(TAG, "OnStart");
        super.onStart();
        loadMemberships();
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
                Intent intent = new Intent(GroupMemberActivity.this, GroupMemberProfileActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
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
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void loadMemberships() {
        remoteApi.listGroupMemberships(group.id, null, 100)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    for (Membership item : resp) {
                        memberships.add(item);
                    }
                    mAdapter.notifyDataSetChanged();
                }, Logger::logThrowable);
    }
}
