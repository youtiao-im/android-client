package im.youtiao.android_client.ui.activity.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.inject.Inject;

import im.youtiao.android_client.R;

import im.youtiao.android_client.adapter.ChannelAdapter;
import im.youtiao.android_client.data.SyncManager;
import im.youtiao.android_client.greendao.Channel;
import im.youtiao.android_client.greendao.ChannelHelper;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class ChannelsFragment extends RoboFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ChannelsFragment.class
            .getCanonicalName();

    private OnChannelsFragmentInteractionListener mListener;

    @InjectView(android.R.id.list)
    private AbsListView mListView;
    @Inject private SyncManager syncManager;
    private ChannelAdapter mAdapter;

    private static final int URL_LOADER = 1921;

    public ChannelsFragment() {
    }

    @Override public void onStart() {
        Log.i(TAG, "OnStart");
        super.onStart();
        syncManager.startChannelsSync();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "OnCreateView");
        return inflater.inflate(R.layout.fragment_channels, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG, "OnViewCreate");
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new ChannelAdapter(this.getActivity(), null);
        mListView.setAdapter(mAdapter);
        Button addNewChannelButton = (Button) view.findViewById(R.id.add_new_channel_button);
        addNewChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNewChannelButtonClick();
            }
        });

        Button joinChannelButton = (Button) view.findViewById(R.id.join_channel_button);
        joinChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onJoinChannelButtonClick();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "OnItemClick");
                if (null != mListener) {
                    Cursor cursor = (Cursor) mAdapter.getItem(position);
                    Channel channel = ChannelHelper.fromCursor(cursor);
                    mListener.onChannelsItemClick(channel);
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG, "OnAttach");
        super.onAttach(activity);
        try {
            mListener = (OnChannelsFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "OnDetach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "OnCreateLoader");
        return new CursorLoader(getActivity(), ChannelHelper.CONTENT_URI,
                ChannelHelper.getProjection(), null, null, ChannelHelper.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG, "onLoadFinished");
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    public interface OnChannelsFragmentInteractionListener {
        public void onChannelsItemClick(Channel channel);

        public void onNewChannelButtonClick();

        public void onJoinChannelButtonClick();
    }
}
