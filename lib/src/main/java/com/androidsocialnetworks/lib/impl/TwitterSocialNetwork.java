package com.androidsocialnetworks.lib.impl;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.androidsocialnetworks.lib.OAuthActivity;
import com.androidsocialnetworks.lib.SocialNetwork;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterSocialNetwork extends SocialNetwork {
    public static final int ID = UUID.randomUUID().hashCode();
    private static final String TAG = TwitterSocialNetwork.class.getSimpleName();
    private static final String SAVE_STATE_KEY_OAUTH_TOKEN = "TwitterSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
    private static final String SAVE_STATE_KEY_OAUTH_SECRET = "TwitterSocialNetwork.SAVE_STATE_KEY_OAUTH_SECRET";
    private static final String SAVE_STATE_RUNNING_REQUESTS = "TwitterSocialNetwork.SAVE_STATE_RUNNING_REQUESTS";
    private static final String SAVE_STATE_LOGIN_2_URI = "TwitterSocialNetwork.SAVE_STATE_LOGIN_2_URI";

    private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";

    private static final int REQUEST_AUTH = UUID.randomUUID().hashCode();

    private final String TWITTER_CALLBACK_URL = "oauth://AndroidSocialNetworks";
    private final String fConsumerKey;
    private final String fConsumerSecret;

    private Twitter mTwitter;
    private RequestToken mRequestToken;

    private LoginAsyncTask mLoginAsyncTask;
    private Login2AsyncTask mLogin2AsyncTask;

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

    @Override
    public void login() {
        if (mLoginAsyncTask != null) {
            throw new IllegalStateException("login already started, please wait for complete");
        }

        mLoginAsyncTask = new LoginAsyncTask();
        mLoginAsyncTask.execute();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSharedPreferences.contains(SAVE_STATE_RUNNING_REQUESTS)) {
            String savedRequests = mSharedPreferences.getString(SAVE_STATE_RUNNING_REQUESTS, null);
            String[] runningRequests = savedRequests.split("#");

            for (String request : runningRequests) {
                if (request.equals(LoginAsyncTask.class.getSimpleName())) {
                    mLoginAsyncTask = new LoginAsyncTask();
                    mLoginAsyncTask.execute();
                }
            }

            mSharedPreferences.edit().remove(SAVE_STATE_RUNNING_REQUESTS).apply();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Set<String> runningRequests = new HashSet<String>();

        if (mLoginAsyncTask != null) {
            mLoginAsyncTask.cancel(true);
            mLoginAsyncTask = null;

            runningRequests.add(LoginAsyncTask.class.getSimpleName());
        }

        String finalValue = "";
        for (String request : runningRequests) {
            finalValue += request;
            finalValue += '#';
        }

        mSharedPreferences.edit().putString(SAVE_STATE_RUNNING_REQUESTS, finalValue).apply();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode + " : " + resultCode + " : " + data);

        Uri uri = data.getData();

        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
            String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
            mSharedPreferences.edit().putString(SAVE_STATE_LOGIN_2_URI, verifier).apply();

            mLogin2AsyncTask = new Login2AsyncTask();
            mLogin2AsyncTask.execute(verifier);
        } else {
            if (mOnLoginCompleteListener != null) {
                mOnLoginCompleteListener.onLoginFailed(getID(), "incorrect URI returned: " + uri);
            }
        }
    }

    private class LoginAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "LoginAsyncTask.onPreExecute()");
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "LoginAsyncTask.doInBackground()");

            try {
                mRequestToken = mTwitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                Uri oauthLoginURL = Uri.parse(mRequestToken.getAuthenticationURL() + "&force_login=true");

                Log.d(TAG, "oauthLoginURL: " + oauthLoginURL);

                Intent intent = new Intent(mActivity, OAuthActivity.class)
                        .putExtra(OAuthActivity.PARAM_CALLBACK, TWITTER_CALLBACK_URL)
                        .putExtra(OAuthActivity.PARAM_URL_TO_LOAD, oauthLoginURL.toString());

                mActivity.startActivityForResult(intent, REQUEST_AUTH);
            } catch (TwitterException e) {
                Log.e(TAG, "ERROR", e);
                return e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String error) {
            Log.d(TAG, "LoginAsyncTask.onPostExecute()");

            mLoginAsyncTask = null;
        }
    }

    private class Login2AsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String verifier = params[0];

            try {
                AccessToken accessToken = mTwitter.getOAuthAccessToken(mRequestToken, verifier);

                // Shared Preferences
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(SAVE_STATE_KEY_OAUTH_TOKEN, accessToken.getToken());
                editor.putString(SAVE_STATE_KEY_OAUTH_TOKEN, accessToken.getTokenSecret());
                editor.commit();
            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);
                return e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String error) {
            mLogin2AsyncTask = null;
            mSharedPreferences.edit().remove(SAVE_STATE_LOGIN_2_URI).apply();

            if (mOnLoginCompleteListener != null) {
                if (error == null) {
                    mOnLoginCompleteListener.onLoginSuccess(getID());
                } else {
                    mOnLoginCompleteListener.onLoginFailed(getID(), error);
                }
            }
        }
    }
}
