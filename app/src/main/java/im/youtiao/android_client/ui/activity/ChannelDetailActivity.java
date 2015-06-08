package im.youtiao.android_client.ui.activity;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.inject.Inject;

import de.greenrobot.event.EventBus;
import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.FeedCursorAdapter;
import im.youtiao.android_client.event.FeedStarEvent;
import im.youtiao.android_client.greendao.Channel;
import im.youtiao.android_client.greendao.ChannelHelper;
import im.youtiao.android_client.greendao.DaoSession;
import im.youtiao.android_client.greendao.Feed;
import im.youtiao.android_client.greendao.FeedDao;
import im.youtiao.android_client.greendao.FeedHelper;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class ChannelDetailActivity extends RoboActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ChannelDetailActivity.class.getCanonicalName();
    public static final String PARAM_CHANNEL = "current_channel";

    private Channel channel;

    private static final int URL_LOADER = 1920;

    @InjectView(R.id.feed_list)
    ListView feedList;

    @InjectView(R.id.new_feed)
    Button newFeedBtn;

    @Inject
    private FeedCursorAdapter mAdapter;

    @Inject
    private DaoSession daoSession;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        channel = (Channel) intent.getSerializableExtra(PARAM_CHANNEL);

        setTitle(channel.getName());
        getLoaderManager().initLoader(URL_LOADER, null, this);

        feedList.setEmptyView(findViewById(R.id.empty));
        feedList.setAdapter(mAdapter);
        feedList.setDividerHeight(20);

        if (channel.getRole().equalsIgnoreCase("member")) {
            newFeedBtn.setVisibility(View.GONE);
        }

        feedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "OnItemClick");
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                Feed feed = FeedHelper.fromCursor(cursor);
                Bundle data = new Bundle();
                data.putSerializable(FeedDetailActivity.PARAM_FEED, feed);
                Intent intent = new Intent(ChannelDetailActivity.this, FeedDetailActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_channel_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_profile:
                Bundle data = new Bundle();
                data.putSerializable(ChannelProfileActivity.PARAM_CHANNEL, channel);
                Intent intent = new Intent(ChannelDetailActivity.this, ChannelProfileActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "OnCreateLoader");
        return new CursorLoader(this, FeedHelper.CONTENT_URI,
                FeedHelper.getProjection(), FeedHelper.CHANNELID + " = '" + channel.getId() + "'", null, FeedHelper.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG, "onLoadFinished: " + data.getCount());
        mAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void onEventAsync(FeedStarEvent event) {
        Log.i(TAG, "onEventAsync");
        Feed feed = event.feed;
        feed.setIsStarred(!feed.getIsStarred());
        //TODO: seed request to server
        FeedDao feedDao = daoSession.getFeedDao();
        feedDao.update(feed);
        getContentResolver().notifyChange(FeedHelper.CONTENT_URI, null);
    }
}
