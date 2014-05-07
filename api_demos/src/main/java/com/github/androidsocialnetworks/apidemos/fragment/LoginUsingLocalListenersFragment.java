package com.github.androidsocialnetworks.apidemos.fragment;

import android.view.View;
import android.widget.Toast;

import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.github.androidsocialnetworks.apidemos.R;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseLoginDemoFragment;

public class LoginUsingLocalListenersFragment extends BaseLoginDemoFragment {
    public static LoginUsingLocalListenersFragment newInstance() {
        return new LoginUsingLocalListenersFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.twitter_button:
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
                break;
            case R.id.linkedin_button:
                Toast.makeText(getActivity(), "Local. LinkedIn Login", Toast.LENGTH_SHORT).show();
                break;
            case R.id.facebook_button:
                Toast.makeText(getActivity(), "Local. Facebook Login", Toast.LENGTH_SHORT).show();
                break;
            case R.id.google_plus_button:
                Toast.makeText(getActivity(), "Local. Google Plus Login", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
