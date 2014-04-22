package com.androidsocialnetworks.lib.impl;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.androidsocialnetworks.lib.SocialNetwork;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterSocialNetwork extends SocialNetwork {
    private static final String TAG = TwitterSocialNetwork.class.getSimpleName();

    private static final String SAVE_STATE_KEY_OAUTH_TOKEN = "TwitterSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
    private static final String SAVE_STATE_KEY_OAUTH_SECRET = "TwitterSocialNetwork.SAVE_STATE_KEY_OAUTH_SECRET";

    private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";

    private static final int REQUEST_AUTH = 15612;

    private final String TWITTER_CALLBACK_URL = "oauth://AndroidSocialNetworks";
    private final String fConsumerKey;
    private final String fConsumerSecret;
    private Twitter mTwitter;

    public TwitterSocialNetwork(Activity activity, String consumerKey, String consumerSecret) {
        super(activity);
        Log.d(TAG, "new TwitterSocialNetwork: " + consumerKey + " : " + consumerSecret);

        fConsumerKey = consumerKey;
        fConsumerSecret = consumerSecret;

        if (TextUtils.isEmpty(fConsumerKey) || TextUtils.isEmpty(fConsumerSecret)) {
            throw new IllegalArgumentException("consumerKey and consumerSecret are invalid");
        }

        initTwitterClient();
    }

    private void initTwitterClient() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(fConsumerKey);
        builder.setOAuthConsumerSecret(fConsumerSecret);

        String accessToken = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
        String accessTokenSecret = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null);

        TwitterFactory factory = new TwitterFactory(builder.build());

        if (TextUtils.isEmpty(accessToken) && TextUtils.isEmpty(accessTokenSecret)) {
            mTwitter = factory.getInstance();
        } else {
            mTwitter = factory.getInstance(new AccessToken(accessToken, accessTokenSecret));
        }
    }

    @Override
    public boolean isConnected() {
        String accessToken = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
        String accessTokenSecret = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null);
        return accessToken != null && accessTokenSecret != null;
    }
}
