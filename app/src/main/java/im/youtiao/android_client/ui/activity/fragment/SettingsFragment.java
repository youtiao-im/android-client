package im.youtiao.android_client.ui.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.ui.activity.dummy.DummyContent;
import roboguice.fragment.RoboFragment;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnProfileFragmentInteractionListener}
 * interface.
 */
public class SettingsFragment extends RoboFragment {


    private OnProfileFragmentInteractionListener mListener;

    Button signOutBtn;


    YTApplication getApp() {
        return (YTApplication)getActivity().getApplication();
    }

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        signOutBtn = (Button)view.findViewById(R.id.btn_sign_out);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApp().signOutAccount(getApp().getCurrentAccount().getId());
                mListener.onSignOut();
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnProfileFragmentInteractionListener) activity;
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


    public interface OnProfileFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSignOut();
    }

}
