package com.androidsocialnetworks.app.fragment;

import android.widget.Toast;

import com.androidsocialnetworks.app.activity.MainActivity;
import com.androidsocialnetworks.app.fragment.base.BaseFragment;

public class LinkedInFragment extends BaseFragment {

    public static LinkedInFragment newInstance() {
        return new LinkedInFragment();
    }

    @Override
    protected void onConnectDisconnectButtonClick() {
        Toast.makeText(getActivity(), "Connect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("LinkedIn");
    }
}
