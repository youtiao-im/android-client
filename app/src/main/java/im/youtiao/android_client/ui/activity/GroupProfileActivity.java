package im.youtiao.android_client.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.dao.GroupHelper;
import im.youtiao.android_client.data.DaoHelper;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.wrap.GroupWrap;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class GroupProfileActivity extends RoboActionBarActivity {
    private static final String TAG = GroupProfileActivity.class.getCanonicalName();
    public static final String PARAM_GROUP = "current_group";

    private Group group;

    @Inject
    RemoteApi remoteApi;

    @InjectView(R.id.tv_group_name) TextView groupNameTv;
    @InjectView(R.id.tv_group_code) TextView groupCodeTv;
    @InjectView(R.id.tv_group_admin) TextView groupAdminTv;
    @InjectView(R.id.tv_group_members_count) TextView groupMembersCountTv;
    @InjectView(R.id.iv_group_memeber_forward) ImageView groupMemberForwardImageView;
    @InjectView(R.id.layout_group_members) RelativeLayout groupMemsLayout;
    @InjectView(R.id.layout_delete_group) RelativeLayout deleteGroupLayout;
    @InjectView(R.id.btn_group_info_edit) Button groupInfoEditBtn;

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
        groupCodeTv.setText(group.code);
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

        deleteGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfileActivity.this);
                builder.setMessage("Are you sure you want to remove this group?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = getIntent();
                        GroupProfileActivity.this.setResult(0, intent);
                        GroupProfileActivity.this.finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
            }
        });

        groupInfoEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putSerializable(GroupEditActivity.PARAM_GROUP, group);
                Intent intent = new Intent(GroupProfileActivity.this, GroupEditActivity.class);
                intent.putExtras(data);
                startActivityForResult(intent, 0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        Log.i(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == 0 && resultCode == 0) {
            if (intent != null) {
                Group group = (Group) intent.getSerializableExtra(GroupEditActivity.PARAM_GROUP);
                if (group != null) {
                    groupNameTv.setText(group.name);
                    groupCodeTv.setText(group.code);
                    DaoHelper.insertOrUpdate(daoSession, GroupWrap.validate(group));
                    getContentResolver().notifyChange(GroupHelper.CONTENT_URI, null);
                }
            }
        }
    }
}
