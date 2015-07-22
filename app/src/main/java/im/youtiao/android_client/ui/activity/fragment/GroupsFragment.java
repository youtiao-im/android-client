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
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.GroupCursorAdapter;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.dao.GroupDao;
import im.youtiao.android_client.dao.GroupHelper;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.NetworkExceptionHandler;
import im.youtiao.android_client.wrap.GroupWrap;
import im.youtiao.android_client.util.Log;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import mehdi.sakout.fancybuttons.FancyButton;
import roboguice.fragment.RoboFragment;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class GroupsFragment extends RoboFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = GroupsFragment.class.getCanonicalName();
    private PtrClassicFrameLayout mPtrFrame;

    @Inject
    RemoteApi remoteApi;

    @Inject
    DaoSession daoSession;

    @Inject
    GroupCursorAdapter groupCursorAdapter;

    private OnGroupsFragmentInteractionListener mListener;

    private static final int URL_LOADER = 1921;

    private static final String DEFAULT_SORT_ORDER = " " + GroupHelper.ROLE + " DESC ";

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(URL_LOADER, null, this);
        updateData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGroupsFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG, "OnViewCreated");
        final ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        listView.setAdapter(groupCursorAdapter);

        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_list_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                updateData();
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
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
//        mPtrFrame.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPtrFrame.autoRefresh();
//            }
//        }, 100);

        FancyButton addNewChannelButton = (FancyButton) view.findViewById(R.id.add_new_channel_button);
        addNewChannelButton.setOnClickListener(v -> {
            mListener.onNewGroupButtonClick();
        });

        FancyButton joinChannelButton = (FancyButton) view.findViewById(R.id.join_channel_button);
        joinChannelButton.setOnClickListener(v -> {
            mListener.onJoinGroupButtonClick();
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "OnItemClick");
                if (null != mListener) {
                    Cursor cursor = (Cursor) groupCursorAdapter.getItem(position);
                    Group group = GroupWrap.wrap(GroupHelper.fromCursor(cursor));
                    mListener.onGroupItemClick(group);
                }
            }
        });
    }

    protected void updateData() {
        Log.i(TAG, "updateData");
        AppObservable.bindFragment(this, remoteApi.listGroups())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    Log.i(TAG, "groups size = " + resp.size());
                    GroupDao groupDao = daoSession.getGroupDao();
                    groupDao.deleteAll();
                    for (Group item : resp) {
                        groupDao.insertOrReplace(GroupWrap.validate(item));
                    }
                    Log.i(TAG, "groupDao size=" + groupDao.count());
                    mPtrFrame.refreshComplete();
                    getActivity().getContentResolver().notifyChange(GroupHelper.CONTENT_URI, null);
                }, error -> {
                    mPtrFrame.refreshComplete();
                    NetworkExceptionHandler.handleThrowable(error, getActivity());
                });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "OnCreateLoader");
        return new CursorLoader(getActivity(), GroupHelper.CONTENT_URI,
                GroupHelper.getProjection(), null, null, DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG, "onLoadFinished: ");
        groupCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        groupCursorAdapter.swapCursor(null);
    }

    public interface OnGroupsFragmentInteractionListener {
        public void onGroupItemClick(Group group);

        public void onNewGroupButtonClick();

        public void onJoinGroupButtonClick();
    }
}
