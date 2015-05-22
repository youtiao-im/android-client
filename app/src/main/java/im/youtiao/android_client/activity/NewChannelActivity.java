package im.youtiao.android_client.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.ChannelDAO;
import im.youtiao.android_client.model.Channel;

public class NewChannelActivity extends ActionBarActivity {
    private EditText mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_channel);
        mTitle = (EditText) findViewById(R.id.add_todo_edittext);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addNew(View v) {
        String name = mTitle.getText().toString().trim();
        if (name != null && name.length() != 0) {
            Channel channel = new Channel();
            channel.setName(name);
            channel.setRole(Channel.Role.OWNER.toString().toLowerCase());
            ChannelDAO.getInstance().addNewChannel(getContentResolver(), channel);
            finish();
        }
    }
}
