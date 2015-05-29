package im.youtiao.android_client.ui.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.data.SyncManager;
import im.youtiao.android_client.greendao.Channel;
import im.youtiao.android_client.greendao.ChannelDao;
import im.youtiao.android_client.greendao.ChannelHelper;
import im.youtiao.android_client.greendao.DaoSession;
import im.youtiao.android_client.greendao.UserDao;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.Logger;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewChannelActivity extends RoboActionBarActivity {
    private static final String TAG = NewChannelActivity.class
            .getCanonicalName();

    @InjectView(R.id.add_todo_edittext)
    private EditText mTitle;

    @Inject private RemoteApi remoteApi;

    @Inject private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_channel);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_channel, menu);
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

    public void addNew(View v) {
        String name = mTitle.getText().toString().trim();
        if (name != null && name.length() != 0) {
            remoteApi.createChannel(name).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( resp -> {
                        ChannelDao channelDao = daoSession.getChannelDao();
                        Channel channel = new Channel();
                        channel.setName(name);
                        channel.setRole("owner");
                        channel.setUpdatedAt(resp.updatedAt);
                        channel.setCreatedAt(resp.createdAt);
                        channel.setServerId(resp.id);
                        channel.setUsersCount(resp.membershipsCount);
                        channelDao.insertOrReplace(channel);
                        getContentResolver().notifyChange(ChannelHelper.CONTENT_URI, null);
                        finish();
                    }, Logger::logThrowable);
        }
    }
}
