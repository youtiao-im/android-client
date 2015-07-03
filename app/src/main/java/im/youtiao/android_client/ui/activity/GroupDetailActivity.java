package im.youtiao.android_client.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.LinkedList;

import de.greenrobot.event.EventBus;
import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.BulletinArrayAdapter;
import im.youtiao.android_client.dao.BulletinDao;
import im.youtiao.android_client.dao.BulletinHelper;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.data.SyncManager;
import im.youtiao.android_client.event.BulletinStampEvent;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.Logger;
import im.youtiao.android_client.wrap.BulletinWrap;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupDetailActivity extends RoboActionBarActivity {
    private static final String TAG = GroupDetailActivity.class.getCanonicalName();
    public static final String PARAM_GROUP = "current_group";

    private Group group;

    private static int LIMIT = 5;

    private PtrClassicFrameLayout mPtrFrame;

    @InjectView(R.id.lv_bulletin_list)
    ListView bulletinLv;

    private BulletinArrayAdapter mAdapter;
    @Inject
    private DaoSession daoSession;
    @Inject
    private RemoteApi remoteApi;
    @Inject
    private SyncManager syncManager;
    private LinkedList<Bulletin> bulletins = new LinkedList<Bulletin>();

    int mLastSavedFirstVisibleItem = -1;
    boolean mMoreDataAvailable = true;
    boolean isInit = true;

    public boolean hasMoreDate() {
        return mMoreDataAvailable;
    }

    public boolean isInit() {
        return this.isInit;
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra(PARAM_GROUP);
        setTitle(group.name);

        mAdapter = new BulletinArrayAdapter(this, R.layout.row_bulletin, bulletins);
        bulletinLv.setDividerHeight(20);
        bulletinLv.setAdapter(mAdapter);
        bulletinLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (visibleItemCount > 0
                        && (firstVisibleItem + visibleItemCount == totalItemCount)) {
                    // only process first event
                    if (firstVisibleItem != mLastSavedFirstVisibleItem) {
                        Log.i(TAG, "onScroll: firstVisibleItem=" + firstVisibleItem + ", visibleItemCount=" + visibleItemCount + ", totalItemCount=" + totalItemCount
                                + ", mLastSavedFirstVisibleItem=" + mLastSavedFirstVisibleItem);
                        Log.i(TAG, "onLastItemVisible");
                        mLastSavedFirstVisibleItem = firstVisibleItem;
                        loadMoreData();
                    }
                }
            }
        });

        bulletinLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "OnItemClick");
                Bulletin bulletin = (Bulletin) mAdapter.getItem(position);
                Bundle data = new Bundle();
                data.putSerializable(BulletinDetailActivity.PARAM_BULLETIN, bulletin);
                Intent intent = new Intent(GroupDetailActivity.this, BulletinDetailActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        mPtrFrame.setPullToRefresh(false);
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                //mPtrFrame.autoRefresh();
            }
        }, 100);

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ("owner".equalsIgnoreCase(group.membership.role)) {
            getMenuInflater().inflate(R.menu.menu_group_detail_owner, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_group_detail_member, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_group_profile:
                Bundle data = new Bundle();
                data.putSerializable(GroupProfileActivity.PARAM_GROUP, group);
                Intent intent = null;
                if (group.membership.role.equalsIgnoreCase(Group.Role.OWNER.toString())) {
                    intent = new Intent(GroupDetailActivity.this, GroupProfileActivity.class);
                } else {
                    intent = new Intent(GroupDetailActivity.this, JoinedGroupProfileActivity.class);
                }
                intent.putExtras(data);
                startActivityForResult(intent, 0);
                return true;
            case R.id.action_new_bulletin:
                Bundle data2 = new Bundle();
                data2.putSerializable(NewBulletinActivity.PARAM_GROUP, group);
                Intent intent2 = new Intent(GroupDetailActivity.this, NewBulletinActivity.class);
                intent2.putExtras(data2);
                startActivityForResult(intent2, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        Log.i(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == 0 && resultCode == 0 && intent != null) {
            this.finish();
        } else if (requestCode == 1 && resultCode == 1 && intent != null) {
            Bulletin bulletin = (Bulletin)intent.getSerializableExtra(NewBulletinActivity.PARAM_NEW_BULLETIN);
            bulletins.addFirst(bulletin);
            mAdapter.notifyDataSetChanged();

            //TODO: adjust sort
            BulletinDao bulletinDao = daoSession.getBulletinDao();
            bulletinDao.insert(BulletinWrap.validate(bulletin));
            getContentResolver().notifyChange(BulletinHelper.CONTENT_URI, null);
        }
    }

    public void loadInitData() {
        Log.i(TAG, "loadInitData");

    }

    public void refreshData() {
        Log.i(TAG, "refreshData");
        remoteApi.listGroupBulletins(group.id, null, LIMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    bulletins.clear();
                    bulletins.addAll(resp);
                    mAdapter.notifyDataSetChanged();
                    if (resp.size() < LIMIT) {
                        mMoreDataAvailable = false;
                    } else {
                        mMoreDataAvailable = true;
                    }
                    mLastSavedFirstVisibleItem = -1;
                    isInit = false;
                    mPtrFrame.refreshComplete();
                    mAdapter.notifyDataSetChanged();
                }, Logger::logThrowable);
    }

    public void loadMoreData() {
        Log.i(TAG, "load More");
        String before_id = null;
        if (bulletins.size() > 0) {
            Bulletin lastBulletin = bulletins.get(bulletins.size() - 1);
            before_id = lastBulletin.id;
        }
        remoteApi.listGroupBulletins(group.id, before_id, LIMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    bulletins.addAll(resp);
                    if (resp.size() < LIMIT) {
                        mMoreDataAvailable = false;
                    } else {
                        mMoreDataAvailable = true;
                    }
                    isInit = false;
                    mAdapter.notifyDataSetChanged();
                }, Logger::logThrowable);
    }

    public void onEventMainThread(BulletinStampEvent event) {
        Log.i(TAG, "onEventAsync");
        Bulletin bulletin = event.bulletin;
        String symbol = event.symbol;
        int index = bulletins.indexOf(bulletin);
        if (index != -1) {
            remoteApi.markBulletin(bulletin.id, symbol)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(resp -> {
                        bulletins.remove(index);
                        bulletins.add(index, resp);
                        mAdapter.notifyDataSetChanged();
                    }, Logger::logThrowable);
        }
    }


}
