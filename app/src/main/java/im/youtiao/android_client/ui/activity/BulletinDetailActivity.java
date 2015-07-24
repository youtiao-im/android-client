package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.inject.Inject;


import java.util.LinkedList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;
import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.StampArrayAdapter;
import im.youtiao.android_client.dao.BulletinHelper;
import im.youtiao.android_client.dao.DaoHelper;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.data.SyncManager;
import im.youtiao.android_client.event.BulletinStampEvent;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Stamp;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.ui.widget.ProgressHUD;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import im.youtiao.android_client.util.Log;
import im.youtiao.android_client.wrap.BulletinWrap;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import com.umeng.analytics.MobclickAgent;

public class BulletinDetailActivity extends RoboActionBarActivity {
    private static final String TAG = BulletinDetailActivity.class.getCanonicalName();

    public static final String PARAM_BULLETIN = "current_bulletin";

    @Inject
    DaoSession daoSession;

    @InjectView(R.id.lv_stamps)
    ListView stampsLv;

    @Inject
    RemoteApi remoteApi;
    @Inject
    private SyncManager syncManager;

    Bulletin bulletin;

    private LinkedList<Stamp> stamps = new LinkedList<Stamp>();
    StampArrayAdapter mAdapter;

    ProgressHUD progressDialog;

    @Override
    public void onStart() {
        Log.i(TAG, "OnStart");
        super.onStart();
        sync();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        bulletin = (Bulletin) intent.getSerializableExtra(PARAM_BULLETIN);

        mAdapter = new StampArrayAdapter(this, R.layout.row_stamp, stamps);
        stampsLv.setAdapter(mAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bulletin_detail, menu);
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

    void sync() {
        progressDialog = ProgressHUD.show(this, "", true, true, null);
        stamps.clear();
        startStampsSyncing();
        progressDialog.dismiss();
    }

    void processStamps(List<Stamp> stampList) {
        for (Stamp item : stampList) {
            stamps.add(item);
        }
        if (stampList.size() > 0) {
            startStampsSyncing();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    void startStampsSyncing() {
        String lastStampId = null;
        if (stamps.size() > 0) {
            lastStampId = stamps.getLast().id;
        }
        AppObservable.bindActivity(this, remoteApi.listStamps(bulletin.id, lastStampId, 100))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    processStamps(resp);
                }, error -> {
                    progressDialog.dismiss();
                    NetworkExceptionHandler.handleThrowable(error, this);
                });
    }
}
