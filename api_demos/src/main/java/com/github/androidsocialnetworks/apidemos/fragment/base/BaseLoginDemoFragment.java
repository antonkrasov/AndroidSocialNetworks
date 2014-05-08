package com.github.androidsocialnetworks.apidemos.fragment.base;

import android.graphics.Color;
import android.view.View;

public abstract class BaseLoginDemoFragment extends BaseDemoFragment implements View.OnClickListener {

    @Override
    public void onSocialNetworkManagerInitialized() {
        if (mSocialNetworkManager.getTwitterSocialNetwork().isConnected()) {
            mTwitterButton.setText("Twitter connected");
            mTwitterButton.setBackgroundColor(Color.LTGRAY);
            mTwitterButton.setOnClickListener(null);
        }

        if (mSocialNetworkManager.getLinkedInSocialNetwork().isConnected()) {
            mLinkedInButton.setText("LinkedIn connected");
            mLinkedInButton.setBackgroundColor(Color.LTGRAY);
            mLinkedInButton.setOnClickListener(null);
        }

        if (mSocialNetworkManager.getFacebookSocialNetwork().isConnected()) {
            mFacebookButton.setText("Facebook connected");
            mFacebookButton.setBackgroundColor(Color.LTGRAY);
            mFacebookButton.setOnClickListener(null);
        }

        if (mSocialNetworkManager.getGooglePlusSocialNetwork().isConnected()) {
            mGooglePlusButton.setText("Google Plus connected");
            mGooglePlusButton.setBackgroundColor(Color.LTGRAY);
            mGooglePlusButton.setOnClickListener(null);
        }
    }

}
