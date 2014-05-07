package com.github.androidsocialnetworks.apidemos.fragment.base;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.github.androidsocialnetworks.apidemos.R;

public abstract class BaseLoginDemoFragment extends BaseDemoFragment
        implements SocialNetworkManager.OnInitializationCompleteListener, View.OnClickListener {

    public static final String SOCIAL_NETWORK_TAG = "BaseLoginDemoFragment.SOCIAL_NETWORK_TAG";
    protected SocialNetworkManager mSocialNetworkManager;

    private Button mTwitterButton;
    private Button mLinkedInButton;
    private Button mFacebookButton;
    private Button mGooglePlusButton;

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

        mTwitterButton = (Button) view.findViewById(R.id.twitter_button);
        mLinkedInButton = (Button) view.findViewById(R.id.linkedin_button);
        mFacebookButton = (Button) view.findViewById(R.id.facebook_button);
        mGooglePlusButton = (Button) view.findViewById(R.id.google_plus_button);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        getFragmentManager().beginTransaction().remove(mSocialNetworkManager).commit();
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        if (mSocialNetworkManager.getTwitterSocialNetwork().isConnected()) {
            mTwitterButton.setText("Twitter connected");
            mTwitterButton.setBackgroundColor(Color.LTGRAY);
        } else {
            mTwitterButton.setOnClickListener(this);
        }

        if (mSocialNetworkManager.getLinkedInSocialNetwork().isConnected()) {
            mLinkedInButton.setText("LinkedIn connected");
            mLinkedInButton.setBackgroundColor(Color.LTGRAY);
        } else {
            mLinkedInButton.setOnClickListener(this);
        }

        if (mSocialNetworkManager.getFacebookSocialNetwork().isConnected()) {
            mFacebookButton.setText("Facebook connected");
            mFacebookButton.setBackgroundColor(Color.LTGRAY);
        } else {
            mFacebookButton.setOnClickListener(this);
        }

        if (mSocialNetworkManager.getGooglePlusSocialNetwork().isConnected()) {
            mGooglePlusButton.setText("Google Plus connected");
            mGooglePlusButton.setBackgroundColor(Color.LTGRAY);
        } else {
            mGooglePlusButton.setOnClickListener(this);
        }
    }

}
