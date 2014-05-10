package com.github.androidsocialnetworks.apidemos.fragment.demo;

import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseLoginDemoFragment;

public class LoginUsingGlobalListenersFragment extends BaseLoginDemoFragment
        implements OnLoginCompleteListener {

    private static final String TAG = LoginUsingGlobalListenersFragment.class.getSimpleName();

    public static LoginUsingGlobalListenersFragment newInstance() {
        return new LoginUsingGlobalListenersFragment();
    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        hideProgress();
        handleError(errorMessage);
    }

    @Override
    protected void onTwitterAction() {
        showProgress("Authentificating... Twitter");
        mSocialNetworkManager.getTwitterSocialNetwork().requestLogin();
    }

    @Override
    protected void onLinkedInAction() {
        showProgress("Authentificating... LinkedIn");
        mSocialNetworkManager.getLinkedInSocialNetwork().requestLogin();
    }

    @Override
    protected void onFacebookAction() {
        mSocialNetworkManager.getFacebookSocialNetwork().requestLogin();
    }

    @Override
    protected void onGooglePlusAction() {
        mSocialNetworkManager.getGooglePlusSocialNetwork().requestLogin();
    }

    @Override
    public void onLoginSuccess(int socialNetworkID) {
        // let's reset buttons, we need to disable buttons
        onSocialNetworkManagerInitialized();

        hideProgress();
        handleSuccess("onLoginSuccess", "Now you can try other API Demos.");
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        super.onSocialNetworkManagerInitialized();

        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
        }
    }
}
