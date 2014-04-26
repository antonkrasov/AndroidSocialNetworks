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
import com.androidsocialnetworks.lib.SocialNetworkException;
import com.androidsocialnetworks.lib.SocialPerson;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class SocialNetworkFragment extends BaseFragment implements
        SocialNetwork.OnLoginCompleteListener, SocialNetwork.OnRequestSocialPersonListener, SocialNetwork.OnPostingListener, SocialNetwork.OnCheckingIsFriendListener, SocialNetwork.OnAddFriendListener, SocialNetwork.OnRemoveFriendListener {

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
        } else if (name.equals(SocialNetworksListFragment.FACEBOOK)) {
            mSocialNetwork = getMainActivity().getSocialNetworkManager().getFacebookSocialNetwork();
        } else {
            throw new IllegalStateException("Can't find social network for: " + name);
        }

        mSocialNetwork.setOnLoginCompleteListener(this);
        mSocialNetwork.setOnRequestSocialPersonListener(this);
        mSocialNetwork.setOnPostingListener(this);
        mSocialNetwork.setOnCheckingIsFriendListener(this);
        mSocialNetwork.setOnAddFriendListener(this);
        mSocialNetwork.setOnRemoveFriendListener(this);

        mConnectDisconnectButton = (Button) view.findViewById(R.id.connect_disconnect_button);
        if (mSocialNetwork.isConnected()) {
            mConnectDisconnectButton.setText("Logout");
        } else {
            mConnectDisconnectButton.setText("Login");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mSocialNetwork.setOnLoginCompleteListener(null);
        mSocialNetwork.setOnRequestSocialPersonListener(null);
        mSocialNetwork.setOnPostingListener(null);
        mSocialNetwork.setOnCheckingIsFriendListener(null);
        mSocialNetwork.setOnAddFriendListener(null);
        mSocialNetwork.setOnRemoveFriendListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getArguments().getString(PARAM_NAME));
    }

    private String getUserID() {
        String name = getArguments().getString(PARAM_NAME);
        if (name.equals(SocialNetworksListFragment.TWITTER)) {
            return "39222068";
        } else if (name.equals(SocialNetworksListFragment.FACEBOOK)) {
            return "100008263800271";
        } else if (name.equals(SocialNetworksListFragment.LINKED_IN)) {
            return "WQlagxgbbw";
        } else {
            throw new IllegalStateException("Can't find social network for: " + name);
        }
    }

    @Override
    protected void onConnectDisconnectButtonClick() {
        if (!mSocialNetwork.isConnected()) {
            mSocialNetwork.requestLogin();
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
            Toast.makeText(getActivity(), "Please requestLogin first", Toast.LENGTH_SHORT).show();
            return;
        }

        mSocialNetwork.requestPerson();

        switchUIState(UIState.PROGRESS);
    }

    @Override
    protected void onPostMessage() {
        if (!mSocialNetwork.isConnected()) {
            Toast.makeText(getActivity(), "Please requestLogin first", Toast.LENGTH_SHORT).show();
            return;
        }

        mSocialNetwork.requestPostMessage(UUID.randomUUID().toString());

        switchUIState(UIState.PROGRESS);
    }

    @Override
    protected void onPostPhoto() {
        if (!mSocialNetwork.isConnected()) {
            Toast.makeText(getActivity(), "Please requestLogin first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mSocialNetwork.requestPostPhoto(MainActivity.ANDROID_IMAGE, "Test");
            switchUIState(UIState.PROGRESS);
        } catch (SocialNetworkException e) {
            Log.e(TAG, "ERROR", e);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void checkIsFriendClick() {
        try {
            mSocialNetwork.requestCheckIsFriend(getUserID());
            switchUIState(UIState.PROGRESS);
        } catch (SocialNetworkException e) {
            Log.e(TAG, "ERROR", e);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void addFriendClick() {
        try {
            mSocialNetwork.requestAddFriend(getUserID());
            switchUIState(UIState.PROGRESS);
        } catch (SocialNetworkException e) {
            Log.e(TAG, "ERROR", e);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void removeFriendClick() {
        try {
            mSocialNetwork.requestRemoveFriend(getUserID());
            switchUIState(UIState.PROGRESS);
        } catch (SocialNetworkException e) {
            Log.e(TAG, "ERROR", e);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoginSuccess(int socialNetworkID) {
        Log.d(TAG, "onLoginSuccess: " + socialNetworkID);

        switchUIState(UIState.CONTENT);
        Toast.makeText(getActivity(), "requestLogin successfull", Toast.LENGTH_SHORT).show();
        mConnectDisconnectButton.setText("Logout");
    }

    @Override
    public void onLoginFailed(int socialNetworkID, String reason) {
        handleError(socialNetworkID, reason);
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
        handleError(socialNetworkID, reason);
    }

    @Override
    public void onPostSuccessfully(int socialNetworkID) {
        Log.d(TAG, "onPostSuccessfully: " + socialNetworkID);

        Toast.makeText(getActivity(), "Posted...", Toast.LENGTH_SHORT).show();

        switchUIState(UIState.CONTENT);
    }

    @Override
    public void onPostFailed(int socialNetworkID, String reason) {
        handleError(socialNetworkID, reason);
    }


    @Override
    public void onCheckIsFriendSuccess(int socialNetworkID, String userID, boolean isFriend) {
        Toast.makeText(getActivity(), "onCheckIsFriendSuccess: " + isFriend, Toast.LENGTH_SHORT).show();
        switchUIState(UIState.CONTENT);
    }

    @Override
    public void onCheckIsFriendFailed(int socialNetworkID, String userID, String error) {
        handleError(socialNetworkID, error);
    }

    @Override
    public void onAddFriendSuccess(int socialNetworkID, String userID) {
        Toast.makeText(getActivity(), "onAddFriendSuccess", Toast.LENGTH_SHORT).show();
        switchUIState(UIState.CONTENT);
    }

    @Override
    public void onAddFriendFailed(int socialNetworkID, String userID, String error) {
        handleError(socialNetworkID, error);
    }

    @Override
    public void onRemoveFriendSuccess(int socialNetworkID, String userID) {
        Toast.makeText(getActivity(), "onRemoveFriendSuccess", Toast.LENGTH_SHORT).show();
        switchUIState(UIState.CONTENT);
    }

    @Override
    public void onRemoveFriendFailed(int socialNetworkID, String userID, String error) {
        handleError(socialNetworkID, error);
    }

    private void handleError(int socialNetworkID, String reason) {
        Log.d(TAG, "handleError: " + socialNetworkID + " : " + reason);

        switchUIState(UIState.CONTENT);
        Toast.makeText(getActivity(), "error: " + reason, Toast.LENGTH_SHORT).show();
    }
}
