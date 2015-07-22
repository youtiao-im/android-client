package im.youtiao.android_client.ui.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.youtiao.android_client.AccountDescriptor;
import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.model.User;
import im.youtiao.android_client.util.Log;
import roboguice.fragment.RoboFragment;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnProfileFragmentInteractionListener}
 * interface.
 */
public class SettingsFragment extends RoboFragment {
    private static final String TAG = SettingsFragment.class.getCanonicalName();
    private TextView emailTv;
    private TextView nameTv;

    private OnProfileFragmentInteractionListener mListener;

    YTApplication getApp() {
        return (YTApplication)getActivity().getApplication();
    }

    public SettingsFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG, "OnViewCreated");
        AccountDescriptor account = getApp().getCurrentAccount();
        emailTv = (TextView) view.findViewById(R.id.tv_email);
        emailTv.setText(account.getEmail());

        nameTv = (TextView) view.findViewById(R.id.tv_name);
        nameTv.setText(account.getName());

        RelativeLayout signOutLayout = (RelativeLayout)view.findViewById(R.id.layout_sign_out);
        signOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSignOut();
            }
        });

        RelativeLayout nameLayout = (RelativeLayout) view.findViewById(R.id.layout_name);
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAccountNameClick();
            }
        });

        RelativeLayout changePasswordLayout = (RelativeLayout) view.findViewById(R.id.layout_change_password);
        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onChangePasswordClick();
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

    public void updateAccount(User user) {
        emailTv.setText(user.email);
        nameTv.setText(user.name);
    }


    public interface OnProfileFragmentInteractionListener {
        public void onSignOut();
        public void onAccountNameClick();
        public void onChangePasswordClick();
    }

}
