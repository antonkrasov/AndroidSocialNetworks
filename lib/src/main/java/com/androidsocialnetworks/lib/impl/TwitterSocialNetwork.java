package com.androidsocialnetworks.lib.impl;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.androidsocialnetworks.lib.OAuthActivity;
import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialPerson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterSocialNetwork extends SocialNetwork {
    public static final int ID = UUID.randomUUID().hashCode();
    private static final String TAG = TwitterSocialNetwork.class.getSimpleName();
    private static final String SAVE_STATE_KEY_OAUTH_TOKEN = "TwitterSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
    private static final String SAVE_STATE_KEY_OAUTH_SECRET = "TwitterSocialNetwork.SAVE_STATE_KEY_OAUTH_SECRET";
    private static final String SAVE_STATE_KEY_USER_ID = "TwitterSocialNetwork.SAVE_STATE_KEY_USER_ID";
    private static final String SAVE_STATE_RUNNING_REQUESTS = "TwitterSocialNetwork.SAVE_STATE_RUNNING_REQUESTS";
    private static final String SAVE_STATE_LOGIN_2_URI = "TwitterSocialNetwork.SAVE_STATE_LOGIN_2_URI";
    private static final String SAVE_STATE_LOGIN_2_REQUEST_TOKEN = "TwitterSocialNetwork.SAVE_STATE_LOGIN_2_REQUEST_TOKEN";

    private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";

    // max 16 bit to use in startActivityForResult
    private static final int REQUEST_AUTH = UUID.randomUUID().hashCode() & 0xFFFF;

    private final String TWITTER_CALLBACK_URL = "oauth://AndroidSocialNetworks";
    private final String fConsumerKey;
    private final String fConsumerSecret;

    private Twitter mTwitter;
    private RequestToken mRequestToken;

    private LoginAsyncTask mLoginAsyncTask;
    private Login2AsyncTask mLogin2AsyncTask;
    private RequestPersonAsyncTask mRequestPersonAsyncTask;

    public TwitterSocialNetwork(Fragment fragment, String consumerKey, String consumerSecret) {
        super(fragment);
        Log.d(TAG, "new TwitterSocialNetwork: " + consumerKey + " : " + consumerSecret);

        fConsumerKey = consumerKey;
        fConsumerSecret = consumerSecret;

        if (TextUtils.isEmpty(fConsumerKey) || TextUtils.isEmpty(fConsumerSecret)) {
            throw new IllegalArgumentException("consumerKey and consumerSecret are invalid");
        }

        initTwitterClient();
    }

    /**
     * Read the object from Base64 string.
     */
    private static Object fromString(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64.decode(s, Base64.DEFAULT);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    /**
     * Write the object to a Base64 string.
     */
    private static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
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
    public void logout() {
        mSharedPreferences.edit()
                .remove(SAVE_STATE_KEY_OAUTH_TOKEN)
                .remove(SAVE_STATE_KEY_OAUTH_SECRET)
                .apply();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void requestPerson() {
        long userID = mSharedPreferences.getLong(SAVE_STATE_KEY_USER_ID, -1);

        mRequestPersonAsyncTask = new RequestPersonAsyncTask();
        mRequestPersonAsyncTask.execute(userID);
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
                } else if (request.equals(Login2AsyncTask.class.getSimpleName())) {
                    String verifyer = mSharedPreferences.getString(SAVE_STATE_LOGIN_2_URI, "");
                    try {
                        mRequestToken = (RequestToken) fromString(mSharedPreferences.getString(SAVE_STATE_LOGIN_2_REQUEST_TOKEN, ""));
                    } catch (IOException e) {
                        Log.e(TAG, "ERROR", e);
                    } catch (ClassNotFoundException e) {
                        Log.e(TAG, "ERROR", e);
                    }

                    mLogin2AsyncTask = new Login2AsyncTask();
                    mLogin2AsyncTask.execute(verifyer);
                } else if (request.equals(RequestPersonAsyncTask.class.getSimpleName())) {
                    requestPerson();
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

        if (mLogin2AsyncTask != null) {
            mLogin2AsyncTask.cancel(true);
            mLogin2AsyncTask = null;

            runningRequests.add(Login2AsyncTask.class.getSimpleName());
        }

        if (mRequestPersonAsyncTask != null) {
            mRequestPersonAsyncTask.cancel(true);
            mRequestPersonAsyncTask = null;

            runningRequests.add(RequestPersonAsyncTask.class.getSimpleName());
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

        Uri uri = data != null ? data.getData() : null;

        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
            String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
            try {
                mSharedPreferences.edit()
                        .putString(SAVE_STATE_LOGIN_2_URI, verifier)
                        .putString(SAVE_STATE_LOGIN_2_REQUEST_TOKEN, toString(mRequestToken))
                        .apply();
            } catch (IOException e) {
                Log.e(TAG, "ERROR", e);
            }

            mLogin2AsyncTask = new Login2AsyncTask();
            mLogin2AsyncTask.execute(verifier);
        } else {
            if (mOnLoginCompleteListener != null) {
                mOnLoginCompleteListener.onLoginFailed(getID(), "incorrect URI returned: " + uri);
            }
        }
    }

    private class LoginAsyncTask extends AsyncTask<String, String, Bundle> {
        private static final String RESULT_ERROR = "LoginAsyncTask.RESULT_ERROR";
        private static final String RESULT_OAUTH_LOGIN = "LoginAsyncTask.RESULT_OAUTH_LOGIN";

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "LoginAsyncTask.onPreExecute()");
        }

        @Override
        protected Bundle doInBackground(String... params) {
            Bundle result = new Bundle();

            Log.d(TAG, "LoginAsyncTask.doInBackground()");

            try {
                mRequestToken = mTwitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                Uri oauthLoginURL = Uri.parse(mRequestToken.getAuthenticationURL() + "&force_login=true");

                Log.d(TAG, "oauthLoginURL: " + oauthLoginURL);

                result.putString(RESULT_OAUTH_LOGIN, oauthLoginURL.toString());
            } catch (TwitterException e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            Log.d(TAG, "LoginAsyncTask.onPostExecute()");

            mLoginAsyncTask = null;

            if (result.containsKey(RESULT_ERROR) && mOnLoginCompleteListener != null) {
                mOnLoginCompleteListener.onLoginFailed(getID(), result.getString(RESULT_ERROR));
            }

            if (result.containsKey(RESULT_OAUTH_LOGIN)) {
                Intent intent = new Intent(mSocialNetworkManager.getActivity(), OAuthActivity.class)
                        .putExtra(OAuthActivity.PARAM_CALLBACK, TWITTER_CALLBACK_URL)
                        .putExtra(OAuthActivity.PARAM_URL_TO_LOAD, result.getString(RESULT_OAUTH_LOGIN));

                mSocialNetworkManager.startActivityForResult(intent, REQUEST_AUTH);
            }
        }
    }

    private class Login2AsyncTask extends AsyncTask<String, Void, Bundle> {
        private static final String RESULT_ERROR = "LoginAsyncTask.RESULT_ERROR";
        private static final String RESULT_TOKEN = "LoginAsyncTask.RESULT_TOKEN";
        private static final String RESULT_SECRET = "LoginAsyncTask.RESULT_SECRET";
        private static final String RESULT_USER_ID = "LoginAsyncTask.RESULT_USER_ID";

        @Override
        protected Bundle doInBackground(String... params) {
            String verifier = params[0];

            Bundle result = new Bundle();

            try {
                AccessToken accessToken = mTwitter.getOAuthAccessToken(mRequestToken, verifier);

                result.putString(RESULT_TOKEN, accessToken.getToken());
                result.putString(RESULT_SECRET, accessToken.getTokenSecret());
                result.putLong(RESULT_USER_ID, accessToken.getUserId());
            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            mLogin2AsyncTask = null;
            mSharedPreferences.edit().remove(SAVE_STATE_LOGIN_2_URI).apply();

            String error = result.containsKey(RESULT_ERROR) ? result.getString(RESULT_ERROR) : null;

            if (error == null) {
                // Shared Preferences
                mSharedPreferences.edit()
                        .putString(SAVE_STATE_KEY_OAUTH_TOKEN, result.getString(RESULT_TOKEN))
                        .putString(SAVE_STATE_KEY_OAUTH_SECRET, result.getString(RESULT_SECRET))
                        .putLong(SAVE_STATE_KEY_USER_ID, result.getLong(RESULT_USER_ID))
                        .apply();
            }

            if (mOnLoginCompleteListener != null) {
                if (error == null) {
                    mOnLoginCompleteListener.onLoginSuccess(getID());
                } else {
                    mOnLoginCompleteListener.onLoginFailed(getID(), error);
                }
            }
        }
    }

    private class RequestPersonAsyncTask extends AsyncTask<Long, Void, Bundle> {
        private static final String RESULT_ERROR = "LoginAsyncTask.RESULT_ERROR";
        private static final String RESULT_ID = "LoginAsyncTask.RESULT_ID";
        private static final String RESULT_NAME = "LoginAsyncTask.RESULT_NAME";
        private static final String RESULT_AVATAR_URL = "LoginAsyncTask.RESULT_AVATAR_URL";

        @Override
        protected Bundle doInBackground(Long... params) {
            final long userID = params[0];
            Log.d(TAG, "load user: " + userID);

            Bundle result = new Bundle();

            try {
                User user = mTwitter.showUser(userID);

                result.putString(RESULT_ID, user.getId() + "");
                result.putString(RESULT_NAME, user.getName());
                result.putString(RESULT_AVATAR_URL, user.getBiggerProfileImageURL());
            } catch (TwitterException e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            mRequestPersonAsyncTask = null;

            String error = result.containsKey(RESULT_ERROR) ? result.getString(RESULT_ERROR) : null;

            if (mOnRequestSocialPersonListener != null) {
                if (error == null) {
                    SocialPerson socialPerson = new SocialPerson();
                    socialPerson.id = result.getString(RESULT_ID);
                    socialPerson.name = result.getString(RESULT_NAME);
                    socialPerson.avatarURL = result.getString(RESULT_AVATAR_URL);

                    mOnRequestSocialPersonListener.onRequestSocialPersonSuccess(getID(), socialPerson);
                } else {
                    mOnRequestSocialPersonListener.onRequestSocialPersonFailed(getID(), error);
                }
            }
        }
    }
}
