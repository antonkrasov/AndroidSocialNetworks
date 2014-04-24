package com.androidsocialnetworks.app.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidsocialnetworks.app.R;
import com.androidsocialnetworks.app.activity.MainActivity;
import com.androidsocialnetworks.app.fragment.base.BaseFragment;
import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialPerson;
import com.squareup.picasso.Picasso;

public class SocialNetworkFragment extends BaseFragment implements
        SocialNetwork.OnLoginCompleteListener, SocialNetwork.OnRequestSocialPersonListener {

    private static final String TAG = SocialNetworkFragment.class.getSimpleName();

    private static final String PARAM_NAME = "PARAM_NAME";

    private SocialNetwork mSocialNetwork;
    private Button mConnectDisconnectButton;

    public static SocialNetworkFragment newInstance(String name) {
        Bundle args = new Bundle();
        args.putString(PARAM_NAME, name);

        SocialNetworkFragment socialNetworkFragment = new SocialNetworkFragment();
        socialNetworkFragment.setArguments(args);
        return socialNetworkFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String name = getArguments().getString(PARAM_NAME);

        if (name.equals(SocialNetworksListFragment.TWITTER)) {
            mSocialNetwork = getMainActivity().getSocialNetworkManager().getTwitterSocialNetwork();
        } else if (name.equals(SocialNetworksListFragment.LINKED_IN)) {
            mSocialNetwork = getMainActivity().getSocialNetworkManager().getLinkedInSocialNetwork();
        } else {
            throw new IllegalStateException("Can't find social network for: " + name);
        }

        mSocialNetwork.setOnLoginCompleteListener(this);
        mSocialNetwork.setOnRequestSocialPersonListener(this);

        mConnectDisconnectButton = (Button) view.findViewById(R.id.connect_disconnect_button);
        if (mSocialNetwork.isConnected()) {
            mConnectDisconnectButton.setText("Logout");
        } else {
            mConnectDisconnectButton.setText("Login");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getArguments().getString(PARAM_NAME));
    }

    @Override
    protected void onConnectDisconnectButtonClick() {
        if (!mSocialNetwork.isConnected()) {
            mSocialNetwork.login();
            switchUIState(UIState.PROGRESS);
        } else {
            mSocialNetwork.logout();
            mConnectDisconnectButton.setText("Login");

            mNameTextView.setText("");
            mAvatarImageView.setImageBitmap(null);
        }
    }

    @Override
    protected void onLoadProfileClick() {
        if (!mSocialNetwork.isConnected()) {
            Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        mSocialNetwork.requestPerson();

        switchUIState(UIState.PROGRESS);
    }

    @Override
    public void onLoginSuccess(int socialNetworkID) {
        Log.d(TAG, "onLoginSuccess: " + socialNetworkID);

        switchUIState(UIState.CONTENT);
        Toast.makeText(getActivity(), "login successfull", Toast.LENGTH_SHORT).show();
        mConnectDisconnectButton.setText("Logout");
    }

    @Override
    public void onLoginFailed(int socialNetworkID, String reason) {
        Log.d(TAG, "onLoginFailed: " + socialNetworkID + " : " + reason);

        switchUIState(UIState.CONTENT);
        Toast.makeText(getActivity(), "error: " + reason, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSocialPersonSuccess(int socialNetworkID, SocialPerson socialPerson) {
        Log.d(TAG, "onRequestSocialPersonSuccess: " + socialPerson);

        mNameTextView.setText(socialPerson.name);
        Picasso.with(getActivity()).load(socialPerson.avatarURL).into(mAvatarImageView);

        switchUIState(UIState.CONTENT);
    }

    @Override
    public void onRequestSocialPersonFailed(int socialNetworkID, String reason) {
        Log.d(TAG, "onRequestSocialPersonFailed: " + socialNetworkID + " : " + reason);

        switchUIState(UIState.CONTENT);
        Toast.makeText(getActivity(), "error: " + reason, Toast.LENGTH_SHORT).show();
    }
}
