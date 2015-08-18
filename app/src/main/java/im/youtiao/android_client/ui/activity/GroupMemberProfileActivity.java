package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.model.Membership;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.ui.widget.ProgressHUD;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.google.inject.Inject;
import com.umeng.analytics.MobclickAgent;

public class GroupMemberProfileActivity extends RoboActionBarActivity {

    public static String PARAM_MEMBERSHIP = "current_member";
    public static String PARAM_GROUP = "current_group";

    @InjectView(R.id.tv_user_name)
    TextView userNameTv;
    @InjectView(R.id.tv_user_email)
    TextView userEmailTv;
    @InjectView(R.id.layout_change_role)
    RelativeLayout changeRoleLayout;
    @InjectView(R.id.tv_change_role)
    TextView changeRoleTv;

    Group group;
    Membership membership;

    boolean isMember;

    @Inject
    RemoteApi remoteApi;

    YTApplication getApp() {
        return (YTApplication) getApplication();
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
        setContentView(R.layout.activity_group_member_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra(PARAM_GROUP);
        membership = (Membership) intent.getSerializableExtra(PARAM_MEMBERSHIP);

        if (group.membership.role.equalsIgnoreCase(Membership.Role.OWNER.toString())
                && !membership.userId.equalsIgnoreCase(getApp().getCurrentAccount().getId())) {
            changeRoleLayout.setVisibility(View.VISIBLE);
        } else {
            changeRoleLayout.setVisibility(View.GONE);
        }

        isMember = membership.role.equalsIgnoreCase(Membership.Role.MEMBER.toString()) ? true : false;
        if (isMember) {
            changeRoleTv.setText(getString(R.string.btn_promote));
        } else {
            changeRoleTv.setText(getString(R.string.btn_demote));
        }

        userNameTv.setText(membership.user.name);
        userEmailTv.setText(membership.user.email);

        changeRoleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressHUD progressDialog = ProgressHUD.show(GroupMemberProfileActivity.this, "", true, true, null);
                if (isMember) {
                    AppObservable.bindActivity(GroupMemberProfileActivity.this, remoteApi.promoteMembership(membership.id))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(resp -> {
                                progressDialog.dismiss();
                                Bundle data = new Bundle();
                                data.putSerializable(PARAM_MEMBERSHIP, resp);
                                Intent intent = getIntent();
                                intent.putExtras(data);
                                GroupMemberProfileActivity.this.setResult(1, intent);
                                GroupMemberProfileActivity.this.finish();
                            }, error -> {
                                progressDialog.dismiss();
                                NetworkExceptionHandler.handleThrowable(error, GroupMemberProfileActivity.this);
                            });
                } else {
                    AppObservable.bindActivity(GroupMemberProfileActivity.this, remoteApi.demoteMembership(membership.id))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(resp -> {
                                progressDialog.dismiss();
                                Bundle data = new Bundle();
                                data.putSerializable(PARAM_MEMBERSHIP, resp);
                                Intent intent = getIntent();
                                intent.putExtras(data);
                                GroupMemberProfileActivity.this.setResult(1, intent);
                                GroupMemberProfileActivity.this.finish();
                            }, error -> {
                                progressDialog.dismiss();
                                NetworkExceptionHandler.handleThrowable(error, GroupMemberProfileActivity.this);
                            });
                }
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
