package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;


import java.util.LinkedList;
import java.util.List;

import im.youtiao.android_client.AccountDescriptor;
import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.adapter.StampArrayAdapter;
import im.youtiao.android_client.dao.BulletinHelper;
import im.youtiao.android_client.dao.DaoHelper;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.data.SyncManager;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Stamp;
import im.youtiao.android_client.model.User;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.ui.widget.ProgressHUD;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import im.youtiao.android_client.util.Log;
import im.youtiao.android_client.util.TimeWrap;
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

    @InjectView(R.id.lv_stamps) ListView stampsLv;

    @InjectView(R.id.tv_bulletin_text) TextView feedContentTv;
    @InjectView(R.id.tv_group_name) TextView groupNameTv;
    @InjectView(R.id.tv_created_info) TextView createdInfoTv;
    @InjectView(R.id.imgBtn_bulletin_check) ImageView checkImgBtn;
    @InjectView(R.id.tv_bulletin_checks_count) TextView checksCountTv;
    @InjectView(R.id.imgBtn_bulletin_cross) ImageView crossImgBtn;
    @InjectView(R.id.tv_bulletin_crosses_count) TextView crossesCountTv;
    @InjectView(R.id.imgBtn_bulletin_eye) ImageView eyeImgBtn;
    @InjectView(R.id.tv_bulletin_eyes_count) TextView eyesCountTv;
    @InjectView(R.id.layout_check) LinearLayout checkLayout;
    @InjectView(R.id.layout_cross) LinearLayout crossLayout;

    @Inject
    RemoteApi remoteApi;
    @Inject
    private SyncManager syncManager;

    Bulletin bulletin;

    private LinkedList<Stamp> stamps = new LinkedList<Stamp>();
    StampArrayAdapter mAdapter;

    ProgressHUD progressDialog;

    public YTApplication getApp() {
        return (YTApplication) getApplication();
    }

    @Override
    public void onStart() {
        Log.i(TAG, "OnStart");
        super.onStart();
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

        initBulletin();
        mAdapter = new StampArrayAdapter(this, R.layout.row_stamp, stamps, getApp().getCurrentAccount().getId());
        stampsLv.setAdapter(mAdapter);
        sync();
    }

    private void initBulletin() {
        feedContentTv.setText(Html.fromHtml("<b>" + bulletin.createdBy.name + ": " + "</b>" + bulletin.text));
        String createdAt = (TimeWrap.wrapTimeDisplyValue(Math.round(1000 * Double.parseDouble(bulletin.createdAt)), this));
        createdInfoTv.setText(createdAt);
        groupNameTv.setText(bulletin.group.name);
        checksCountTv.setText("" + bulletin.checksCount);
        crossesCountTv.setText("" + bulletin.crossesCount);
        int readCount = bulletin.checksCount + bulletin.crossesCount + bulletin.eyesCount;
        eyesCountTv.setText("" + readCount + "/" + bulletin.group.membershipsCount);

        checkImgBtn.setColorFilter(getResources().getColor(R.color.icon_unselected_color));
        crossImgBtn.setColorFilter(getResources().getColor(R.color.icon_unselected_color));
        eyeImgBtn.setColorFilter(getResources().getColor(R.color.icon_unselected_color));
        checksCountTv.setTextColor(getResources().getColor(R.color.icon_unselected_color));
        crossesCountTv.setTextColor(getResources().getColor(R.color.icon_unselected_color));
        if (bulletin.stamp != null && bulletin.stamp.symbol != null) {
            switch (Stamp.Mark.valueOf(bulletin.stamp.symbol.toUpperCase())) {
                case CHECK:
                    checkImgBtn.setColorFilter(getResources().getColor(R.color.icon_stamp_check_selected_color));
                    checksCountTv.setTextColor(getResources().getColor(R.color.icon_stamp_check_selected_color));
                    break;
                case CROSS:
                    crossImgBtn.setColorFilter(getResources().getColor(R.color.icon_stamp_cross_selected_color));
                    crossesCountTv.setTextColor(getResources().getColor(R.color.icon_stamp_cross_selected_color));
                    break;
                case EYE:
                    eyeImgBtn.setColorFilter(getResources().getColor(R.color.icon_stamp_eye_selected_color));
                default:
            }
        }

        groupNameTv.setOnClickListener(v -> {;
            Bundle data = new Bundle();
            data.putSerializable(GroupProfileActivity.PARAM_GROUP, bulletin.group);
            Intent intent = new Intent(this, GroupProfileActivity.class);
            intent.putExtras(data);
            startActivity(intent);
        });

        checkLayout.setOnClickListener(v -> {
            //EventBus.getDefault().post(new BulletinStampEvent(bulletin, Stamp.Mark.CHECK.toString().toLowerCase()));
            Log.i(TAG, "checkLayout click");
            onBulletinStamp(Stamp.Mark.CHECK.toString().toLowerCase());
        });

        crossLayout.setOnClickListener(v -> {
            //EventBus.getDefault().post(new BulletinStampEvent(bulletin, Stamp.Mark.CROSS.toString().toLowerCase()));
            onBulletinStamp(Stamp.Mark.CROSS.toString().toLowerCase());
        });
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
            if (bulletin.stamp == null) {
                onBulletinStamp(Stamp.Mark.EYE.toString().toLowerCase());
            }
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

    void onBulletinStamp(String symbol) {
        AppObservable.bindActivity(this, remoteApi.markBulletin(bulletin.id, symbol))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(resp -> {
                    this.bulletin = resp;
                    DaoHelper.insertOrUpdate(daoSession, BulletinWrap.validate(resp));
                    getContentResolver().notifyChange(BulletinHelper.CONTENT_URI, null);
                    initBulletin();
                    updateStamps();
                }, error -> NetworkExceptionHandler.handleThrowable(error, this));
    }

    void updateStamps() {
        boolean exist = false;
        for (Stamp stamp : stamps ) {
            if (stamp.createdById.equalsIgnoreCase(getApp().getCurrentAccount().getId())) {
                stamp.symbol = bulletin.stamp.symbol;
                stamp.createdAt = bulletin.stamp.createdAt;
                exist = true;
            }
        }
        if (!exist) {
            if (bulletin.stamp.createdBy == null) {
                AccountDescriptor account = getApp().getCurrentAccount();
                User user = new User();
                user.id = account.getId();
                user.name = account.getName();
                user.email = account.getEmail();
                bulletin.stamp.createdBy = user;
            }
            stamps.addFirst(bulletin.stamp);
        }
        mAdapter.notifyDataSetChanged();
    }
}
