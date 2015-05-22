package im.youtiao.android_client.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.FeedAdapter;
import im.youtiao.java_sdk.core.Feed;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFeedsFragmentInteractionListener}
 * interface.
 */
public class FeedsFragment extends ListFragment implements FeedAdapter.FeedAdapterDelegate {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int ID_CHECK = 1;
    private static final int ID_CROSS = 2;
    private static final int ID_QUESTION = 3;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FeedAdapter feedAdapter;
    private QuickAction quickAction;

    private OnFeedsFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    public static FeedsFragment newInstance(String param1, String param2) {
        FeedsFragment fragment = new FeedsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeedsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        ArrayList<Feed> feeds = new ArrayList<Feed>();
        for (int i = 0; i < 10; i++) {
            Feed feed = new Feed();
            feed.setId("A75gyQR2");
            feed.setContent("Applications do not normally need to");
            feed.setChannelId("A75gyQR2");
            feed.setCreatorId("A75gyQR2");
            feed.setUpdatedAt(new Date());
            feeds.add(feed);
        }
        feedAdapter = new FeedAdapter(getActivity(), R.layout.row_feed, feeds);
        setListAdapter(feedAdapter);

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
        feedAdapter.setDelegate(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        feedAdapter.setDelegate(null);
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
            mListener.onFeedsFragmentInteraction(feedAdapter.getItem(position).getId());
        }
    }

    @Override
    public void toggleStar(View v, Feed feed) {

        ImageButton imageButton = (ImageButton) v;
        feed.setIsStarred(!feed.isStarred());
        //TODO: seed request to server
        feedAdapter.notifyDataSetChanged();
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
                        fd.setIsChecked(true);
                        fd.setIsCrossed(false);
                        fd.setIsQuestioned(false);
                        break;
                    case ID_CROSS:
                        fd.setIsChecked(false);
                        fd.setIsCrossed(true);
                        fd.setIsQuestioned(false);
                        break;
                    case ID_QUESTION:
                        fd.setIsChecked(false);
                        fd.setIsCrossed(false);
                        fd.setIsQuestioned(true);
                        break;
                }
                feedAdapter.notifyDataSetChanged();
            }
        });

        quickAction.show(v);
    }

    @Override
    public void extendComment() {
        //TODO:
    }


    public interface OnFeedsFragmentInteractionListener {
        public void onFeedsFragmentInteraction(String id);
    }

}
