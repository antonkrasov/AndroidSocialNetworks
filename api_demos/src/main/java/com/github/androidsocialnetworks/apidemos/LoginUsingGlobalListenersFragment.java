package com.github.androidsocialnetworks.apidemos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.SocialPerson;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;

public class LoginUsingGlobalListenersFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener, OnRequestSocialPersonCompleteListener {

    private SocialNetworkManager mSocialNetworkManager;

    public static LoginUsingGlobalListenersFragment newInstance() {
        return new LoginUsingGlobalListenersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mSocialNetworkManager.setOnInitializationCompleteListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_using_global_listeners, container, false);
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
        }
    }

    @Override
    public void onLoginSuccess(int socialNetworkID) {

    }

    @Override
    public void onError(int socialNetworkID, String errorMessage, Object data) {

    }

    @Override
    public void onRequestSocialPersonSuccess(int socialNetworkID, SocialPerson socialPerson) {

    }
}
