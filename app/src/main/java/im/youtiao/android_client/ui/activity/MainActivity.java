package im.youtiao.android_client.ui.activity;

import android.app.AlertDialog;
import android.app.usage.UsageEvents;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.inject.Inject;

import de.greenrobot.event.EventBus;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.dao.BulletinDao;
import im.youtiao.android_client.dao.BulletinHelper;
import im.youtiao.android_client.dao.DaoHelper;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.event.AccountModifyEvent;
import im.youtiao.android_client.event.BulletinCommentClickEvent;
import im.youtiao.android_client.event.BulletinGroupNameClickEvent;
import im.youtiao.android_client.event.BulletinStampEvent;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.model.User;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.ui.activity.fragment.BulletinsFragment;
import im.youtiao.android_client.ui.activity.fragment.GroupsFragment;
import im.youtiao.android_client.ui.activity.fragment.SettingsFragment;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import im.youtiao.android_client.util.Log;
import im.youtiao.android_client.wrap.BulletinWrap;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

import im.youtiao.android_client.R;
import roboguice.activity.RoboActionBarActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends RoboActionBarActivity implements MaterialTabListener, BulletinsFragment.OnBulletinsFragmentInteractionListener,
        SettingsFragment.OnProfileFragmentInteractionListener, GroupsFragment.OnGroupsFragmentInteractionListener {

    private static final String TAG = MainActivity.class
            .getCanonicalName();
    MaterialTabHost tabHost;
    ViewPager pager;
    ViewPagerAdapter adapter;
    Resources res;

    MenuItem newBulletinMenu;

    @Inject
    DaoSession daoSession;

    @Inject
    RemoteApi remoteApi;

    private static final int INTENT_NEW_BULLETIN = 0;
    private static final int INTENT_CREATE_GROUP = 1;
    private static final int INTENT_JOIN_GROUP = 2;
    private static final int INTENT_EDIT_ACCOUNT_NAME = 3;
    private static final int INTENT_CHANGE_PASSWORD= 4;

    YTApplication getApp() {
        return (YTApplication)getApplication();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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
        setContentView(R.layout.activity_main);

        res = this.getResources();

        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);


        tabHost.setIconColor(getResources().getColor(R.color.tab_icon_unselected_color));
        tabHost.setAccentColor(getResources().getColor(R.color.tab_icon_unselected_color));

        // init view pager
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
                tabHost.getCurrentTab().setIconColor(getResources().getColor(R.color.tab_icon_selected_color));
                if (position == 0) {
                    newBulletinMenu.setVisible(true);
                } else {
                    newBulletinMenu.setVisible(false);
                }
            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setIcon(getIcon(i))
                            .setTabListener(this));
        }
        tabHost.setSelectedNavigationItem(0);
        tabHost.getCurrentTab().setIconColor(getResources().getColor(R.color.tab_icon_selected_color));
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            switch (num) {
                case 0:
                    return new BulletinsFragment();
                case 1:
                    return new GroupsFragment();
                case 2:
                    return new SettingsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.bulletins);
                case 1:
                    return getString(R.string.groups);
                case 2:
                    return getString(R.string.settings);
                default:
                    return null;
            }
        }
    }

    /*
    * It doesn't matter the color of the icons, but they must have solid colors
    */
    private Drawable getIcon(int position) {
        switch (position) {
            case 0:
                return res.getDrawable(R.mipmap.tab_home_filled);
            case 1:
                return res.getDrawable(R.mipmap.tab_group_filled);
            case 2:
                return res.getDrawable(R.mipmap.tab_settings_filled);
        }
        return null;
    }



    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        newBulletinMenu = menu.findItem(R.id.action_new_bulletin);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_new_bulletin:
                Intent intent = new Intent(MainActivity.this, NewBulletinActivity.class);
                startActivityForResult(intent, INTENT_NEW_BULLETIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        Log.i(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == INTENT_NEW_BULLETIN && resultCode == 0 && intent != null) {
            Bulletin bulletin = (Bulletin)intent.getSerializableExtra(NewBulletinActivity.PARAM_NEW_BULLETIN);
            BulletinDao bulletinDao = daoSession.getBulletinDao();
            bulletinDao.insert(BulletinWrap.validate(bulletin));
            getContentResolver().notifyChange(BulletinHelper.CONTENT_URI, null);
        }

        if (requestCode == INTENT_EDIT_ACCOUNT_NAME && resultCode == 0 && intent != null) {
            User user = (User)intent.getSerializableExtra(FieldEditActivity.PARAM_USER);
            ((SettingsFragment)getSupportFragmentManager().getFragments().get(pager.getCurrentItem())).updateAccount(user);
            getApp().onUpdateCurrentAccount(user);
        }
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        pager.setCurrentItem(materialTab.getPosition());
        materialTab.setIconColor(getResources().getColor(R.color.tab_icon_selected_color));
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {
        materialTab.setIconColor(getResources().getColor(R.color.tab_icon_selected_color));
    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {
        materialTab.setIconColor(getResources().getColor(R.color.tab_icon_unselected_color));
    }

    @Override
    public void onBulletinClick(Bulletin bulletin) {
        Bundle data = new Bundle();
        data.putSerializable(BulletinDetailActivity.PARAM_BULLETIN, bulletin);
        Intent intent = new Intent(this, BulletinDetailActivity.class);
        intent.putExtras(data);
        startActivity(intent);
    }



    @Override
    public void onSignOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.tip_sign_out));
        builder.setPositiveButton(getString(R.string.tip_btn_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getApp().signOutAccount(getApp().getCurrentAccount().getId());
                Intent intent = new Intent(MainActivity.this, BootstrapActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(getString(R.string.tip_btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public void onAccountNameClick() {
        Intent intent = new Intent(this, FieldEditActivity.class);
        intent.putExtra(FieldEditActivity.PARAM_EDIT_TYPE, FieldEditActivity.TYPE_ACCOUNT_NAME);
        startActivityForResult(intent, INTENT_EDIT_ACCOUNT_NAME);
    }

    @Override
    public void onChangePasswordClick() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivityForResult(intent, INTENT_CHANGE_PASSWORD);
    }

    @Override
    public void onNewGroupButtonClick() {
        Intent intent = new Intent(this, NewGroupActivity.class);
        startActivityForResult(intent, INTENT_CREATE_GROUP);
    }

    @Override
    public void onJoinGroupButtonClick() {
        Intent intent = new Intent(this, JoinGroupActivity.class);
        startActivityForResult(intent, INTENT_JOIN_GROUP);
    }

    @Override
    public void onGroupItemClick(Group group) {
        Bundle data = new Bundle();
        data.putSerializable(GroupProfileActivity.PARAM_GROUP, group);
        Intent intent = new Intent(MainActivity.this, GroupProfileActivity.class);
        intent.putExtras(data);
        startActivity(intent);
    }

    public void onEventMainThread(BulletinCommentClickEvent event) {
        Log.i(TAG, "on BulletinCommentClickEvent");
        Bulletin bulletin = event.bulletin;
        Bundle data = new Bundle();
        data.putSerializable(BulletinDetailActivity.PARAM_BULLETIN, bulletin);
        Intent intent = new Intent(this, BulletinDetailActivity.class);
        intent.putExtras(data);
        startActivity(intent);
    }

    public void onEventMainThread(BulletinStampEvent event) {
        Log.i(TAG, "on BulletinStampEvent");
        Bulletin bulletin = event.bulletin;
        String symbol = event.symbol;

        remoteApi.markBulletin(bulletin.id, symbol)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(resp -> {
                    DaoHelper.insertOrUpdate(daoSession, BulletinWrap.validate(resp));
                    getContentResolver().notifyChange(BulletinHelper.CONTENT_URI, null);
                }, error -> NetworkExceptionHandler.handleThrowable(error, this));
    }

    public void onEventMainThread(BulletinGroupNameClickEvent event) {
        Log.i(TAG, "on BulletinGroupNameClickEvent");
        Bulletin bulletin = event.bulletin;
        Bundle data = new Bundle();
        data.putSerializable(GroupProfileActivity.PARAM_GROUP, bulletin.group);
        Intent intent = new Intent(this, GroupProfileActivity.class);
        intent.putExtras(data);
        startActivity(intent);
    }
}
