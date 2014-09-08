package com.androidsocialnetworks.lib.impl;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

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

import java.io.File;
import java.util.UUID;

import twitter4j.Relationship;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import static com.androidsocialnetworks.lib.Consts.TAG;

public class TwitterSocialNetwork extends OAuthSocialNetwork {
    public static final int ID = 1;
    private static final String SAVE_STATE_KEY_OAUTH_TOKEN = "TwitterSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
    private static final String SAVE_STATE_KEY_OAUTH_SECRET = "TwitterSocialNetwork.SAVE_STATE_KEY_OAUTH_SECRET";
    private static final String SAVE_STATE_KEY_USER_ID = "TwitterSocialNetwork.SAVE_STATE_KEY_USER_ID";

    private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";

    // max 16 bit to use in startActivityForResult
    private static final int REQUEST_AUTH = UUID.randomUUID().hashCode() & 0xFFFF;

    private final String TWITTER_CALLBACK_URL = "oauth://AndroidSocialNetworks";
    private final String fConsumerKey;
    private final String fConsumerSecret;

    private Twitter mTwitter;
    private RequestToken mRequestToken;

    public TwitterSocialNetwork(Fragment fragment, String consumerKey, String consumerSecret) {
        super(fragment);

        fConsumerKey = consumerKey;
        fConsumerSecret = consumerSecret;

        if (TextUtils.isEmpty(fConsumerKey) || TextUtils.isEmpty(fConsumerSecret)) {
            throw new IllegalArgumentException("consumerKey and consumerSecret are invalid");
        }

        /*
        *
        * No authentication challenges found
        * Relevant discussions can be found on the Internet at:
        * http://www.google.co.jp/search?q=8e063946 or
        * http://www.google.co.jp/search?q=ef59cf9f
        *
        * */
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
    public int getID() {
        return ID;
    }

    @Override
    public boolean isConnected() {
        String accessToken = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
        String accessTokenSecret = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null);
        return accessToken != null && accessTokenSecret != null;
    }

    @Override
    public com.androidsocialnetworks.lib.AccessToken getAccessToken() {
        return new com.androidsocialnetworks.lib.AccessToken(
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
        mSharedPreferences.edit()
                .remove(SAVE_STATE_KEY_OAUTH_TOKEN)
                .remove(SAVE_STATE_KEY_OAUTH_SECRET)
                .remove(SAVE_STATE_KEY_USER_ID)
                .apply();

        mTwitter = null;
        initTwitterClient();
    }

    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);

        executeRequest(new RequestGetPersonAsyncTask(), null, REQUEST_GET_CURRENT_PERSON);
    }

    @Override
    public void requestSocialPerson(String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestSocialPerson(userID, onRequestSocialPersonCompleteListener);

        if (TextUtils.isEmpty(userID)) {
            throw new SocialNetworkException("userID can't be null or empty");
        }

        Bundle args = new Bundle();
        try {
            args.putLong(RequestGetPersonAsyncTask.PARAM_USER_ID, Long.parseLong(userID));
        } catch (NumberFormatException e) {
            Log.e(TAG, "ERROR", e);
            throw new SocialNetworkException("userID should be long number");
        }

        executeRequest(new RequestGetPersonAsyncTask(), args, REQUEST_GET_PERSON);
    }

    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostMessage(message, onPostingCompleteListener);

        Bundle args = new Bundle();
        args.putString(RequestUpdateStatusAsyncTask.PARAM_MESSAGE, message);

        executeRequest(new RequestUpdateStatusAsyncTask(), args, REQUEST_POST_MESSAGE);
    }

    @Override
    public void requestPostPhoto(File photo, String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostPhoto(photo, message, onPostingCompleteListener);

        Bundle args = new Bundle();
        args.putString(RequestUpdateStatusAsyncTask.PARAM_MESSAGE, message);
        args.putString(RequestUpdateStatusAsyncTask.PARAM_PHOTO_PATH, photo.getAbsolutePath());

        executeRequest(new RequestUpdateStatusAsyncTask(), args, REQUEST_POST_PHOTO);
    }

    @Override
    public void requestCheckIsFriend(String userID, OnCheckIsFriendCompleteListener onCheckIsFriendCompleteListener) {
        super.requestCheckIsFriend(userID, onCheckIsFriendCompleteListener);

        Bundle args = new Bundle();
        try {
            args.putLong(RequestCheckIsFriendAsyncTask.PARAM_USER_ID, Long.parseLong(userID));
        } catch (NumberFormatException e) {
            Log.e(TAG, "ERROR", e);
            throw new SocialNetworkException("userID should be long number");
        }

        executeRequest(new RequestCheckIsFriendAsyncTask(), args, REQUEST_CHECK_IS_FRIEND);
    }

    @Override
    public void requestAddFriend(String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        super.requestAddFriend(userID, onRequestAddFriendCompleteListener);

        Bundle args = new Bundle();
        try {
            args.putLong(RequestAddFriendAsyncTask.PARAM_USER_ID, Long.parseLong(userID));
        } catch (NumberFormatException e) {
            Log.e(TAG, "ERROR", e);
            throw new SocialNetworkException("userID should be long number");
        }

        executeRequest(new RequestAddFriendAsyncTask(), args, REQUEST_ADD_FRIEND);
    }

    @Override
    public void requestRemoveFriend(String userID, OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener) {
        super.requestRemoveFriend(userID, onRequestRemoveFriendCompleteListener);

        Bundle args = new Bundle();
        try {
            args.putLong(RequestRemoveFriendAsyncTask.PARAM_USER_ID, Long.parseLong(userID));
        } catch (NumberFormatException e) {
            Log.e(TAG, "ERROR", e);
            throw new SocialNetworkException("userID should be long number");
        }

        executeRequest(new RequestRemoveFriendAsyncTask(), args, REQUEST_REMOVE_FRIEND);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_AUTH) return;

        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data != null ? data.getData() : null;

        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
            String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

            RequestLogin2AsyncTask requestLogin2AsyncTask = new RequestLogin2AsyncTask();
            mRequests.put(REQUEST_LOGIN2, requestLogin2AsyncTask);
            Bundle args = new Bundle();
            args.putString(RequestLogin2AsyncTask.PARAM_VERIFIER, verifier);
            requestLogin2AsyncTask.execute(args);
        } else {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, "incorrect URI returned: " + uri, null);
                mLocalListeners.remove(REQUEST_LOGIN);
            }

            /*
            *
            * No authentication challenges found
            * Relevant discussions can be found on the Internet at:
            * http://www.google.co.jp/search?q=8e063946 or
            * http://www.google.co.jp/search?q=ef59cf9f
            *
            * */
            initTwitterClient();
        }
    }

    @Override
    public void cancelLoginRequest() {
        super.cancelLoginRequest();

        initTwitterClient();
    }

    private class RequestLoginAsyncTask extends SocialNetworkAsyncTask {
        private static final String RESULT_OAUTH_LOGIN = "LoginAsyncTask.RESULT_OAUTH_LOGIN";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle result = new Bundle();

            try {
                mRequestToken = mTwitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                Uri oauthLoginURL = Uri.parse(mRequestToken.getAuthenticationURL() + "&force_login=true");

                result.putString(RESULT_OAUTH_LOGIN, oauthLoginURL.toString());
            } catch (TwitterException e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage() == null ? "canceled" : e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_LOGIN)) return;

            if (result.containsKey(RESULT_OAUTH_LOGIN)) {
                Intent intent = new Intent(mSocialNetworkManager.getActivity(), OAuthActivity.class)
                        .putExtra(OAuthActivity.PARAM_CALLBACK, TWITTER_CALLBACK_URL)
                        .putExtra(OAuthActivity.PARAM_URL_TO_LOAD, result.getString(RESULT_OAUTH_LOGIN));

                mSocialNetworkManager.startActivityForResult(intent, REQUEST_AUTH);
            }
        }
    }

    private class RequestLogin2AsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_VERIFIER = "Login2AsyncTask.PARAM_VERIFIER";

        private static final String RESULT_TOKEN = "Login2AsyncTask.RESULT_TOKEN";
        private static final String RESULT_SECRET = "Login2AsyncTask.RESULT_SECRET";
        private static final String RESULT_USER_ID = "Login2AsyncTask.RESULT_USER_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String verifier = params[0].getString(PARAM_VERIFIER);

            Bundle result = new Bundle();

            try {
                AccessToken accessToken = mTwitter.getOAuthAccessToken(mRequestToken, verifier);

                result.putString(RESULT_TOKEN, accessToken.getToken());
                result.putString(RESULT_SECRET, accessToken.getTokenSecret());
                result.putLong(RESULT_USER_ID, accessToken.getUserId());
            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage() == null ? "canceled" : e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            mRequests.remove(REQUEST_LOGIN2);
            if (!handleRequestResult(result, REQUEST_LOGIN)) {
                initTwitterClient();
                return;
            }

            // Shared Preferences
            mSharedPreferences.edit()
                    .putString(SAVE_STATE_KEY_OAUTH_TOKEN, result.getString(RESULT_TOKEN))
                    .putString(SAVE_STATE_KEY_OAUTH_SECRET, result.getString(RESULT_SECRET))
                    .putLong(SAVE_STATE_KEY_USER_ID, result.getLong(RESULT_USER_ID))
                    .apply();

            initTwitterClient();

            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
            }

            mLocalListeners.remove(REQUEST_LOGIN);
        }
    }

    private class RequestGetPersonAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "RequestGetPersonAsyncTask.PARAM_USER_ID";

        private static final String RESULT_IS_CURRENT_PERSON = "RequestPersonAsyncTask.RESULT_IS_CURRENT_PERSON";
        private static final String RESULT_ID = "RequestPersonAsyncTask.RESULT_ID";
        private static final String RESULT_NAME = "RequestPersonAsyncTask.RESULT_NAME";
        private static final String RESULT_AVATAR_URL = "RequestPersonAsyncTask.RESULT_AVATAR_URL";
        private static final String RESULT_PROFILE_URL = "RequestPersonAsyncTask.RESULT_PROFILE_URL";
        private static final String RESULT_NICKNAME = "RequestPersonAsyncTask.RESULT_NICKNAME";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle();
            Long userID;

            if (args.containsKey(PARAM_USER_ID)) {
                userID = args.getLong(PARAM_USER_ID);
                result.putBoolean(RESULT_IS_CURRENT_PERSON, false);
            } else {
                userID = mSharedPreferences.getLong(SAVE_STATE_KEY_USER_ID, -1);
                result.putBoolean(RESULT_IS_CURRENT_PERSON, true);
            }

            try {
                User user = mTwitter.showUser(userID);

                result.putString(RESULT_ID, user.getId() + "");
                result.putString(RESULT_NAME, user.getName());
                result.putString(RESULT_AVATAR_URL, user.getBiggerProfileImageURL());
                result.putString(RESULT_PROFILE_URL, "https://twitter.com/" + user.getScreenName());
                result.putString(RESULT_NICKNAME, user.getScreenName());
            } catch (TwitterException e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (result.getBoolean(RESULT_IS_CURRENT_PERSON)) {
                if (!handleRequestResult(result, REQUEST_GET_CURRENT_PERSON)) return;

                SocialPerson socialPerson = initSocialPerson(result);

                ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_CURRENT_PERSON))
                        .onRequestSocialPersonSuccess(getID(), socialPerson);

                mLocalListeners.remove(REQUEST_GET_CURRENT_PERSON);
            } else {
                if (!handleRequestResult(result, REQUEST_GET_PERSON)) return;

                SocialPerson socialPerson = initSocialPerson(result);

                ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_PERSON))
                        .onRequestSocialPersonSuccess(getID(), socialPerson);

                mLocalListeners.remove(REQUEST_GET_PERSON);
            }

        }

        private SocialPerson initSocialPerson(Bundle result) {
            SocialPerson socialPerson = new SocialPerson();
            socialPerson.id = result.getString(RESULT_ID);
            socialPerson.name = result.getString(RESULT_NAME);
            socialPerson.avatarURL = result.getString(RESULT_AVATAR_URL);
            socialPerson.profileURL = result.getString(RESULT_PROFILE_URL);
            socialPerson.nickname = result.getString(RESULT_NICKNAME);
            return socialPerson;
        }
    }


    private class RequestUpdateStatusAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_MESSAGE = "RequestUpdateStatusAsyncTask.PARAM_MESSAGE";
        public static final String PARAM_PHOTO_PATH = "RequestUpdateStatusAsyncTask.PARAM_PHOTO_PATH";

        private static final String RESULT_POST_PHOTO = "RequestUpdateStatusAsyncTask.RESULT_POST_PHOTO";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle();
            String paramMessage = "";
            String paramPhotoPath = null;

            if (args.containsKey(PARAM_MESSAGE)) {
                paramMessage = args.getString(PARAM_MESSAGE);
            }

            if (args.containsKey(PARAM_PHOTO_PATH)) {
                paramPhotoPath = args.getString(PARAM_PHOTO_PATH);

                result.putBoolean(RESULT_POST_PHOTO, true);
            } else {
                result.putBoolean(RESULT_POST_PHOTO, false);
            }

            try {
                StatusUpdate status = new StatusUpdate(paramMessage);

                if (paramPhotoPath != null) {
                    status.setMedia(new File(paramPhotoPath));
                }

                Log.d(TAG, "RequestUpdateStatusAsyncTask.mTwitter.updateStatus");
                mTwitter.updateStatus(status);
            } catch (TwitterException e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            String requestID = result.getBoolean(RESULT_POST_PHOTO) ? REQUEST_POST_PHOTO : REQUEST_POST_MESSAGE;

            mRequests.remove(requestID);

            String error = result.containsKey(RESULT_ERROR) ? result.getString(RESULT_ERROR) : null;

            if (mLocalListeners.get(requestID) != null) {
                if (error == null) {
                    ((OnPostingCompleteListener) mLocalListeners.get(requestID)).onPostSuccessfully(getID());
                } else {
                    mLocalListeners.get(requestID).onError(getID(), requestID, error, null);
                }
            }

            mLocalListeners.remove(requestID);
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "RequestUpdateStatusAsyncTask.onCancelled");
        }
    }

    private class RequestCheckIsFriendAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "PARAM_USER_ID";

        public static final String RESULT_IS_FRIEND = "RESULT_IS_FRIEND";
        public static final String RESULT_REQUESTED_ID = "RESULT_REQUESTED_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle();
            Long userID = args.getLong(PARAM_USER_ID);

            result.putLong(RESULT_REQUESTED_ID, userID);
            try {
                long currentUserID = mSharedPreferences.getLong(SAVE_STATE_KEY_USER_ID, -1);

                Relationship relationship = mTwitter.showFriendship(currentUserID, userID);
                result.putBoolean(RESULT_IS_FRIEND, relationship.isSourceFollowingTarget());
            } catch (TwitterException e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_CHECK_IS_FRIEND,
                    result.getLong(RESULT_REQUESTED_ID))) return;

            ((OnCheckIsFriendCompleteListener) mLocalListeners.get(REQUEST_CHECK_IS_FRIEND))
                    .onCheckIsFriendComplete(getID(),
                            "" + result.getLong(RESULT_REQUESTED_ID),
                            result.getBoolean(RESULT_IS_FRIEND)
                    );
        }
    }

    private class RequestAddFriendAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "PARAM_USER_ID";

        public static final String RESULT_REQUESTED_ID = "RESULT_REQUESTED_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle();
            Long userID = args.getLong(PARAM_USER_ID);

            result.putLong(RESULT_REQUESTED_ID, userID);
            try {
                mTwitter.createFriendship(userID);
            } catch (TwitterException e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_ADD_FRIEND,
                    result.getLong(RESULT_REQUESTED_ID))) return;

            ((OnRequestAddFriendCompleteListener) mLocalListeners.get(REQUEST_ADD_FRIEND))
                    .onRequestAddFriendComplete(getID(),
                            "" + result.getLong(RESULT_REQUESTED_ID)
                    );
        }
    }

    private class RequestRemoveFriendAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "PARAM_USER_ID";

        public static final String RESULT_REQUESTED_ID = "RESULT_REQUESTED_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle();
            Long userID = args.getLong(PARAM_USER_ID);

            result.putLong(RESULT_REQUESTED_ID, userID);
            try {
                mTwitter.destroyFriendship(userID);
            } catch (TwitterException e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_REMOVE_FRIEND,
                    result.getLong(RESULT_REQUESTED_ID))) return;

            ((OnRequestRemoveFriendCompleteListener) mLocalListeners.get(REQUEST_REMOVE_FRIEND))
                    .onRequestRemoveFriendComplete(getID(),
                            "" + result.getLong(RESULT_REQUESTED_ID)
                    );
        }
    }

}
