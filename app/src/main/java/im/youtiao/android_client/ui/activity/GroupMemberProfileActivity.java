package im.youtiao.android_client.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.youtiao.android_client.R;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.model.Membership;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class GroupMemberProfileActivity extends RoboActionBarActivity {

    public static String PARAM_MEMBER = "current_member";
    public static String PARAM_GROUP = "current_group";

    @InjectView(R.id.tv_user_name)
    TextView userNameTv;
    @InjectView(R.id.tv_user_email)
    TextView userEmailTv;
    @InjectView(R.id.layout_unsubscribe_user)
    RelativeLayout removeSubscriberLayout;

    Group group;
    Membership membership;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra(PARAM_GROUP);
        membership = (Membership) intent.getSerializableExtra(PARAM_MEMBER);

        userNameTv.setText(membership.user.name);
        userEmailTv.setText(membership.user.email);

        removeSubscriberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupMemberProfileActivity.this);
                builder.setMessage(getString(R.string.unsubscribe_tip));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //TODO: remove subscriber
                        Bundle data = new Bundle();
                        data.putSerializable(PARAM_MEMBER, membership);
                        Intent intent = getIntent();
                        intent.putExtras(data);
                        GroupMemberProfileActivity.this.setResult(0, intent);
                        GroupMemberProfileActivity.this.finish();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_member_profile, menu);
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
