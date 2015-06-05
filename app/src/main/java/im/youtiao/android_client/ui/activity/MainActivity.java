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

import im.youtiao.android_client.greendao.Channel;
import im.youtiao.android_client.greendao.Feed;
import im.youtiao.android_client.providers.RemoteApiFactory;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.rest.responses.TokenResponse;
import im.youtiao.android_client.ui.activity.fragment.ChannelsFragment;
import im.youtiao.android_client.ui.activity.fragment.ChatsFragment;
import im.youtiao.android_client.ui.activity.fragment.FeedsFragment;
import im.youtiao.android_client.ui.activity.fragment.ProfileFragment;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

import im.youtiao.android_client.R;
import roboguice.activity.RoboFragmentActivity;
import rx.Observable;
import rx.schedulers.Schedulers;

public class MainActivity extends RoboFragmentActivity implements MaterialTabListener, FeedsFragment.OnFeedsFragmentInteractionListener,
        ChatsFragment.OnChatsFragmentInteractionListener, ProfileFragment.OnProfileFragmentInteractionListener, ChannelsFragment.OnChannelsFragmentInteractionListener {

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
    public void onFeedsFragmentInteraction(Feed feed) {
        Bundle data = new Bundle();
        data.putSerializable(FeedDetailActivity.PARAM_FEED, feed);
        Intent intent = new Intent(this, FeedDetailActivity.class);
        intent.putExtras(data);
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
    public void onNewChannelButtonClick() {
        Intent intent = new Intent(this, NewChannelActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onJoinChannelButtonClick() {
        Intent intent = new Intent(this, JoinChannelActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onChannelsItemClick(Channel channel) {
        Bundle data = new Bundle();
        data.putSerializable(ChannelDetailActivity.PARAM_CHANNEL, channel);
        Intent intent = new Intent(this, ChannelDetailActivity.class);
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
                    return new FeedsFragment();
                case 1:
                    return new ChatsFragment();
                case 2:
                    return new ChannelsFragment();
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
