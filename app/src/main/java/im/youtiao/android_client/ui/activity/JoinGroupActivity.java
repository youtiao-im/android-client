package im.youtiao.android_client.ui.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.rest.RemoteApi;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class JoinGroupActivity extends RoboActionBarActivity {

    private static final String TAG = JoinGroupActivity.class.getCanonicalName();

    @InjectView(R.id.joined_channel_name) private EditText mTitle;
    @Inject private RemoteApi remoteApi;
    @Inject
    private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        mTitle = (EditText) findViewById(R.id.joined_channel_name);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_join_group, menu);
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

    public void joinNew(View v) {
        String name = mTitle.getText().toString().trim();
        //TODO:
        if (name != null && name.length() != 0) {
//            remoteApi.createChannel(name).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(resp -> {
//                        ChannelDao channelDao = daoSession.getChannelDao();
//                        Channel channel = new Channel();
//                        channel.setName(name);
//                        channel.setRole("member");
//                        channel.setUpdatedAt(resp.updatedAt);
//                        channel.setCreatedAt(resp.createdAt);
//                        channel.setServerId(resp.id);
//                        channel.setUsersCount(resp.membershipsCount);
//                        channelDao.insertOrReplace(channel);
//                        getContentResolver().notifyChange(ChannelHelper.CONTENT_URI, null);
//                        finish();
//                    }, Logger::logThrowable);
        }
    }
}
