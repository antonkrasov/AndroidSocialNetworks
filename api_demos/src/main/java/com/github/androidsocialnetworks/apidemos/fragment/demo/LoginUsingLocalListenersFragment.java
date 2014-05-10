package com.github.androidsocialnetworks.apidemos.fragment.demo;

import android.widget.Toast;

import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseLoginDemoFragment;

public class LoginUsingLocalListenersFragment extends BaseLoginDemoFragment {
    public static LoginUsingLocalListenersFragment newInstance() {
        return new LoginUsingLocalListenersFragment();
    }

    @Override
    protected void onTwitterAction() {
        showProgress("Authentificating... twitter");
        mSocialNetworkManager.getTwitterSocialNetwork().requestLogin(new OnLoginCompleteListener() {
            @Override
            public void onLoginSuccess(int socialNetworkID) {
                // let's reset buttons, we need to disable buttons
                onSocialNetworkManagerInitialized();

                hideProgress();
                handleSuccess("onLoginSuccess", "Now you can try other API Demos.");
            }

            @Override
            public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                hideProgress();
                handleError(errorMessage);
            }
        });
    }

    @Override
    protected void onLinkedInAction() {
        Toast.makeText(getActivity(), "Local. LinkedIn Login", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onFacebookAction() {
        Toast.makeText(getActivity(), "Local. Facebook Login", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onGooglePlusAction() {
        Toast.makeText(getActivity(), "Local. Google Plus Login", Toast.LENGTH_SHORT).show();
    }

}
