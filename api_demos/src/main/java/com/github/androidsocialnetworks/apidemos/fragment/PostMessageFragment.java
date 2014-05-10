package com.github.androidsocialnetworks.apidemos.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidsocialnetworks.lib.impl.FacebookSocialNetwork;
import com.androidsocialnetworks.lib.impl.LinkedInSocialNetwork;
import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;
import com.androidsocialnetworks.lib.listener.OnPostingCompleteListener;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseDemoFragment;

import java.util.UUID;

public class PostMessageFragment extends BaseDemoFragment {

    public static PostMessageFragment newInstance() {
        return new PostMessageFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterButton.setText("Post tweet");
        mLinkedInButton.setText("Update LinkedIn status");
        mFacebookButton.setText("Post to Facebook");
        mGooglePlusButton.setVisibility(View.GONE);
    }

    @Override
    protected void onTwitterAction() {
        if (!checkIsLoginned(TwitterSocialNetwork.ID)) return;

        final String message = "ASN Test: " + UUID.randomUUID();

        showProgress("Posting message");
        mSocialNetworkManager.getTwitterSocialNetwork().requestPostMessage(message,
                new OnPostingCompleteListener() {
                    @Override
                    public void onPostSuccessfully(int socialNetworkID) {
                        hideProgress();

                        handleSuccess("Success", "Message: '" + message + "' successfully posted.");
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
        if (!checkIsLoginned(LinkedInSocialNetwork.ID)) return;

        Toast.makeText(getActivity(), "LinkedIn post message", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onFacebookAction() {
        if (!checkIsLoginned(FacebookSocialNetwork.ID)) return;

        Toast.makeText(getActivity(), "Facebook post message", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onGooglePlusAction() {
        throw new IllegalStateException("Unsupported");
    }
}
