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
import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;
import com.squareup.picasso.Picasso;

public class TwitterFragment extends BaseFragment implements SocialNetwork.OnLoginCompleteListener, SocialNetwork.OnRequestSocialPersonListener {
    private static final String TAG = TwitterFragment.class.getSimpleName();

    private TwitterSocialNetwork mTwitterSocialNetwork;
    private Button mConnectDisconnectButton;

    public static TwitterFragment newInstance() {
        return new TwitterFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterSocialNetwork = getMainActivity().getSocialNetworkManager().getTwitterSocialNetwork();
        mTwitterSocialNetwork.setOnLoginCompleteListener(this);
        mTwitterSocialNetwork.setOnRequestSocialPersonListener(this);

        mConnectDisconnectButton = (Button) view.findViewById(R.id.connect_disconnect_button);
        if (mTwitterSocialNetwork.isConnected()) {
            mConnectDisconnectButton.setText("Logout");
        } else {
            mConnectDisconnectButton.setText("Login");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Twitter");
    }

    @Override
    protected void onConnectDisconnectButtonClick() {
        if (!mTwitterSocialNetwork.isConnected()) {
            mTwitterSocialNetwork.login();
            switchUIState(UIState.PROGRESS);
        } else {
            mTwitterSocialNetwork.logout();
            mConnectDisconnectButton.setText("Login");

            mNameTextView.setText("");
            mAvatarImageView.setImageBitmap(null);
        }
    }

    @Override
    protected void onLoadProfileClick() {
        if (!mTwitterSocialNetwork.isConnected()) {
            Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        mTwitterSocialNetwork.requestPerson();

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
