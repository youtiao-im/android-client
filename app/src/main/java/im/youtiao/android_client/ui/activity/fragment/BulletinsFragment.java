package im.youtiao.android_client.ui.activity.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.BulletinCursorAdapter;
import im.youtiao.android_client.dao.BulletinDao;
import im.youtiao.android_client.dao.BulletinHelper;
import im.youtiao.android_client.dao.DaoHelper;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import im.youtiao.android_client.wrap.BulletinWrap;
import im.youtiao.android_client.util.Log;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    private static int LIMIT = 25;

    private PtrClassicFrameLayout mPtrFrame;

    @InjectView(R.id.lv_bulletin_list)
    ListView bulletinLv;

    private BulletinCursorAdapter mAdapter;

    @Inject
    DaoSession daoSession;

    @Inject
    RemoteApi remoteApi;

    private OnBulletinsFragmentInteractionListener mListener;

    private static final int URL_LOADER = 1922;

    private static final String DEFAULT_SORT_ORDER = " " + BulletinHelper.CREATEDAT + " DESC";

    int mLastSavedFirstVisibleItem = -1;
    boolean mMoreDataAvailable = true;
    boolean isInit = true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BulletinsFragment() {
    }


    public boolean hasMoreDate() {
        return mMoreDataAvailable;
    }

    public boolean isInit() {
        return this.isInit;
    }

    @Override
    public void onStart() {
        Log.i(TAG, "OnStart");
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(URL_LOADER, null, this);
        refreshData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_bulletins, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG, "OnViewCreated");
        super.onViewCreated(view, savedInstanceState);

        //bulletinLv.setDividerHeight(20);
        mAdapter = new BulletinCursorAdapter(getActivity(), this);
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
                Log.i(TAG, "OnItemClick: " + position);
                if (null != mListener) {
                    Cursor cursor = (Cursor) mAdapter.getItem(position);
                    Bulletin bulletin = BulletinWrap.wrap(BulletinHelper.fromCursor(cursor));
                    mListener.onBulletinClick(bulletin);
                }
            }
        });

        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_list_view_frame);
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
//        mPtrFrame.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPtrFrame.autoRefresh();
//            }
//        }, 100);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    @Override
    public void onResume() {
        //Log.i(TAG, "OnResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        //Log.i(TAG, "OnPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        //Log.i(TAG, "OnStop");
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
        return new CursorLoader(getActivity(), BulletinHelper.CONTENT_URI,
                BulletinHelper.getProjection(), null, null, DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
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
        AppObservable.bindFragment(this, remoteApi.listBulletins(null, LIMIT))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    BulletinDao bulletinDao = daoSession.getBulletinDao();
                    bulletinDao.deleteAll();
                    for (Bulletin item : resp) {
                        DaoHelper.insertOrUpdate(daoSession, BulletinWrap.validate(item));
                    }
                    if (resp.size() >= LIMIT) {
                        mMoreDataAvailable = true;
                    } else {
                        mMoreDataAvailable = false;
                    }
                    isInit = false;
                    mPtrFrame.refreshComplete();
                    getActivity().getContentResolver().notifyChange(BulletinHelper.CONTENT_URI, null);
                }, error -> {
                    mPtrFrame.refreshComplete();
                    NetworkExceptionHandler.handleThrowable(error, getActivity());
                });
    }

    private void loadMoreData() {
        Log.i(TAG, "load More");
        im.youtiao.android_client.dao.Bulletin oldestBulletin = DaoHelper.getOldestBulletin(daoSession);
        String lastBulletinId = oldestBulletin == null ? null : oldestBulletin.getServerId();

        AppObservable.bindFragment(this, remoteApi.listBulletins(lastBulletinId, LIMIT))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    for (Bulletin item : resp) {
                        DaoHelper.insertOrUpdate(daoSession, BulletinWrap.validate(item));
                    }
                    if (resp.size() >= LIMIT) {
                        mMoreDataAvailable = true;
                    } else {
                        mMoreDataAvailable = false;
                    }
                    isInit = false;
                    getActivity().getContentResolver().notifyChange(BulletinHelper.CONTENT_URI, null);
                }, error -> NetworkExceptionHandler.handleThrowable(error, getActivity()));
    }

}
