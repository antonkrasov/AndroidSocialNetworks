package com.androidsocialnetworks.app.fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.androidsocialnetworks.app.activity.MainActivity;
import com.androidsocialnetworks.app.fragment.base.BaseFragment;
import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;

public class TwitterFragment extends BaseFragment {

    private TwitterSocialNetwork mTwitterSocialNetwork;

    public static TwitterFragment newInstance() {
        return new TwitterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTwitterSocialNetwork = getMainActivity().getSocialNetworkManager().getTwitterSocialNetwork();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Twitter");
    }

    @Override
    protected void onConnectDisconnectButtonClick() {
        Toast.makeText(getActivity(), "Connect", Toast.LENGTH_SHORT).show();
    }
}
