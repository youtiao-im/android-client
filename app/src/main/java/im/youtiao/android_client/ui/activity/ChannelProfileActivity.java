package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.greendao.Channel;
import im.youtiao.android_client.greendao.DaoSession;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class ChannelProfileActivity extends RoboActionBarActivity {
    private static final String TAG = ChannelProfileActivity.class.getCanonicalName();
    public static final String PARAM_CHANNEL = "current_channel";

    private Channel channel;

    @InjectView(R.id.tv_channel_name) TextView channelNameTv;
    @InjectView(R.id.tv_channel_number) TextView channelNumTv;
    @InjectView(R.id.tv_channel_code) TextView channelCodeTv;
    @InjectView(R.id.tv_channel_admin) TextView channelAdminTv;
    @InjectView(R.id.tv_channel_members_count) TextView channelMembersCountTv;
    @InjectView(R.id.iv_channel_memeber_forward) ImageView channelMemberForwardImageView;

    @Inject DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        channel = (Channel) intent.getSerializableExtra(PARAM_CHANNEL);

        channel.__setDaoSession(daoSession);
        channelNameTv.setText(channel.getName());
        channelNumTv.setText(channel.getServerId());
        channelCodeTv.setText("");
        channelAdminTv.setText(channel.getUser().getEmail());
        channelMembersCountTv.setText("" + channel.getUsersCount());

        channelMemberForwardImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putSerializable(ChannelMemberActivity.PARAM_CHANNEL, channel);
                Intent intent = new Intent(ChannelProfileActivity.this, ChannelMemberActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_channel_profile, menu);
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
