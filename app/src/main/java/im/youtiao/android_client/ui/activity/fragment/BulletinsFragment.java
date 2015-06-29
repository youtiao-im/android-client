package im.youtiao.android_client.ui.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.inject.Inject;

import cn.trinea.android.common.view.DropDownListView;
import de.greenrobot.event.EventBus;
import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.BulletinCursorAdapter;
import im.youtiao.android_client.dao.BulletinDao;
import im.youtiao.android_client.dao.BulletinHelper;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.dao.GroupHelper;
import im.youtiao.android_client.data.DaoHelper;
import im.youtiao.android_client.data.SyncManager;
import im.youtiao.android_client.event.BulletinCommentClickEvent;
import im.youtiao.android_client.event.BulletinStampEvent;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.ui.activity.BulletinDetailActivity;
import im.youtiao.android_client.util.Logger;
import im.youtiao.android_client.wrap.BulletinWrap;
import im.youtiao.android_client.wrap.GroupWrap;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A fragment representing a list of Items.
 * <p>
 * <p>
 * Activities containing this fragment MUST implement the {@link OnBulletinsFragmentInteractionListener}
 * interface.
 */
public class BulletinsFragment extends RoboFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = BulletinsFragment.class.getCanonicalName();


    private static final int ID_CHECK = 1;
    private static final int ID_CROSS = 2;
    private static final int ID_QUESTION = 3;

    private static int LIMIT = 5;


    @InjectView(R.id.lv_bulletin_list)
    DropDownListView bulletinLv;

    @Inject
    private BulletinCursorAdapter mAdapter;

    @Inject
    DaoSession daoSession;

    @Inject
    RemoteApi remoteApi;

    private OnBulletinsFragmentInteractionListener mListener;

    private static final int URL_LOADER = 1922;

    private static final String DEFAULT_SORT_ORDER = " " + BulletinHelper.ID;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BulletinsFragment() {
    }

    @Override
    public void onStart() {
        Log.i(TAG, "OnStart");
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bulletin, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG, "OnViewCreated");
        super.onViewCreated(view, savedInstanceState);

        bulletinLv.setDividerHeight(20);
        bulletinLv.setAdapter(mAdapter);

        bulletinLv.setShowFooterWhenNoMore(true);
        bulletinLv.setOnDropDownListener(new DropDownListView.OnDropDownListener() {
            @Override
            public void onDropDown() {
                refreshData();
            }
        });

        bulletinLv.setOnBottomListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreData();
            }
        });

        bulletinLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "OnItemClick: " + position);
                if (null != mListener) {
                    Cursor cursor = (Cursor) mAdapter.getItem(position - 1);
                    Bulletin bulletin = BulletinWrap.wrap(BulletinHelper.fromCursor(cursor));
                    mListener.onBulletinClick(bulletin);
                }
            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "OnActivityCreated");
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    @Override
    public void onResume() {
        Log.i(TAG, "OnResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "OnPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "OnStop");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnBulletinsFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnChatsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "OnCreateLoader");
        return new CursorLoader(getActivity(), BulletinHelper.CONTENT_URI,
                BulletinHelper.getProjection(), null, null, DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG, "onLoadFinished: " + cursor.getCount());
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    public interface OnBulletinsFragmentInteractionListener {
        public void onBulletinClick(Bulletin bulletin);
    }

    private void refreshData() {
        Log.i(TAG, "refreshData");
        remoteApi.listBulletins(null, LIMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    BulletinDao bulletinDao = daoSession.getBulletinDao();
                    bulletinDao.deleteAll();
                    for (Bulletin item : resp) {
                        bulletinDao.insert(BulletinWrap.validate(item));
                    }
                    getActivity().getContentResolver().notifyChange(BulletinHelper.CONTENT_URI, null);
                    if (resp.size() >= LIMIT) {
                        bulletinLv.setHasMore(true);
                    } else {
                        bulletinLv.setHasMore(false);
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
                    bulletinLv.onDropDownComplete("updated at " + dateFormat.format(new Date()));
                    bulletinLv.onBottomComplete();
                }, Logger::logThrowable);
    }

    private void loadMoreData() {
        Log.i(TAG, "load More");
        BulletinDao bulletinDao = daoSession.getBulletinDao();
        String lastBulletinId = bulletinDao.count() > 0 ? bulletinDao.loadByRowId(bulletinDao.count()).getServerId() : null;
        remoteApi.listBulletins(lastBulletinId, LIMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    for (Bulletin item : resp) {
                        bulletinDao.insert(BulletinWrap.validate(item));
                    }
                    getActivity().getContentResolver().notifyChange(BulletinHelper.CONTENT_URI, null);
                    if (resp.size() < LIMIT) {
                        bulletinLv.setHasMore(false);
                    }
                    bulletinLv.onBottomComplete();
                }, Logger::logThrowable);
    }


    public void onEventAsync(BulletinStampEvent event) {
        Log.i(TAG, "onEventAsync");
        Bulletin bulletin = event.bulletin;
        String symbol = event.symbol;

        remoteApi.markBulletin(bulletin.id, symbol)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(resp -> {
                    DaoHelper.insertOrUpdate(daoSession, BulletinWrap.validate(resp));
                    getActivity().getContentResolver().notifyChange(BulletinHelper.CONTENT_URI, null);
                }, Logger::logThrowable);
    }
}
