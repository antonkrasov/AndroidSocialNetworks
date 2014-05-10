package com.github.androidsocialnetworks.apidemos.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidsocialnetworks.lib.impl.FacebookSocialNetwork;
import com.androidsocialnetworks.lib.impl.GooglePlusSocialNetwork;
import com.androidsocialnetworks.lib.impl.LinkedInSocialNetwork;
import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseDemoFragment;

public class OtherUsersProfile extends BaseDemoFragment {

    public static OtherUsersProfile newInstance() {
        return new OtherUsersProfile();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterButton.setText("Load Twitter Profile");
        mLinkedInButton.setText("Load LinkedIn Profile");
        mFacebookButton.setText("Load Facebook Profile");
        mGooglePlusButton.setText("Load Google Plus Profile");
    }

    @Override
    protected void onTwitterAction() {
        if (!checkIsLoginned(TwitterSocialNetwork.ID)) return;

        Toast.makeText(getActivity(), "Load Twitter Profile", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onLinkedInAction() {
        if (!checkIsLoginned(LinkedInSocialNetwork.ID)) return;

        Toast.makeText(getActivity(), "Load LinkedIn Profile", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onFacebookAction() {
        if (!checkIsLoginned(FacebookSocialNetwork.ID)) return;

        Toast.makeText(getActivity(), "Load Facebook Profile", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onGooglePlusAction() {
        if (!checkIsLoginned(GooglePlusSocialNetwork.ID)) return;

        Toast.makeText(getActivity(), "Load Google Plus Profile", Toast.LENGTH_SHORT).show();
    }

}
