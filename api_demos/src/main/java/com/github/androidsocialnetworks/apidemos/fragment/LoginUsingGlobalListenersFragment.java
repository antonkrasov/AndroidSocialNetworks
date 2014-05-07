package com.github.androidsocialnetworks.apidemos.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.github.androidsocialnetworks.apidemos.R;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseDemoFragment;

public class LoginUsingGlobalListenersFragment extends BaseDemoFragment
        implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener,
        View.OnClickListener {

    public static final String SOCIAL_NETWORK_TAG = "LoginUsingGlobalListenersFragment.SOCIAL_NETWORK_TAG";
    private static final String TAG = LoginUsingGlobalListenersFragment.class.getSimpleName();
    private SocialNetworkManager mSocialNetworkManager;

    public static LoginUsingGlobalListenersFragment newInstance() {
        return new LoginUsingGlobalListenersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);

        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = SocialNetworkManager.Builder.from(getActivity())
                    .twitter("3IYEDC9Pq5SIjzENhgorlpera", "fawjHMhyzhrfcFKZVB6d5YfiWbWGmgX7vPfazi61xZY9pdD1aE")
                    .linkedIn("77ieoe71pon7wq", "pp5E8hkdY9voGC9y", "r_basicprofile+rw_nus+r_network+w_messages")
                    .facebook()
                    .googlePlus()
                    .build();
            getFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();
        }

        mSocialNetworkManager.setOnInitializationCompleteListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.twitter_button).setOnClickListener(this);
        view.findViewById(R.id.linkedin_button).setOnClickListener(this);
        view.findViewById(R.id.facebook_button).setOnClickListener(this);
        view.findViewById(R.id.google_plus_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.twitter_button:
                showProgress("Authentificating... twitter");
                mSocialNetworkManager.getTwitterSocialNetwork().requestLogin();
                break;
            case R.id.linkedin_button:
                showProgress("Authentificating... linkedIn");
                mSocialNetworkManager.getLinkedInSocialNetwork().requestLogin();
                break;
            case R.id.facebook_button:
                showProgress("Authentificating... facebook");
                mSocialNetworkManager.getFacebookSocialNetwork().requestLogin();
                break;
            case R.id.google_plus_button:
                showProgress("Authentificating... googlePlus");
                mSocialNetworkManager.getGooglePlusSocialNetwork().requestLogin();
                break;
            default:
                throw new IllegalArgumentException("Can't find click handler for: " + v);
        }
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
        }
    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        hideProgress();
        handleError(errorMessage);
    }

    @Override
    public void onLoginSuccess(int socialNetworkID) {
        hideProgress();
        handleSuccess("onLoginSuccess", "Now you can try other API Demos.");
    }

}
