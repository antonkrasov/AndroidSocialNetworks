package com.androidsocialnetworks.lib.impl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.OAuthActivity;
import com.androidsocialnetworks.lib.OAuthSocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkAsyncTask;
import com.androidsocialnetworks.lib.SocialNetworkException;
import com.androidsocialnetworks.lib.SocialPerson;
import com.androidsocialnetworks.lib.listener.OnCheckIsFriendCompleteListener;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnPostingCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestAddFriendCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestRemoveFriendCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
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
import com.google.code.linkedinapi.schema.Position;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LinkedInSocialNetwork extends OAuthSocialNetwork {
    public static final int ID = 2;

    public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
    public static final String OAUTH_CALLBACK_HOST = "linkedinApiTestCallback";
    public static final String OAUTH_CALLBACK_URL = String.format("%s://%s", OAUTH_CALLBACK_SCHEME, OAUTH_CALLBACK_HOST);
    public static final String OAUTH_QUERY_TOKEN = "oauth_token";
    public static final String OAUTH_QUERY_VERIFIER = "oauth_verifier";
    public static final String OAUTH_QUERY_PROBLEM = "oauth_problem";

    private static final String TAG = LinkedInSocialNetwork.class.getSimpleName();
    private static final String SAVE_STATE_KEY_OAUTH_TOKEN = "LinkedInSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
    private static final String SAVE_STATE_KEY_OAUTH_SECRET = "LinkedInSocialNetwork.SAVE_STATE_KEY_OAUTH_SECRET";

    //    internal server error
//    private static final EnumSet<ProfileField> PROFILE_PARAMETERS = EnumSet.allOf(ProfileField.class);
    private static final EnumSet<ProfileField> PROFILE_PARAMETERS = EnumSet.of(
            ProfileField.ID,
            ProfileField.FIRST_NAME,
            ProfileField.LAST_NAME,
            ProfileField.POSITIONS,
            ProfileField.CONNECTIONS,
            ProfileField.API_STANDARD_PROFILE_REQUEST_HEADERS,
            ProfileField.PICTURE_URL,
            ProfileField.PUBLIC_PROFILE_URL
    );

    // max 16 bit to use in startActivityForResult
    private static final int REQUEST_AUTH = UUID.randomUUID().hashCode() & 0xFFFF;

    private final LinkedInOAuthService mOAuthService;
    private final LinkedInApiClientFactory mLinkedInApiClientFactory;

    private String mOAuthTokenSecret;

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
    public AccessToken getAccessToken() {
        return new AccessToken(
                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null),
                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null)
        );
    }

    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);

        executeRequest(new RequestLoginAsyncTask(), null, REQUEST_LOGIN);
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
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);

        executeRequest(new RequestCurrentPersonAsyncTask(), null, REQUEST_GET_CURRENT_PERSON);
    }

    @Override
    public void requestSocialPerson(String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestSocialPerson(userID, onRequestSocialPersonCompleteListener);

        Bundle args = new Bundle();
        args.putString(RequestSocialPersonAsyncTask.PARAM_USER_ID, userID);
        executeRequest(new RequestSocialPersonAsyncTask(), args, REQUEST_GET_PERSON);
    }

    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostMessage(message, onPostingCompleteListener);

        Bundle args = new Bundle();
        args.putString(RequestPostMessageAsyncTask.PARAM_MESSAGE, message);
        executeRequest(new RequestPostMessageAsyncTask(), args, REQUEST_POST_MESSAGE);
    }

    @Override
    public void requestPostPhoto(File photo, String message, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostPhoto isn't allowed for LinkedInSocialNetwork");
    }

    @Override
    public void requestCheckIsFriend(String userID, OnCheckIsFriendCompleteListener onCheckIsFriendCompleteListener) {
        super.requestCheckIsFriend(userID, onCheckIsFriendCompleteListener);

        Bundle args = new Bundle();
        args.putString(RequestCheckIsFriendAsyncTask.PARAM_USER_ID, userID);
        executeRequest(new RequestCheckIsFriendAsyncTask(), args, REQUEST_CHECK_IS_FRIEND);
    }

    @Override
    public void requestAddFriend(String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        super.requestAddFriend(userID, onRequestAddFriendCompleteListener);

        Bundle args = new Bundle();
        args.putString(RequestSendInviteAsyncTask.PARAM_USER_ID, userID);
        executeRequest(new RequestSendInviteAsyncTask(), args, REQUEST_ADD_FRIEND);
    }

    @Override
    public void requestRemoveFriend(String userID, OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener) {
        throw new SocialNetworkException("requestRemoveFriend isn't allowed for LinkedInSocialNetwork");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_AUTH) return;

        if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, "Login canceled", null);
            }

            return;
        }

        Uri uri = data.getData();

        final String problem = uri.getQueryParameter(OAUTH_QUERY_PROBLEM);
        if (problem != null) {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, problem, null);
            }

            return;
        }

        Bundle args = new Bundle();
        args.putString(RequestLogin2AsyncTask.PARAM_VERIFIER, uri.toString());
        args.putString(RequestLogin2AsyncTask.PARAM_AUTH_REQUEST_TOKEN, mOAuthTokenSecret);
        executeRequest(new RequestLogin2AsyncTask(), args, REQUEST_LOGIN2);
    }

    private void fatalError() {
        mSharedPreferences.edit()
                .remove(SAVE_STATE_KEY_OAUTH_TOKEN)
                .remove(SAVE_STATE_KEY_OAUTH_SECRET)
                .apply();
    }

    private class RequestLoginAsyncTask extends SocialNetworkAsyncTask {
        private static final String RESULT_URL = "RequestLoginAsyncTask.RESULT_URL";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle result = new Bundle();

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
            if (!handleRequestResult(result, REQUEST_LOGIN)) return;

            Intent intent = new Intent(mSocialNetworkManager.getActivity(), OAuthActivity.class)
                    .putExtra(OAuthActivity.PARAM_CALLBACK, OAUTH_CALLBACK_URL)
                    .putExtra(OAuthActivity.PARAM_URL_TO_LOAD, result.getString(RESULT_URL));

            mSocialNetworkManager.startActivityForResult(intent, REQUEST_AUTH);
        }
    }

    private class RequestLogin2AsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_AUTH_REQUEST_TOKEN = "PARAM_AUTH_REQUEST_TOKEN";
        public static final String PARAM_VERIFIER = "PARAM_VERIFIER";

        private static final String RESULT_TOKEN = "RESULT_TOKEN";
        private static final String RESULT_SECRET = "RESULT_SECRET";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            final String paramAuthRequestToken = args.getString(PARAM_AUTH_REQUEST_TOKEN);
            final String paramVerifier = args.getString(PARAM_VERIFIER);

            Uri uri = Uri.parse(paramVerifier);

            Bundle result = new Bundle();

            try {
                final LinkedInAccessToken accessToken = mOAuthService.getOAuthAccessToken(
                        new LinkedInRequestToken(
                                uri.getQueryParameter(OAUTH_QUERY_TOKEN),
                                paramAuthRequestToken
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
            mRequests.remove(REQUEST_LOGIN2);
            if (!handleRequestResult(result, REQUEST_LOGIN)) return;

            // Shared Preferences
            mSharedPreferences.edit()
                    .putString(SAVE_STATE_KEY_OAUTH_TOKEN, result.getString(RESULT_TOKEN))
                    .putString(SAVE_STATE_KEY_OAUTH_SECRET, result.getString(RESULT_SECRET))
                    .apply();

            ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
            mLocalListeners.remove(REQUEST_LOGIN);
        }
    }

    private class RequestCurrentPersonAsyncTask extends SocialNetworkAsyncTask {
        private static final String RESULT_ID = "LoginAsyncTask.RESULT_ID";
        private static final String RESULT_NAME = "LoginAsyncTask.RESULT_NAME";
        private static final String RESULT_AVATAR_URL = "LoginAsyncTask.RESULT_AVATAR_URL";
        private static final String RESULT_COMPANY = "LoginAsyncTask.RESULT_COMPANY";
        private static final String RESULT_POSITION = "LoginAsyncTask.RESULT_POSITION";
        private static final String RESULT_PROFILE_URL = "LoginAsyncTask.RESULT_PROFILE_URL";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle result = new Bundle();

            try {
                LinkedInApiClient client = mLinkedInApiClientFactory.createLinkedInApiClient(
                        new LinkedInAccessToken(
                                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null),
                                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null)
                        )
                );
                Person person = client.getProfileForCurrentUser(PROFILE_PARAMETERS);

                result.putString(RESULT_ID, person.getId());
                result.putString(RESULT_NAME, person.getFirstName() + " " + person.getLastName());
                result.putString(RESULT_AVATAR_URL, person.getPictureUrl());
                result.putString(RESULT_PROFILE_URL, person.getPublicProfileUrl());

                List<Position> positions = person.getPositions().getPositionList();
                if (positions.size() > 0) {
                    Position position = positions.get(positions.size() - 1);

                    result.putString(RESULT_COMPANY, position.getCompany().getName());
                    result.putString(RESULT_POSITION, position.getTitle());
                }
            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_GET_CURRENT_PERSON)) return;

            SocialPerson socialPerson = new SocialPerson();
            socialPerson.id = result.getString(RESULT_ID);
            socialPerson.name = result.getString(RESULT_NAME);
            socialPerson.avatarURL = result.getString(RESULT_AVATAR_URL);
            socialPerson.company = result.getString(RESULT_COMPANY);
            socialPerson.position = result.getString(RESULT_POSITION);
            socialPerson.profileURL = result.getString(RESULT_PROFILE_URL);

            ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_CURRENT_PERSON)).
                    onRequestSocialPersonSuccess(getID(), socialPerson);
            mLocalListeners.remove(REQUEST_GET_CURRENT_PERSON);
        }
    }

    private class RequestSocialPersonAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "PARAM_USER_ID";

        private static final String RESULT_ID = "LoginAsyncTask.RESULT_ID";
        private static final String RESULT_NAME = "LoginAsyncTask.RESULT_NAME";
        private static final String RESULT_AVATAR_URL = "LoginAsyncTask.RESULT_AVATAR_URL";
        private static final String RESULT_COMPANY = "LoginAsyncTask.RESULT_COMPANY";
        private static final String RESULT_POSITION = "LoginAsyncTask.RESULT_POSITION";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String userID = params[0].getString(PARAM_USER_ID);

            Bundle result = new Bundle();

            try {
                LinkedInApiClient client = mLinkedInApiClientFactory.createLinkedInApiClient(
                        new LinkedInAccessToken(
                                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null),
                                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null)
                        )
                );
                Person person = client.getProfileById(userID);

                result.putString(RESULT_ID, person.getId());
                result.putString(RESULT_NAME, person.getFirstName() + " " + person.getLastName());
                result.putString(RESULT_AVATAR_URL, person.getPictureUrl());

                List<Position> positions = person.getPositions() == null ?
                        new ArrayList<Position>() : person.getPositions().getPositionList();
                if (positions.size() > 0) {
                    Position position = positions.get(positions.size() - 1);

                    result.putString(RESULT_COMPANY, position.getCompany().getName());
                    result.putString(RESULT_POSITION, position.getTitle());
                }
            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_GET_PERSON)) return;

            SocialPerson socialPerson = new SocialPerson();
            socialPerson.id = result.getString(RESULT_ID);
            socialPerson.name = result.getString(RESULT_NAME);
            socialPerson.avatarURL = result.getString(RESULT_AVATAR_URL);
            socialPerson.company = result.getString(RESULT_COMPANY);
            socialPerson.position = result.getString(RESULT_POSITION);

            ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_PERSON)).
                    onRequestSocialPersonSuccess(getID(), socialPerson);
            mLocalListeners.remove(REQUEST_GET_PERSON);
        }
    }

    private class RequestPostMessageAsyncTask extends SocialNetworkAsyncTask {
        private static final String PARAM_MESSAGE = "PARAM_MESSAGE";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String message = params[0].getString(PARAM_MESSAGE);

            Bundle result = new Bundle();

            try {
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
            if (!handleRequestResult(result, REQUEST_POST_MESSAGE)) return;

            ((OnPostingCompleteListener) mLocalListeners.get(REQUEST_POST_MESSAGE))
                    .onPostSuccessfully(getID());
        }
    }

    private class RequestCheckIsFriendAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "PARAM_USER_ID";

        private static final String RESULT_IS_FRIEND = "RESULT_IS_FRIEND";
        private static final String RESULT_REQUESTED_ID = "RESULT_REQUESTED_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String userID = params[0].getString(PARAM_USER_ID);

            Bundle result = new Bundle();
            result.putString(RESULT_REQUESTED_ID, userID);

            try {
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
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_CHECK_IS_FRIEND,
                    result.getString(RESULT_REQUESTED_ID))) return;

            ((OnCheckIsFriendCompleteListener) mLocalListeners.get(REQUEST_CHECK_IS_FRIEND))
                    .onCheckIsFriendComplete(
                            getID(),
                            result.getString(RESULT_REQUESTED_ID),
                            result.getBoolean(RESULT_IS_FRIEND)
                    );
            mLocalListeners.remove(REQUEST_CHECK_IS_FRIEND);
        }
    }

    private class RequestSendInviteAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "PARAM_USER_ID";
        private static final String RESULT_REQUESTED_ID = "RESULT_REQUESTED_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String userID = params[0].getString(PARAM_USER_ID);

            Bundle result = new Bundle();

            try {
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
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_ADD_FRIEND,
                    result.getString(RESULT_REQUESTED_ID))) return;

            ((OnRequestAddFriendCompleteListener) mLocalListeners.get(REQUEST_ADD_FRIEND))
                    .onRequestAddFriendComplete(
                            getID(),
                            result.getString(RESULT_REQUESTED_ID)
                    );
            mLocalListeners.remove(REQUEST_CHECK_IS_FRIEND);
        }
    }
}
