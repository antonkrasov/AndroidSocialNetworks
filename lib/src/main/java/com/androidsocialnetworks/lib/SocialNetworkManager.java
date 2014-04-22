package com.androidsocialnetworks.lib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.androidsocialnetworks.lib.impl.LinkedInSocialNetwork;
import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;

import java.util.HashMap;
import java.util.Map;

public class SocialNetworkManager extends Fragment {
    public static final String SAVE_KEY = SocialNetworkManager.class.getSimpleName();

    private static final String PARAM_TWITTER_KEY = "SocialNetworkManager.PARAM_TWITTER_KEY";
    private static final String PARAM_TWITTER_SECRET = "SocialNetworkManager.PARAM_TWITTER_SECRET";

    private static final String PARAM_LINKEDIN_KEY = "SocialNetworkManager.PARAM_LINKEDIN_KEY";
    private static final String PARAM_LINKEDIN_SECRET = "SocialNetworkManager.PARAM_LINKEDIN_SECRET";
    private static final String PARAM_LINKEDIN_PERMISSIONS = "SocialNetworkManager.PARAM_LINKEDIN_PERMISSIONS";

    private static final String KEY_SOCIAL_NETWORK_TWITTER = "KEY_SOCIAL_NETWORK_TWITTER";
    private static final String KEY_SOCIAL_NETWORK_LINKED_IN = "KEY_SOCIAL_NETWORK_LINKED_IN";

    private Map<String, SocialNetwork> mSocialNetworksMap = new HashMap<String, SocialNetwork>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = getArguments();

        final String paramTwitterKey = args.getString(PARAM_TWITTER_KEY);
        final String paramTwitterSecret = args.getString(PARAM_TWITTER_SECRET);

        final String paramLinkedInKey = args.getString(PARAM_LINKEDIN_KEY);
        final String paramLinkedInSecret = args.getString(PARAM_LINKEDIN_SECRET);
        final String paramLinkedInPermissions = args.getString(PARAM_LINKEDIN_PERMISSIONS);

        if (!TextUtils.isEmpty(paramTwitterKey) || !TextUtils.isEmpty(paramTwitterKey)) {
            mSocialNetworksMap.put(KEY_SOCIAL_NETWORK_TWITTER,
                    new TwitterSocialNetwork(getActivity(), paramTwitterKey, paramTwitterSecret));
        }

        if (!TextUtils.isEmpty(paramLinkedInKey) || !TextUtils.isEmpty(paramLinkedInSecret)) {
            mSocialNetworksMap.put(KEY_SOCIAL_NETWORK_LINKED_IN,
                    new LinkedInSocialNetwork(getActivity(), paramLinkedInKey, paramLinkedInSecret, paramLinkedInPermissions));
        }

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static class Builder {
        private String twitterConsumerKey, twitterConsumerSecret;
        private String linkedInConsumerKey, linkedInConsumerSecret, linkedInPermissions;

        private Builder() {

        }

        public static Builder create() {
            return new Builder();
        }

        public Builder twitter(String consumerKey, String consumerSecret) {
            twitterConsumerKey = consumerKey;
            twitterConsumerSecret = consumerSecret;
            return this;
        }

        public Builder linkedIn(String consumerKey, String consumerSecret, String permissions) {
            linkedInConsumerKey = consumerKey;
            linkedInConsumerSecret = consumerSecret;
            linkedInPermissions = permissions;
            return this;
        }

        public SocialNetworkManager build() {
            Bundle args = new Bundle();

            if (!TextUtils.isEmpty(twitterConsumerKey) && !TextUtils.isEmpty(twitterConsumerSecret)) {
                args.putString(PARAM_TWITTER_KEY, twitterConsumerKey);
                args.putString(PARAM_TWITTER_SECRET, twitterConsumerSecret);
            }

            if (!TextUtils.isEmpty(linkedInConsumerKey) && !TextUtils.isEmpty(linkedInConsumerSecret)
                    && !TextUtils.isEmpty(linkedInPermissions)) {
                args.putString(PARAM_LINKEDIN_KEY, linkedInConsumerKey);
                args.putString(PARAM_LINKEDIN_SECRET, linkedInConsumerSecret);
                args.putString(PARAM_LINKEDIN_PERMISSIONS, linkedInPermissions);
            }

            SocialNetworkManager socialNetworkManager = new SocialNetworkManager();
            socialNetworkManager.setArguments(args);
            return socialNetworkManager;
        }
    }
}
