package com.github.androidsocialnetworks.apidemos.fragment.demo;

import android.os.Bundle;
import android.view.View;

import com.androidsocialnetworks.lib.impl.LinkedInSocialNetwork;
import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;
import com.androidsocialnetworks.lib.listener.OnCheckIsFriendCompleteListener;
import com.github.androidsocialnetworks.apidemos.APIDemosApplication;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseDemoFragment;

public class CheckIsFriendFragment extends BaseDemoFragment {
    public static CheckIsFriendFragment newInstance() {
        return new CheckIsFriendFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterButton.setText("Do you follow Anton Krasov?");
        mLinkedInButton.setText("Is Anton Krasov in your connection?");
        mFacebookButton.setVisibility(View.GONE);
        mGooglePlusButton.setVisibility(View.GONE);
    }

    @Override
    protected void onTwitterAction() {
        if (!checkIsLoginned(TwitterSocialNetwork.ID)) return;

        showProgress("Checking do you follow Anton Krasov");
        mSocialNetworkManager.getTwitterSocialNetwork().requestCheckIsFriend(
                APIDemosApplication.USER_ID_TWITTER,
                new DemoTwitterOnCheckIsFriendCompleteListener()
        );

    }

    @Override
    protected void onLinkedInAction() {
        if (!checkIsLoginned(LinkedInSocialNetwork.ID)) return;

        showProgress("Checking is Anton Krasov in your connections");
        mSocialNetworkManager.getLinkedInSocialNetwork().requestCheckIsFriend(
                APIDemosApplication.USER_ID_LINKED_IN,
                new DemoLinkedInOnCheckIsFriendCompleteListener()
        );
    }

    @Override
    protected void onFacebookAction() {
        throw new IllegalStateException("Unsupported");
    }

    @Override
    protected void onGooglePlusAction() {
        throw new IllegalStateException("Unsupported");
    }

    private class DemoTwitterOnCheckIsFriendCompleteListener implements OnCheckIsFriendCompleteListener {
        @Override
        public void onCheckIsFriendComplete(int socialNetworkID, String userID, boolean isFriend) {
            hideProgress();

            if (isFriend) {
                handleSuccess("Is Friend?", "You follow Anton Krasov!");
            } else {
                handleSuccess("Is Friend?", "You don't follow Anton Krasov!");
            }
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            hideProgress();
            handleError(errorMessage);
        }
    }

    private class DemoLinkedInOnCheckIsFriendCompleteListener implements OnCheckIsFriendCompleteListener {
        @Override
        public void onCheckIsFriendComplete(int socialNetworkID, String userID, boolean isFriend) {
            hideProgress();

            if (isFriend) {
                handleSuccess("Is Friend?", "Anton Krasov is in your connection list");
            } else {
                handleSuccess("Is Friend?", "You don't have Anton Krasov in your connection list");
            }
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            hideProgress();
            handleError(errorMessage);
        }
    }

}
