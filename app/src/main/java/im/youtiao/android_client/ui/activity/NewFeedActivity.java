package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.greendao.Channel;
import im.youtiao.android_client.greendao.ChannelDao;
import im.youtiao.android_client.greendao.DaoSession;
import im.youtiao.android_client.greendao.Feed;
import im.youtiao.android_client.greendao.FeedDao;
import im.youtiao.android_client.greendao.FeedHelper;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.Logger;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewFeedActivity extends RoboActionBarActivity {

    public static final String PARAM_CHANNEL = "current_channel";

    @InjectView(R.id.tv_feed_recevier)
    TextView feedReceiverTv;

    @InjectView(R.id.edtTxt_feed_content)
    EditText feedContentEdtTxt;

    @InjectView(R.id.btn_add_feed)
    Button addFeedBtn;

    private Channel channel;

    @Inject
    RemoteApi remoteApi;

    @Inject
    DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feed);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        channel = (Channel) intent.getSerializableExtra(PARAM_CHANNEL);
        feedReceiverTv.setText(channel.getName());

        addFeedBtn.setOnClickListener( v -> {
            String content = feedContentEdtTxt.getText().toString().trim();
            if (content != null && content.length() != 0) {
                remoteApi.createChannelFeed(channel.getServerId(), content).observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> {
                            FeedDao feedDao = daoSession.getFeedDao();
                            Feed feed = new Feed();
                            feed.setServerId(resp.id);
                            feed.setCreatedAt(resp.createdAt);
                            feed.setChannelId(channel.getId());
                            feed.setCreatedBy(((YTApplication) getApplication()).getCurrentUser().getId());
                            feed.setText(resp.text);
                            feedDao.insert(feed);
                            getContentResolver().notifyChange(FeedHelper.CONTENT_URI, null);
                            finish();
                        }, Logger::logThrowable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
