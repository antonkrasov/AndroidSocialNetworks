package com.androidsocialnetworks.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.SocialPerson;
import com.squareup.picasso.Picasso;

public class MainFragment extends Fragment implements View.OnClickListener, SocialNetworkManager.OnInitializationCompleteListener, SocialNetwork.OnLoginCompleteListener, SocialNetwork.OnRequestSocialPersonListener {
    public static final String SOCIAL_NETWORK_TAG = "MainFragment";
    private static final String TAG = MainFragment.class.getSimpleName();
    private static final String SAVE_STATE_KEY_UI_STATE = "MainFragment.SAVE_STATE_KEY_UI_STATE";

    private SocialNetworkManager mSocialNetworkManager;
    private UIState mUIState = UIState.LOGIN;

    private View mLoginContainer;
    private View mProgressContainer;
    private View mProfileContainer;

    private ImageView mAvatarImageView;
    private TextView mNameTextView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "#onCreate");

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
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.twitter_button).setOnClickListener(this);
        view.findViewById(R.id.facebook_button).setOnClickListener(this);
        view.findViewById(R.id.linkedin_button).setOnClickListener(this);
        view.findViewById(R.id.google_plus_button).setOnClickListener(this);

        view.findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
                    socialNetwork.logout();

                    switchUIState(UIState.LOGIN);

                    mAvatarImageView.setImageBitmap(null);
                    mNameTextView.setText("");
                }
            }
        });

        mLoginContainer = view.findViewById(R.id.login_container);
        mProgressContainer = view.findViewById(R.id.progress_container);
        mProfileContainer = view.findViewById(R.id.profile_container);

        mAvatarImageView = (ImageView) view.findViewById(R.id.avatar_image_view);
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);

        if (savedInstanceState != null) {
            mUIState = UIState.values()[savedInstanceState.getInt(SAVE_STATE_KEY_UI_STATE, 0)];
        }

        switchUIState(mUIState);
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        Log.d(TAG, "#onSocialNetworkManagerInitialized");
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            socialNetwork.setOnRequestSocialPersonListener(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_STATE_KEY_UI_STATE, mUIState.ordinal());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "#onDetach");

        mSocialNetworkManager.setOnInitializationCompleteListener(null);
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(null);
            socialNetwork.setOnRequestSocialPersonListener(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.twitter_button:
                mSocialNetworkManager.getTwitterSocialNetwork().requestLogin();
                break;
            case R.id.facebook_button:
                mSocialNetworkManager.getFacebookSocialNetwork().requestLogin();
                break;
            case R.id.linkedin_button:
                mSocialNetworkManager.getLinkedInSocialNetwork().requestLogin();
                break;
            case R.id.google_plus_button:
                mSocialNetworkManager.getGooglePlusSocialNetwork().requestLogin();
                break;
        }

        switchUIState(UIState.PROGRESS);
    }

    private void switchUIState(UIState state) {
        mUIState = state;

        mLoginContainer.setVisibility(View.GONE);
        mProfileContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.GONE);

        switch (state) {
            case LOGIN:
                mLoginContainer.setVisibility(View.VISIBLE);
                break;
            case PROFILE:
                mProfileContainer.setVisibility(View.VISIBLE);
                break;
            case PROGRESS:
                mProgressContainer.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onLoginSuccess(int id) {
        Log.d(TAG, "onLoginSuccess: " + id);
        mSocialNetworkManager.getSocialNetwork(id).requestPerson();
    }

    @Override
    public void onLoginFailed(int id, String errorReason) {
        switchUIState(UIState.LOGIN);
        Log.d(TAG, "onLoginFailed: " + id + " : " + errorReason);
        handleError(id, errorReason);
    }

    @Override
    public void onRequestSocialPersonSuccess(int i, SocialPerson socialPerson) {
        switchUIState(UIState.PROFILE);

        Picasso.with(getActivity()).load(socialPerson.avatarURL).into(mAvatarImageView);
        mNameTextView.setText(socialPerson.name);
    }

    @Override
    public void onRequestSocialPersonFailed(int id, String errorReason) {
        switchUIState(UIState.LOGIN);
        Log.d(TAG, "onRequestSocialPersonFailed: " + id + " : " + errorReason);
        handleError(id, errorReason);
    }

    private void handleError(int id, String errorReason) {
        Toast.makeText(getActivity(), "ERROR: " + errorReason, Toast.LENGTH_SHORT).show();
    }

    private static enum UIState {
        LOGIN,
        PROGRESS,
        PROFILE
    }
}
