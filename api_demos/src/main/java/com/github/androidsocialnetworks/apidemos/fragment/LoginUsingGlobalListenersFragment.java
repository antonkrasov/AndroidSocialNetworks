package com.github.androidsocialnetworks.apidemos.fragment;

import android.view.View;
import android.widget.Toast;

import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.github.androidsocialnetworks.apidemos.R;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.twitter_button:
                showProgress("Authentificating... twitter");
                mSocialNetworkManager.getTwitterSocialNetwork().requestLogin();
                break;
            case R.id.linkedin_button:
                Toast.makeText(getActivity(), "Global. LinkedIn Login", Toast.LENGTH_SHORT).show();
                break;
            case R.id.facebook_button:
                Toast.makeText(getActivity(), "Global. Facebook Login", Toast.LENGTH_SHORT).show();
                break;
            case R.id.google_plus_button:
                Toast.makeText(getActivity(), "Global. GooglePlus Login", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalArgumentException("Can't find click handler for: " + v);
        }
    }

    @Override
    public void onLoginSuccess(int socialNetworkID) {
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
