package com.github.androidsocialnetworks.apidemos.fragment.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidsocialnetworks.lib.impl.LinkedInSocialNetwork;
import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;
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

//        final String message = "ASN Test: " + UUID.randomUUID();
//
//        showProgress("Posting message");
//        mSocialNetworkManager.getTwitterSocialNetwork().requestPostMessage(message,
//                new OnPostingCompleteListener() {
//                    @Override
//                    public void onPostSuccessfully(int socialNetworkID) {
//                        hideProgress();
//
//                        handleSuccess("Success", "Message: '" + message + "' successfully posted.");
//                    }
//
//                    @Override
//                    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
//                        hideProgress();
//                        handleError(errorMessage);
//                    }
//                }
//        );
    }

    @Override
    protected void onLinkedInAction() {
        if (!checkIsLoginned(LinkedInSocialNetwork.ID)) return;

        Toast.makeText(getActivity(), "LinkedIn check is friend", Toast.LENGTH_SHORT).show();
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
