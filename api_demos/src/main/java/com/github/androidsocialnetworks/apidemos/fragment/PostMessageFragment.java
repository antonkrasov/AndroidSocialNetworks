package com.github.androidsocialnetworks.apidemos.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidsocialnetworks.lib.impl.FacebookSocialNetwork;
import com.androidsocialnetworks.lib.impl.LinkedInSocialNetwork;
import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseDemoFragment;

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

//        showProgress("Loading profile");
//        mSocialNetworkManager.getTwitterSocialNetwork().requestSocialPerson(USER_ID_TWITTER, new OnRequestSocialPersonCompleteListener() {
//            @Override
//            public void onRequestSocialPersonSuccess(int socialNetworkID, SocialPerson socialPerson) {
//                hideProgress();
//
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.root_container, ShowProfileFragment.newInstance(socialPerson))
//                        .addToBackStack(null)
//                        .commit();
//            }
//
//            @Override
//            public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
//                hideProgress();
//                handleError(errorMessage);
//            }
//        });
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
