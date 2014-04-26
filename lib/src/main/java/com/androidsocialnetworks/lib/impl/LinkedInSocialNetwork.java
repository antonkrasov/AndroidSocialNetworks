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
import com.androidsocialnetworks.lib.SocialPerson;
import com.google.code.linkedinapi.client.CommunicationsApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.constant.ApplicationConstants;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Headers;
import com.google.code.linkedinapi.schema.HttpHeader;
import com.google.code.linkedinapi.schema.Person;

import java.io.File;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
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

    private static final EnumSet<ProfileField> PROFILE_PARAMETERS = EnumSet.allOf(ProfileField.class);

    // max 16 bit to use in startActivityForResult
    private static final int REQUEST_AUTH = UUID.randomUUID().hashCode() & 0xFFFF;

    private final LinkedInOAuthService mOAuthService;
    private final LinkedInApiClientFactory mLinkedInApiClientFactory;

    private String mOAuthTokenSecret;

    private RequestLoginAsyncTask mRequestLoginAsyncTask;
    private RequestLogin2AsyncTask mRequestLogin2AsyncTask;
    private RequestGetPersonAsyncTask mRequestGetPersonAsyncTask;
    private RequestPostMessageAsyncTask mRequestPostMessageAsyncTask;
    private RequestCheckIsFriendAsyncTask mRequestCheckIsFriendAsyncTask;
    private RequestAddFriendAsyncTask mRequestAddFriendAsyncTask;

    public LinkedInSocialNetwork(Fragment fragment, String consumerKey, String consumerSecret, String permissions) {
        super(fragment);
        Log.d(TAG, "new LinkedInSocialNetwork: " + consumerKey + " : " + consumerSecret + " : " + permissions);

        if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret) || TextUtils.isEmpty(permissions)) {
            throw new IllegalArgumentException("TextUtils.isEmpty(fConsumerKey) || TextUtils.isEmpty(fConsumerSecret) || TextUtils.isEmpty(fPermissions)");
        }

        mOAuthService = LinkedInOAuthServiceFactory.getInstance()
                .createLinkedInOAuthService(consumerKey, consumerSecret, permissions);
        mLinkedInApiClientFactory = LinkedInApiClientFactory.newInstance(consumerKey, consumerSecret);
    }

    @Override
    public boolean isConnected() {
        String accessToken = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
        String accessTokenSecret = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null);
        return accessToken != null && accessTokenSecret != null;
    }

    @Override
    public void login() {
        mRequestLoginAsyncTask = new RequestLoginAsyncTask();
        mRequestLoginAsyncTask.execute();
    }

    @Override
    public void logout() {
        fatalError();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void requestPerson() {
        if (mRequestGetPersonAsyncTask != null) {
            throw new RuntimeException("mRequestGetPersonAsyncTask already running");
        }

        LinkedInAccessToken accessToken = new LinkedInAccessToken(
                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, ""),
                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, "")
        );

        mRequestGetPersonAsyncTask = new RequestGetPersonAsyncTask();
        mRequestGetPersonAsyncTask.execute(accessToken);
    }

    public void postMessage(String message) {
        mRequestPostMessageAsyncTask = new RequestPostMessageAsyncTask();
        mRequestPostMessageAsyncTask.execute(message);
    }

    public void postPhoto(File photo, String message) {
        throw new IllegalStateException("Posting photo isn't allowed for LinkedIn");
    }

    @Override
    public void isFriend(String userID) {
        if (mRequestCheckIsFriendAsyncTask != null) {
            throw new IllegalStateException("mRequestCheckIsFriendAsyncTask already running");
        }

        mRequestCheckIsFriendAsyncTask = new RequestCheckIsFriendAsyncTask();
        mRequestCheckIsFriendAsyncTask.execute(userID);
    }

    @Override
    public void addFriend(String userID) {
        if (mRequestAddFriendAsyncTask != null) {
            throw new IllegalStateException("mRequestAddFriendAsyncTask already running");
        }

        mRequestAddFriendAsyncTask = new RequestAddFriendAsyncTask();
        mRequestAddFriendAsyncTask.execute(userID);
    }

    @Override
    public void removeFriend(String userID) {
        throw new IllegalStateException("removeFriend isn't allowed for LinkedIn");
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

        mRequestLogin2AsyncTask = new RequestLogin2AsyncTask();
        mRequestLogin2AsyncTask.execute(mOAuthTokenSecret, uri.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopAllRequests();
    }

    private void stopAllRequests() {
        Log.d(TAG, "stopAllRequests()");
        final boolean mayInterruptIfRunning = true;

        if (mRequestLoginAsyncTask != null) {
            mRequestLoginAsyncTask.cancel(mayInterruptIfRunning);
            mRequestLoginAsyncTask = null;
        }
        if (mRequestLogin2AsyncTask != null) {
            mRequestLogin2AsyncTask.cancel(mayInterruptIfRunning);
            mRequestLogin2AsyncTask = null;
        }
        if (mRequestGetPersonAsyncTask != null) {
            mRequestGetPersonAsyncTask.cancel(mayInterruptIfRunning);
            mRequestGetPersonAsyncTask = null;
        }
        if (mRequestPostMessageAsyncTask != null) {
            mRequestPostMessageAsyncTask.cancel(mayInterruptIfRunning);
            mRequestPostMessageAsyncTask = null;
        }
        if (mRequestCheckIsFriendAsyncTask != null) {
            mRequestCheckIsFriendAsyncTask.cancel(mayInterruptIfRunning);
            mRequestCheckIsFriendAsyncTask = null;
        }
        if (mRequestAddFriendAsyncTask != null) {
            mRequestAddFriendAsyncTask.cancel(mayInterruptIfRunning);
            mRequestAddFriendAsyncTask = null;
        }
    }

    private void fatalError() {
        mSharedPreferences.edit()
                .remove(SAVE_STATE_KEY_OAUTH_TOKEN)
                .remove(SAVE_STATE_KEY_OAUTH_SECRET)
                .apply();
    }

    private class RequestLoginAsyncTask extends AsyncTask<String, String, Bundle> {
        private static final String RESULT_ERROR = "RequestLoginAsyncTask.RESULT_ERROR";
        private static final String RESULT_URL = "RequestLoginAsyncTask.RESULT_URL";

        @Override
        protected Bundle doInBackground(String... params) {
            Bundle result = new Bundle();

            Log.d(TAG, "LoginAsyncTask.doInBackground()");

            try {
                final LinkedInRequestToken liToken = mOAuthService.getOAuthRequestToken(OAUTH_CALLBACK_URL);

                mOAuthTokenSecret = liToken.getTokenSecret();

                result.putString(RESULT_URL, liToken.getAuthorizationUrl());
            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            mRequestLoginAsyncTask = null;

            if (result.containsKey(RESULT_ERROR) && mOnLoginCompleteListener != null) {
                mOnLoginCompleteListener.onLoginFailed(getID(), result.getString(RESULT_ERROR));
                return;
            }

            Intent intent = new Intent(mSocialNetworkManager.getActivity(), OAuthActivity.class)
                    .putExtra(OAuthActivity.PARAM_CALLBACK, OAUTH_CALLBACK_URL)
                    .putExtra(OAuthActivity.PARAM_URL_TO_LOAD, result.getString(RESULT_URL));

            mSocialNetworkManager.startActivityForResult(intent, REQUEST_AUTH);
        }
    }

    private class RequestLogin2AsyncTask extends AsyncTask<String, Void, Bundle> {
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
            mRequestLogin2AsyncTask = null;

            mOAuthTokenSecret = null;

            String error = result.containsKey(RESULT_ERROR) ? result.getString(RESULT_ERROR) : null;

            if (error == null) {
                // Shared Preferences
                mSharedPreferences.edit()
                        .putString(SAVE_STATE_KEY_OAUTH_TOKEN, result.getString(RESULT_TOKEN))
                        .putString(SAVE_STATE_KEY_OAUTH_SECRET, result.getString(RESULT_SECRET))
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

    private class RequestGetPersonAsyncTask extends AsyncTask<LinkedInAccessToken, Void, Bundle> {
        private static final String RESULT_ERROR = "LoginAsyncTask.RESULT_ERROR";
        private static final String RESULT_ID = "LoginAsyncTask.RESULT_ID";
        private static final String RESULT_NAME = "LoginAsyncTask.RESULT_NAME";
        private static final String RESULT_AVATAR_URL = "LoginAsyncTask.RESULT_AVATAR_URL";

        @Override
        protected Bundle doInBackground(LinkedInAccessToken... params) {
            Bundle result = new Bundle();

            try {
                LinkedInApiClient client = mLinkedInApiClientFactory.createLinkedInApiClient(params[0]);
                Person person = client.getProfileForCurrentUser(PROFILE_PARAMETERS);

                result.putString(RESULT_NAME, person.getFirstName() + " " + person.getLastName());
                result.putString(RESULT_AVATAR_URL, person.getPictureUrl());
            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            mRequestGetPersonAsyncTask = null;
            String error = result.containsKey(RESULT_ERROR) ? result.getString(RESULT_ERROR) : null;

            if (error != null) {
                fatalError();

                if (mOnRequestSocialPersonListener != null) {
                    mOnRequestSocialPersonListener.onRequestSocialPersonFailed(getID(), error);
                }
                return;
            }

            if (mOnRequestSocialPersonListener != null) {
                SocialPerson socialPerson = new SocialPerson();
                socialPerson.id = result.getString(RESULT_ID);
                socialPerson.name = result.getString(RESULT_NAME);
                socialPerson.avatarURL = result.getString(RESULT_AVATAR_URL);

                mOnRequestSocialPersonListener.onRequestSocialPersonSuccess(getID(), socialPerson);
            }

        }
    }

    private class RequestPostMessageAsyncTask extends AsyncTask<String, Void, Bundle> {
        private static final String RESULT_ERROR = "LoginAsyncTask.RESULT_ERROR";

        @Override
        protected Bundle doInBackground(String... params) {
            Bundle result = new Bundle();

            try {
                String message = params[0];

                LinkedInApiClient apiClient = mLinkedInApiClientFactory.createLinkedInApiClient(
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, ""),
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, "")
                );

                apiClient.updateCurrentStatus(message);
            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);

                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            mRequestPostMessageAsyncTask = null;

            String error = result.containsKey(RESULT_ERROR) ? result.getString(RESULT_ERROR) : null;

            if (mOnPostingListener == null) return;

            if (error == null) {
                mOnPostingListener.onPostSuccessfully(getID());
            } else {
                mOnPostingListener.onPostFailed(getID(), error);
            }
        }
    }

    private class RequestCheckIsFriendAsyncTask extends AsyncTask<String, Void, Bundle> {
        private static final String RESULT_ERROR = "RequestCheckIsFriendAsyncTask.RESULT_ERROR";
        private static final String RESULT_IS_FRIEND = "RequestCheckIsFriendAsyncTask.RESULT_IS_FRIEND";
        private static final String RESULT_REQUESTED_ID = "RequestCheckIsFriendAsyncTask.RESULT_REQUESTED_ID";

        @Override
        protected Bundle doInBackground(String... params) {
            Bundle result = new Bundle();

            try {
                String userID = params[0];
                result.putString(RESULT_REQUESTED_ID, userID);

                LinkedInApiClient apiClient = mLinkedInApiClientFactory.createLinkedInApiClient(
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, ""),
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, "")
                );

                Person person = apiClient.getProfileForCurrentUser(PROFILE_PARAMETERS);

                List<Person> list = person.getConnections().getPersonList();
                if (list != null) {
                    for (Person p : list) {
                        if (p.getId().equals(userID)) {
                            result.putBoolean(RESULT_IS_FRIEND, true);
                            return result;
                        }
                    }
                }

                result.putBoolean(RESULT_IS_FRIEND, false);
            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            mRequestCheckIsFriendAsyncTask = null;

            String error = bundle.containsKey(RESULT_ERROR) ? bundle.getString(RESULT_ERROR) : null;

            if (mOnCheckingIsFriendListener != null) {
                if (error != null) {
                    mOnCheckingIsFriendListener.onCheckIsFriendFailed(getID(), bundle.getString(RESULT_REQUESTED_ID), error);
                } else {
                    mOnCheckingIsFriendListener.onCheckIsFriendSuccess(
                            getID(),
                            bundle.getString(RESULT_REQUESTED_ID),
                            bundle.getBoolean(RESULT_IS_FRIEND)
                    );
                }
            }
        }
    }

    private class RequestAddFriendAsyncTask extends AsyncTask<String, Void, Bundle> {
        private static final String RESULT_ERROR = "RequestAddFriendAsyncTask.RESULT_ERROR";
        private static final String RESULT_REQUESTED_ID = "RequestAddFriendAsyncTask.RESULT_REQUESTED_ID";

        @Override
        protected Bundle doInBackground(String... params) {
            Bundle result = new Bundle();

            try {
                String userID = params[0];
                result.putString(RESULT_REQUESTED_ID, userID);

                LinkedInApiClient apiClient = mLinkedInApiClientFactory.createLinkedInApiClient(
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, ""),
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, "")
                );

                Set<ProfileField> fields = new HashSet<ProfileField>();
                fields.add(ProfileField.API_STANDARD_PROFILE_REQUEST);

                Person person = apiClient.getProfileById(userID, fields);

                String authHeader = "";
                Headers headers = person.getApiStandardProfileRequest().getHeaders();
                List<HttpHeader> httpHeaders = headers.getHttpHeaderList();
                for (HttpHeader httpHeader : httpHeaders) {
                    if (httpHeader.getName().equals(ApplicationConstants.AUTH_HEADER_NAME)) {
                        authHeader = httpHeader.getValue();
                        break;
                    }
                }

                CommunicationsApiClient communicationsApiClient = mLinkedInApiClientFactory.createCommunicationsApiClient(
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, ""),
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, "")
                );
                communicationsApiClient.sendInviteById(userID, "Join my network on LinkedIn",
                        "Since you are a person I trust, I wanted to invite you to join my network on LinkedIn.", authHeader);
            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            mRequestAddFriendAsyncTask = null;

            String error = bundle.containsKey(RESULT_ERROR) ? bundle.getString(RESULT_ERROR) : null;

            if (mOnAddFriendListener != null) {
                if (error != null) {
                    mOnAddFriendListener.onAddFriendFailed(getID(), bundle.getString(RESULT_REQUESTED_ID), error);
                } else {
                    mOnAddFriendListener.onAddFriendSuccess(
                            getID(),
                            bundle.getString(RESULT_REQUESTED_ID)
                    );
                }
            }
        }
    }

}
