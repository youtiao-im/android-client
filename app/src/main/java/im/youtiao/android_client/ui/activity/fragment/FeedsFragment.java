package im.youtiao.android_client.ui.activity.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.FeedCursorAdapter;
import im.youtiao.android_client.data.State;
import im.youtiao.android_client.data.SyncManager;
import im.youtiao.android_client.greendao.DaoSession;
import im.youtiao.android_client.greendao.Feed;
import im.youtiao.android_client.greendao.FeedDao;
import im.youtiao.android_client.greendao.FeedHelper;
import roboguice.fragment.RoboListFragment;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFeedsFragmentInteractionListener}
 * interface.
 */
public class FeedsFragment extends RoboListFragment implements FeedCursorAdapter.FeedAdapterDelegate, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FeedsFragment.class.getCanonicalName();


    private static final int ID_CHECK = 1;
    private static final int ID_CROSS = 2;
    private static final int ID_QUESTION = 3;

    @Inject private FeedCursorAdapter mAdapter;
    private QuickAction quickAction;

    @Inject
    private SyncManager syncManager;

    @Inject
    private DaoSession daoSession;

    private OnFeedsFragmentInteractionListener mListener;

    private static final int URL_LOADER = 1922;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeedsFragment() {
    }

    @Override public void onStart() {
        Log.i(TAG, "OnStart");
        super.onStart();
        syncManager.startSync();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(mAdapter);

        ActionItem checkItem = new ActionItem(ID_CHECK, "Check", getResources().getDrawable(R.mipmap.ic_feed_stamp_check));
        ActionItem crossItem = new ActionItem(ID_CROSS, "Cross", getResources().getDrawable(R.mipmap.ic_feed_stamp_cross));
        ActionItem questionItem = new ActionItem(ID_QUESTION, "Question", getResources().getDrawable(R.mipmap.ic_feed_stamp_question));

        quickAction = new QuickAction(this.getActivity(), QuickAction.HORIZONTAL);
        quickAction.addActionItem(checkItem);
        quickAction.addActionItem(crossItem);
        quickAction.addActionItem(questionItem);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setDividerHeight(20);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setDelegate(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.setDelegate(null);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFeedsFragmentInteractionListener) activity;
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FeedsFragment", "OnListItemClick");
        super.onListItemClick(l, v, position, id);
        if (null != mListener) {
            Cursor cursor = (Cursor) mAdapter.getItem(position);
            Feed feed = FeedHelper.fromCursor(cursor);
            mListener.onFeedsFragmentInteraction(feed);
        }
    }

    @Override
    public void toggleStar(View v, Feed feed) {
        ImageButton imageButton = (ImageButton) v;
        feed.setIsStarred(!feed.getIsStarred());
        //TODO: seed request to server
        FeedDao feedDao = daoSession.getFeedDao();
        feedDao.update(feed);
        getActivity().getContentResolver().notifyChange(FeedHelper.CONTENT_URI, null);
    }

    @Override
    public void clickStamp(View v, Feed feed) {
        //TODO:
        final Feed fd = feed;
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                ActionItem actionItem = quickAction.getActionItem(pos);
                Log.i("FeedsFragment", "actionItem = " + actionItem.getActionId());
                switch (actionItem.getActionId()) {
                    case ID_CHECK:
                        fd.setSymbol(State.Mark.CHECK.toString());
                        break;
                    case ID_CROSS:
                        fd.setSymbol(State.Mark.CROSS.toString());
                        break;
                    case ID_QUESTION:
                        fd.setSymbol(State.Mark.QUESTION.toString());
                        break;
                }
                FeedDao feedDao = daoSession.getFeedDao();
                feedDao.update(fd);
                getActivity().getContentResolver().notifyChange(FeedHelper.CONTENT_URI, null);
            }
        });

        quickAction.show(v);
    }

    @Override
    public void extendComment() {
        //TODO:
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "OnCreateLoader");
        return new CursorLoader(getActivity(), FeedHelper.CONTENT_URI,
                FeedHelper.getProjection(), null, null, FeedHelper.DEFAULT_SORT_ORDER);
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


    public interface OnFeedsFragmentInteractionListener {
        public void onFeedsFragmentInteraction(Feed feed);
    }

}
