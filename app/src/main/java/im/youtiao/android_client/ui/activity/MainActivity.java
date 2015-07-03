package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.inject.Inject;

import de.greenrobot.event.EventBus;
import im.youtiao.android_client.dao.BulletinDao;
import im.youtiao.android_client.dao.BulletinHelper;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.event.BulletinCommentClickEvent;
import im.youtiao.android_client.event.BulletinGroupNameClickEvent;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.ui.activity.fragment.BulletinsFragment;
import im.youtiao.android_client.ui.activity.fragment.GroupsFragment;
import im.youtiao.android_client.ui.activity.fragment.SettingsFragment;
import im.youtiao.android_client.wrap.BulletinWrap;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

import im.youtiao.android_client.R;
import roboguice.activity.RoboActionBarActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

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
                // when user do a swipe the selected tab change
                Log.e(TAG, "Page Selected: " + position);
                tabHost.setSelectedNavigationItem(position);
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



    public void OnDestory() {
        Log.e(TAG, "MainActivity OnDestory, close cursor");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        Log.i(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == 1 && resultCode == 1 && intent != null) {
            Bulletin bulletin = (Bulletin)intent.getSerializableExtra(NewBulletinActivity.PARAM_NEW_BULLETIN);
            BulletinDao bulletinDao = daoSession.getBulletinDao();
            bulletinDao.insert(BulletinWrap.validate(bulletin));
            getContentResolver().notifyChange(BulletinHelper.CONTENT_URI, null);
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
        Intent intent = new Intent(this, BootstrapActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNewGroupButtonClick() {
        Intent intent = new Intent(this, NewGroupActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onJoinGroupButtonClick() {
        Intent intent = new Intent(this, JoinGroupActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onGroupItemClick(Group group) {
        Bundle data = new Bundle();
        data.putSerializable(GroupProfileActivity.PARAM_GROUP, group);
        Intent intent = new Intent(this, GroupProfileActivity.class);
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

    public void onEventMainThread(BulletinGroupNameClickEvent event) {
        Log.i(TAG, "on BulletinGroupNameClickEvent");
        Bulletin bulletin = event.bulletin;
        Bundle data = new Bundle();
        data.putSerializable(GroupProfileActivity.PARAM_GROUP, bulletin.group);
        Intent intent = new Intent(this, GroupProfileActivity.class);
        intent.putExtras(data);
        startActivity(intent);
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
                    return "Bulletins";
                case 1:
                    return "Groups";
                case 2:
                    return "Profile";
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
}
