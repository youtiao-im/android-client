package im.youtiao.android_client.ui.activity.fragment;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import im.youtiao.android_client.R;
import im.youtiao.android_client.ui.activity.LoginActivity;
import im.youtiao.android_client.ui.activity.MainActivity;
import im.youtiao.android_client.ui.activity.NewChannelActivity;
import im.youtiao.android_client.adapter.ChannelsCursorAdapter;
import roboguice.fragment.RoboFragment;

public class DemoFragment extends RoboFragment {

    private static final String TAG = DemoFragment.class.getCanonicalName();

    private OnDemoFragmentInteractionListener mListener;
    private ExpandableListView myChannelsListView;

    private boolean[] groupExpandedArray;
    private int firstVisiblePosition;

    private ChannelsCursorAdapter myChannelsAdapter;

    public DemoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_demo, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        myChannelsListView = (ExpandableListView)getView().findViewById(R.id.channels_listview);
        myChannelsAdapter = new ChannelsCursorAdapter(this.getActivity());
        myChannelsListView.setAdapter(myChannelsAdapter);

        TypedArray expandableListViewStyle = getActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.expandableListViewStyle});
        TypedArray groupIndicator = getActivity().getTheme().obtainStyledAttributes(expandableListViewStyle.getResourceId(0,0),new int[]{android.R.attr.groupIndicator});
        myChannelsListView.setGroupIndicator(groupIndicator.getDrawable(0));
        expandableListViewStyle.recycle();
        groupIndicator.recycle();

        int groupCount = myChannelsListView.getCount();
        for (int i=0; i<groupCount; i++) {
            myChannelsListView.expandGroup(i);
        };

        Button addNewChannelButton = (Button) getView().findViewById(R.id.add_new_channel_button);
        addNewChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(new Intent(getActivity(), NewChannelActivity.class), 1);
                mListener.onNewChannelButtonClick();
            }
        });

        Button joinChannelButton = (Button) getView().findViewById(R.id.join_channel_button);
        joinChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onJoinChannelButtonClick();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        Log.e(TAG, "onAttach");
        super.onAttach(activity);
        try {
            mListener = (OnDemoFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
        int numberOfGroups = myChannelsAdapter.getGroupCount();
        groupExpandedArray = new boolean[numberOfGroups];
        for ( int i = 0; i < numberOfGroups; i++ ) {
            groupExpandedArray[i] = myChannelsListView.isGroupExpanded(i);
        }
        firstVisiblePosition = myChannelsListView.getFirstVisiblePosition();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();

        if (groupExpandedArray != null && groupExpandedArray.length > 0 ) {
            for (int i = 0; i < groupExpandedArray.length; i++) {
                if (groupExpandedArray[i] == true)
                    myChannelsListView.expandGroup(i);
            }
            myChannelsListView.setSelection(firstVisiblePosition);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDemoFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onDemoFragmentInteraction(String id);

        public void onNewChannelButtonClick();

        public void onJoinChannelButtonClick();
    }

}
