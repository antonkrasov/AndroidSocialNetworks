package com.github.androidsocialnetworks.apidemos.fragment.demo;

import android.os.Bundle;
import android.view.View;

import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;
import com.androidsocialnetworks.lib.listener.OnRequestRemoveFriendCompleteListener;
import com.github.androidsocialnetworks.apidemos.APIDemosApplication;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseDemoFragment;

public class RemoveFriendFragment extends BaseDemoFragment {
    public static RemoveFriendFragment newInstance() {
        return new RemoveFriendFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterButton.setText("Unfollow Anton Krasov");
        mLinkedInButton.setVisibility(View.GONE);
        mFacebookButton.setVisibility(View.GONE);
        mGooglePlusButton.setVisibility(View.GONE);
    }

    @Override
    protected void onTwitterAction() {
        if (!checkIsLoginned(TwitterSocialNetwork.ID)) return;

        showProgress("Unfollowing Anton Krasov");
        mSocialNetworkManager.getTwitterSocialNetwork().requestRemoveFriend(
                APIDemosApplication.USER_ID_TWITTER,
                new OnRequestRemoveFriendCompleteListener() {
                    @Override
                    public void onRequestRemoveFriendComplete(int socialNetworkID, String userID) {
                        hideProgress();

                        handleSuccess("Remove friend", "Now you don't follow Anton Krasov!");
                    }

                    @Override
                    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                        hideProgress();
                        handleError(errorMessage);
                    }
                }
        );
    }

    @Override
    protected void onLinkedInAction() {
        throw new IllegalStateException("Unsupported");
    }

    @Override
    protected void onFacebookAction() {
        throw new IllegalStateException("Unsupported");
    }

    @Override
    protected void onGooglePlusAction() {
        throw new IllegalStateException("Unsupported");
    }
}
