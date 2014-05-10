package com.github.androidsocialnetworks.apidemos.fragment.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidsocialnetworks.lib.impl.LinkedInSocialNetwork;
import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseDemoFragment;

public class AddFriendFragment extends BaseDemoFragment {
    public static AddFriendFragment newInstance() {
        return new AddFriendFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterButton.setText("Follow Anton Krasov");
        mLinkedInButton.setText("Send invite to Anton Krasov");
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

        Toast.makeText(getActivity(), "LinkedIn add friend", Toast.LENGTH_SHORT).show();
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
