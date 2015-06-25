package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.model.Group;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class GroupProfileActivity extends RoboActionBarActivity {
    private static final String TAG = GroupProfileActivity.class.getCanonicalName();
    public static final String PARAM_GROUP = "current_group";

    private Group group;

    @InjectView(R.id.tv_group_name) TextView groupNameTv;
    @InjectView(R.id.tv_group_number) TextView groupNumTv;
    @InjectView(R.id.tv_group_code) TextView groupCodeTv;
    @InjectView(R.id.tv_group_admin) TextView groupAdminTv;
    @InjectView(R.id.tv_group_members_count) TextView groupMembersCountTv;
    @InjectView(R.id.iv_group_memeber_forward) ImageView groupMemberForwardImageView;
    @InjectView(R.id.layout_group_members) RelativeLayout groupMemsLayout;

    @Inject
    DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra(PARAM_GROUP);

        groupNameTv.setText(group.name);
        groupNumTv.setText(group.id);
        groupCodeTv.setText("");
        //groupAdminTv.setText();
        groupMembersCountTv.setText("" + group.membershipsCount);

        groupMemsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putSerializable(GroupMemberActivity.PARAM_GROUP, group);
                Intent intent = new Intent(GroupProfileActivity.this, GroupMemberActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_profile, menu);
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
}
