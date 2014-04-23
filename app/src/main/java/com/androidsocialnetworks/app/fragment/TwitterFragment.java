package com.androidsocialnetworks.app.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidsocialnetworks.app.activity.MainActivity;
import com.androidsocialnetworks.app.fragment.base.BaseFragment;
import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;

public class TwitterFragment extends BaseFragment implements SocialNetwork.OnLoginCompleteListener {
    private static final String TAG = TwitterFragment.class.getSimpleName();

    private TwitterSocialNetwork mTwitterSocialNetwork;

    public static TwitterFragment newInstance() {
        return new TwitterFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterSocialNetwork = getMainActivity().getSocialNetworkManager().getTwitterSocialNetwork();
        mTwitterSocialNetwork.setOnLoginCompleteListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Twitter");
    }

    @Override
    protected void onConnectDisconnectButtonClick() {
        Toast.makeText(getActivity(), "Connect", Toast.LENGTH_SHORT).show();

        mTwitterSocialNetwork.login();

        switchUIState(UIState.PROGRESS);
    }

    @Override
    public void onLoginSuccess(int socialNetworkID) {
        Log.d(TAG, "onLoginSuccess: " + socialNetworkID);

        switchUIState(UIState.CONTENT);
        Toast.makeText(getActivity(), "login successfull", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginFailed(int socialNetworkID, String reason) {
        Log.d(TAG, "onLoginFailed: " + socialNetworkID + " : " + reason);

        switchUIState(UIState.CONTENT);
        Toast.makeText(getActivity(), "error: " + reason, Toast.LENGTH_SHORT).show();
    }
}
