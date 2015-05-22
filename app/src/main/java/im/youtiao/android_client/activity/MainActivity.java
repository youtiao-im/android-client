package im.youtiao.android_client.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import im.youtiao.android_client.activity.fragment.ChannelsFragment;
import im.youtiao.android_client.activity.fragment.ChatsFragment;
import im.youtiao.android_client.activity.fragment.DemoFragment;
import im.youtiao.android_client.activity.fragment.FeedsFragment;
import im.youtiao.android_client.activity.fragment.ProfileFragment;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

import im.youtiao.android_client.R;

public class MainActivity extends FragmentActivity implements MaterialTabListener, FeedsFragment.OnFeedsFragmentInteractionListener, DemoFragment.OnDemoFragmentInteractionListener,
        ChatsFragment.OnChatsFragmentInteractionListener, ProfileFragment.OnProfileFragmentInteractionListener {

    private static final String TAG = MainActivity.class
            .getCanonicalName();
    MaterialTabHost tabHost;
    ViewPager pager;
    ViewPagerAdapter adapter;
    Resources res;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = this.getResources();

        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);

        // init view pager
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);

            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setIcon(getIcon(i))
                            .setTabListener(this));
        }
    }

    public void OnDestory() {
        Log.e(TAG, "MainActivity OnDestory, close cursor");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        pager.setCurrentItem(materialTab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }

    @Override
    public void onFeedsFragmentInteraction(String id) {
        Intent intent = new Intent(this, FeedDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onChatsFragmentInteraction(String id) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onProfileFragmentInteraction(String id) {

    }

    @Override
    public void onDemoFragmentInteraction(String id) {
        Intent intent = new Intent(this, ChannelDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNewChannelButtonClick() {
        Intent intent = new Intent(this, NewChannelActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onJoinChannelButtonClick() {

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            switch (num) {
                case 0:
                    return new FeedsFragment();
                case 1:
                    return new ChatsFragment();
                case 2:
                    return new DemoFragment();
                case 3:
                    return new ProfileFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Feed";
                case 1:
                    return "Chat";
                case 2:
                    return "Channel";
                case 3:
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
                return res.getDrawable(R.mipmap.ic_person_black_24dp);
            case 1:
                return res.getDrawable(R.mipmap.ic_group_black_24dp);
            case 2:
                return res.getDrawable(R.mipmap.ic_tab_channel);
            case 3:
                return res.getDrawable(R.mipmap.ic_person_black_24dp);
        }
        return null;
    }
}
