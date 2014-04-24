package com.androidsocialnetworks.lib.impl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.androidsocialnetworks.lib.OAuthActivity;
import com.androidsocialnetworks.lib.SocialNetwork;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LinkedInSocialNetwork extends SocialNetwork {
    public static final int ID = UUID.randomUUID().hashCode();
    public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
    public static final String OAUTH_CALLBACK_HOST = "linkedinApiTestCallback";
    public static final String OAUTH_CALLBACK_URL = String.format("%s://%s", OAUTH_CALLBACK_SCHEME, OAUTH_CALLBACK_HOST);
    public static final String OAUTH_QUERY_TOKEN = "oauth_token";
    public static final String OAUTH_QUERY_VERIFIER = "oauth_verifier";
    public static final String OAUTH_QUERY_PROBLEM = "oauth_problem";
    private static final String TAG = LinkedInSocialNetwork.class.getSimpleName();
    private static final String SAVE_STATE_KEY_OAUTH_TOKEN = "LinkedInSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
    private static final String SAVE_STATE_KEY_OAUTH_SECRET = "LinkedInSocialNetwork.SAVE_STATE_KEY_OAUTH_SECRET";
    private static final String SAVE_STATE_RUNNING_REQUESTS = "LinkedInSocialNetwork.SAVE_STATE_RUNNING_REQUESTS";
    private static final String SAVE_STATE_AUTH_REQUEST_URL = "LinkedInSocialNetwork.SAVE_STATE_AUTH_URL";
    private static final String SAVE_STATE_AUTH_REQUEST_TOKEN = "LinkedInSocialNetwork.SAVE_STATE_AUTH_REQUEST_TOKEN";
    private static final String SAVE_STATE_LOGIN2_URI = "LinkedInSocialNetwork.SAVE_STATE_LOGIN2_URI";
    private static final EnumSet<ProfileField> PROFILE_PARAMETERS = EnumSet.allOf(ProfileField.class);

    // max 16 bit to use in startActivityForResult
    private static final int REQUEST_AUTH = UUID.randomUUID().hashCode() & 0xFFFF;

    private final String fConsumerKey;
    private final String fConsumerSecret;
    private final String fPermissions;

    private final LinkedInOAuthService mOAuthService;
    private final LinkedInApiClientFactory mLinkedInApiClientFactory;

    private LoginAsyncTask mLoginAsyncTask;
    private Login2AsyncTask mLogin2AsyncTask;

    public LinkedInSocialNetwork(Fragment fragment, String consumerKey, String consumerSecret, String permissions) {
        super(fragment);
        Log.d(TAG, "new LinkedInSocialNetwork: " + consumerKey + " : " + consumerSecret + " : " + permissions);

        fConsumerKey = consumerKey;
        fConsumerSecret = consumerSecret;
        fPermissions = permissions;

        if (TextUtils.isEmpty(fConsumerKey) || TextUtils.isEmpty(fConsumerSecret) || TextUtils.isEmpty(fPermissions)) {
            throw new IllegalArgumentException("TextUtils.isEmpty(fConsumerKey) || TextUtils.isEmpty(fConsumerSecret) || TextUtils.isEmpty(fPermissions)");
        }

        mOAuthService = LinkedInOAuthServiceFactory.getInstance()
                .createLinkedInOAuthService(fConsumerKey, fConsumerSecret, fPermissions);
        mLinkedInApiClientFactory = LinkedInApiClientFactory.newInstance(fConsumerKey, fConsumerSecret);
    }

    @Override
    public boolean isConnected() {
        String accessToken = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
        String accessTokenSecret = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null);
        return accessToken != null && accessTokenSecret != null;
    }

    @Override
    public void login() {
        mLoginAsyncTask = new LoginAsyncTask();
        mLoginAsyncTask.execute();
    }

    @Override
    public void logout() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void requestPerson() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSharedPreferences.contains(SAVE_STATE_RUNNING_REQUESTS)) {
            String savedRequests = mSharedPreferences.getString(SAVE_STATE_RUNNING_REQUESTS, null);
            String[] runningRequests = savedRequests.split("#");

            for (String request : runningRequests) {
                if (request.equals(LoginAsyncTask.class.getSimpleName())) {
                    mLogin2AsyncTask = new Login2AsyncTask();
                    mLogin2AsyncTask.execute();
                } else if (request.equals(Login2AsyncTask.class.getSimpleName())) {
                    String authRequestToken = mSharedPreferences.getString(SAVE_STATE_AUTH_REQUEST_TOKEN, "");
                    String login2Uri = mSharedPreferences.getString(SAVE_STATE_LOGIN2_URI, "");

                    mLogin2AsyncTask = new Login2AsyncTask();
                    mLogin2AsyncTask.execute(authRequestToken, login2Uri);
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

        String finalValue = "";
        for (String request : runningRequests) {
            finalValue += request;
            finalValue += '#';
        }

        mSharedPreferences.edit().putString(SAVE_STATE_RUNNING_REQUESTS, finalValue).apply();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
            if (mOnLoginCompleteListener != null) {
                mOnLoginCompleteListener.onLoginFailed(getID(), "Login canceled");
            }

            return;
        }

        Uri uri = data.getData();

        final String problem = uri.getQueryParameter(OAUTH_QUERY_PROBLEM);
        if (problem != null) {
            if (mOnLoginCompleteListener != null) {
                mOnLoginCompleteListener.onLoginFailed(getID(), problem);
            }

            return;
        }

        String authRequestToken = mSharedPreferences.getString(SAVE_STATE_AUTH_REQUEST_TOKEN, "");
        String login2Uri = uri.toString();

        mSharedPreferences.edit().putString(SAVE_STATE_LOGIN2_URI, login2Uri).apply();

        mLogin2AsyncTask = new Login2AsyncTask();
        mLogin2AsyncTask.execute(authRequestToken, login2Uri);
    }

    private class LoginAsyncTask extends AsyncTask<String, String, Bundle> {
        private static final String RESULT_ERROR = "LoginAsyncTask.RESULT_ERROR";
        private static final String RESULT_OAUTH_URL = "LoginAsyncTask.RESULT_OAUTH_URL";
        private static final String RESULT_OAUTH_TOKEN = "LoginAsyncTask.RESULT_OAUTH_TOKEN";

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "LoginAsyncTask.onPreExecute()");
        }

        @Override
        protected Bundle doInBackground(String... params) {
            Bundle result = new Bundle();

            Log.d(TAG, "LoginAsyncTask.doInBackground()");

            try {
                final LinkedInRequestToken liToken = mOAuthService.getOAuthRequestToken(OAUTH_CALLBACK_URL);
                result.putString(RESULT_OAUTH_URL, liToken.getAuthorizationUrl());
                result.putString(RESULT_OAUTH_TOKEN, liToken.getTokenSecret());
            } catch (Exception e) {
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
                return;
            }

            mSharedPreferences.edit()
                    .putString(SAVE_STATE_AUTH_REQUEST_URL, result.getString(RESULT_OAUTH_URL))
                    .putString(SAVE_STATE_AUTH_REQUEST_TOKEN, result.getString(RESULT_OAUTH_TOKEN))
                    .apply();

            Intent intent = new Intent(mSocialNetworkManager.getActivity(), OAuthActivity.class)
                    .putExtra(OAuthActivity.PARAM_CALLBACK, OAUTH_CALLBACK_URL)
                    .putExtra(OAuthActivity.PARAM_URL_TO_LOAD, result.getString(RESULT_OAUTH_URL));

            mSocialNetworkManager.startActivityForResult(intent, REQUEST_AUTH);
        }
    }

    private class Login2AsyncTask extends AsyncTask<String, Void, Bundle> {
        private static final String RESULT_ERROR = "LoginAsyncTask.RESULT_ERROR";
        private static final String RESULT_TOKEN = "LoginAsyncTask.RESULT_TOKEN";
        private static final String RESULT_SECRET = "LoginAsyncTask.RESULT_SECRET";

        @Override
        protected Bundle doInBackground(String... params) {
            Bundle result = new Bundle();

            try {
                String authRequestToken = params[0];
                Uri uri = Uri.parse(params[1]);

                Log.d(TAG, "Login2AsyncTask: authRequestToken: " + authRequestToken);
                Log.d(TAG, "Login2AsyncTask: uri: " + uri);

                final LinkedInAccessToken accessToken = mOAuthService.getOAuthAccessToken(
                        new LinkedInRequestToken(
                                uri.getQueryParameter(OAUTH_QUERY_TOKEN),
                                authRequestToken
                        ),
                        uri.getQueryParameter(OAUTH_QUERY_VERIFIER)
                );

                result.putString(RESULT_TOKEN, accessToken.getToken());
                result.putString(RESULT_SECRET, accessToken.getTokenSecret());
            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            mLogin2AsyncTask = null;

            String error = result.containsKey(RESULT_ERROR) ? result.getString(RESULT_ERROR) : null;

            if (error == null) {
                // Shared Preferences
                mSharedPreferences.edit()
                        .putString(SAVE_STATE_KEY_OAUTH_TOKEN, result.getString(RESULT_TOKEN))
                        .putString(SAVE_STATE_KEY_OAUTH_SECRET, result.getString(RESULT_SECRET))
                        .remove(SAVE_STATE_AUTH_REQUEST_URL)
                        .remove(SAVE_STATE_LOGIN2_URI)
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

}
