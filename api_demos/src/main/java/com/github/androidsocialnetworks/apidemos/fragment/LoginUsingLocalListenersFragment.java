package com.github.androidsocialnetworks.apidemos.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.github.androidsocialnetworks.apidemos.R;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseDemoFragment;

public class LoginUsingLocalListenersFragment extends BaseDemoFragment implements View.OnClickListener {
    public static final String SOCIAL_NETWORK_TAG = "LoginUsingGlobalListenersFragment.SOCIAL_NETWORK_TAG";
    private static final String TAG = LoginUsingLocalListenersFragment.class.getSimpleName();
    private SocialNetworkManager mSocialNetworkManager;


    public static LoginUsingLocalListenersFragment newInstance() {
        return new LoginUsingLocalListenersFragment();
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
                Toast.makeText(getActivity(), "L twitter_button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.linkedin_button:
                Toast.makeText(getActivity(), "L linkedin_button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.facebook_button:
                Toast.makeText(getActivity(), "L facebook_button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.google_plus_button:
                Toast.makeText(getActivity(), "L google_plus_button", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
