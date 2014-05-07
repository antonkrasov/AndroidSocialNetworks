package com.github.androidsocialnetworks.apidemos.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.github.androidsocialnetworks.apidemos.R;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseDemoFragment;

public class CurrentUserProfileFragment extends BaseDemoFragment implements View.OnClickListener {

    public static CurrentUserProfileFragment newInstance() {
        return new CurrentUserProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button twitterButton = (Button) view.findViewById(R.id.twitter_button);
        Button linkedInButton = (Button) view.findViewById(R.id.linkedin_button);
        Button facebookButton = (Button) view.findViewById(R.id.facebook_button);
        Button googlePlusButton = (Button) view.findViewById(R.id.google_plus_button);

        twitterButton.setText("Load Twitter Profile");
        linkedInButton.setText("Load LinkedIn Profile");
        facebookButton.setText("Load Facebook Profile");
        googlePlusButton.setText("Load Google Plus Profile");

        twitterButton.setOnClickListener(this);
        linkedInButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        googlePlusButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.twitter_button:
                if (mSocialNetworkManager.getTwitterSocialNetwork().isConnected()) {
                    Toast.makeText(getActivity(), "Load Twitter Profile", Toast.LENGTH_SHORT).show();
                } else {
                    handleError("This action request login, please go to Login demo, and login via Twitter first.");
                }
                break;
            case R.id.linkedin_button:
                if (mSocialNetworkManager.getLinkedInSocialNetwork().isConnected()) {
                    Toast.makeText(getActivity(), "Load LinkedIn Profile", Toast.LENGTH_SHORT).show();
                } else {
                    handleError("This action request login, please go to Login demo, and login via LinkedIn first.");
                }
                break;
            case R.id.facebook_button:
                if (mSocialNetworkManager.getFacebookSocialNetwork().isConnected()) {
                    Toast.makeText(getActivity(), "Load Facebook Profile", Toast.LENGTH_SHORT).show();
                } else {
                    handleError("This action request login, please go to Login demo, and login via Facebook first.");
                }
                break;
            case R.id.google_plus_button:
                if (mSocialNetworkManager.getGooglePlusSocialNetwork().isConnected()) {
                    Toast.makeText(getActivity(), "Load Google Plus Profile", Toast.LENGTH_SHORT).show();
                } else {
                    handleError("This action request login, please go to Login demo, and login via Google Plus first.");
                }
                break;
            default:
                throw new IllegalArgumentException("Can't find click handler for: " + v);
        }
    }
}
