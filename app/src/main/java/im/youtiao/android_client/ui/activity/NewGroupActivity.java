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
import im.youtiao.android_client.dao.GroupDao;
import im.youtiao.android_client.dao.GroupHelper;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.Logger;
import im.youtiao.android_client.wrap.GroupWrap;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewGroupActivity extends RoboActionBarActivity {
    private static final String TAG = NewGroupActivity.class
            .getCanonicalName();

    @InjectView(R.id.add_todo_edittext)
    private EditText mTitle;

    @Inject private RemoteApi remoteApi;

    @Inject private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_group, menu);
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
            remoteApi.createGroup(name).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( resp -> {
                        GroupDao groupDao = daoSession.getGroupDao();
                        groupDao.insertOrReplace(GroupWrap.validate(resp));
                        getContentResolver().notifyChange(GroupHelper.CONTENT_URI, null);
                        finish();
                    }, Logger::logThrowable);
        }
    }
}
