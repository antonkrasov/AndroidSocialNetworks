package com.androidsocialnetworks.lib.impl;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.androidsocialnetworks.lib.OAuthActivity;
import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkAsyncTask;
import com.androidsocialnetworks.lib.SocialPerson;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import static com.androidsocialnetworks.lib.Consts.TAG;

public class TwitterSocialNetwork extends SocialNetwork {
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

    private Map<String, SocialNetworkAsyncTask> mRequests = new HashMap<String, SocialNetworkAsyncTask>();

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
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);

        checkRequestState(mRequests.get(REQUEST_LOGIN));

        RequestLoginAsyncTask requestLoginAsyncTask = new RequestLoginAsyncTask();
        mRequests.put(REQUEST_LOGIN, requestLoginAsyncTask);
        requestLoginAsyncTask.execute();
    }

    @Override
    public void logout() {
        mSharedPreferences.edit()
                .remove(SAVE_STATE_KEY_OAUTH_TOKEN)
                .remove(SAVE_STATE_KEY_OAUTH_SECRET)
                .apply();

        mTwitter = null;
        initTwitterClient();
    }

    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);

        checkRequestState(mRequests.get(REQUEST_GET_CURRENT_PERSON));

        RequestGetPersonAsyncTask requestGetPersonAsyncTask = new RequestGetPersonAsyncTask();
        mRequests.put(REQUEST_GET_CURRENT_PERSON, requestGetPersonAsyncTask);
        requestGetPersonAsyncTask.execute(new Bundle());
    }

    //    @Override
//    public void requestPerson() throws SocialNetworkException {
//        checkRequestState(mRequestGetPersonAsyncTask);
//
//        mRequestGetPersonAsyncTask = new RequestGetPersonAsyncTask();
//        mRequestGetPersonAsyncTask.execute();
//    }
//
//
//    @Override
//    public void requestPostMessage(String message) throws SocialNetworkException {
//        checkRequestState(mRequestUpdateStatusAsyncTask);
//
//        mRequestUpdateStatusAsyncTask = new RequestUpdateStatusAsyncTask();
//        mRequestUpdateStatusAsyncTask.execute(message);
//    }
//
//    @Override
//    public void requestPostPhoto(File photo, String message) throws SocialNetworkException {
//        checkRequestState(mRequestUpdateStatusAsyncTask);
//
//        mRequestUpdateStatusAsyncTask = new RequestUpdateStatusAsyncTask();
//        mRequestUpdateStatusAsyncTask.execute(message, photo.getAbsolutePath());
//    }
//
//    @Override
//    public void requestCheckIsFriend(String userID) throws SocialNetworkException {
//        checkRequestState(mRequestCheckIsFriendAsyncTask);
//
//        mRequestCheckIsFriendAsyncTask = new RequestCheckIsFriendAsyncTask();
//        mRequestCheckIsFriendAsyncTask.execute(userID);
//    }
//
//    @Override
//    public void requestAddFriend(String userID) throws SocialNetworkException {
//        checkRequestState(mRequestAddFriendAsyncTask);
//
//        mRequestAddFriendAsyncTask = new RequestAddFriendAsyncTask();
//        mRequestAddFriendAsyncTask.execute(userID);
//    }
//
//    @Override
//    public void requestRemoveFriend(String userID) throws SocialNetworkException {
//        checkRequestState(mRequestRemoveFriendAsyncTask);
//
//        mRequestRemoveFriendAsyncTask = new RequestRemoveFriendAsyncTask();
//        mRequestRemoveFriendAsyncTask.execute(userID);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        stopAllRequests();
//    }
//
//    private void stopAllRequests() {
//        Log.d(TAG, "stopAllRequests()");
//        final boolean mayInterruptIfRunning = true;
//
//        if (mRequestLoginAsyncTask != null) {
//            mRequestLoginAsyncTask.cancel(mayInterruptIfRunning);
//            mRequestLoginAsyncTask = null;
//        }
//        if (mRequestLogin2AsyncTask != null) {
//            mRequestLogin2AsyncTask.cancel(mayInterruptIfRunning);
//            mRequestLogin2AsyncTask = null;
//        }
//        if (mRequestGetPersonAsyncTask != null) {
//            mRequestGetPersonAsyncTask.cancel(mayInterruptIfRunning);
//            mRequestGetPersonAsyncTask = null;
//        }
//        if (mRequestUpdateStatusAsyncTask != null) {
//            mRequestUpdateStatusAsyncTask.cancel(mayInterruptIfRunning);
//            mRequestUpdateStatusAsyncTask = null;
//        }
//        if (mRequestCheckIsFriendAsyncTask != null) {
//            mRequestCheckIsFriendAsyncTask.cancel(mayInterruptIfRunning);
//            mRequestCheckIsFriendAsyncTask = null;
//        }
//        if (mRequestAddFriendAsyncTask != null) {
//            mRequestAddFriendAsyncTask.cancel(mayInterruptIfRunning);
//            mRequestAddFriendAsyncTask = null;
//        }
//        if (mRequestRemoveFriendAsyncTask != null) {
//            mRequestRemoveFriendAsyncTask.cancel(mayInterruptIfRunning);
//            mRequestRemoveFriendAsyncTask = null;
//        }
//    }

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

        SocialNetworkAsyncTask loginRequest = mRequests.get(REQUEST_LOGIN);
        SocialNetworkAsyncTask login2Request = mRequests.get(REQUEST_LOGIN2);

        if (loginRequest != null) {
            loginRequest.cancel(true);
        }

        if (login2Request != null) {
            login2Request.cancel(true);
        }

        mRequests.remove(REQUEST_LOGIN);
        mRequests.remove(REQUEST_LOGIN2);

        initTwitterClient();
    }

    //    @Override
//    public void cancelLoginRequest() {
//        if (mRequestLoginAsyncTask != null) {
//            mRequestLoginAsyncTask.cancel(true);
//            mRequestLoginAsyncTask = null;
//        }
//
//        if (mRequestLogin2AsyncTask != null) {
//            mRequestLogin2AsyncTask.cancel(true);
//            mRequestLogin2AsyncTask = null;
//        }
//    }
//
//    @Override
//    public void cancelGetPersonRequest() {
//        if (mRequestGetPersonAsyncTask != null) {
//            mRequestGetPersonAsyncTask.cancel(true);
//            mRequestGetPersonAsyncTask = null;
//        }
//    }
//
//    @Override
//    public void cancelPostMessageRequest() {
//        if (mRequestUpdateStatusAsyncTask != null) {
//            mRequestUpdateStatusAsyncTask.cancel(true);
//            mRequestUpdateStatusAsyncTask = null;
//        }
//    }
//
//    @Override
//    public void cancelPostPhotoRequest() {
//        if (mRequestUpdateStatusAsyncTask != null) {
//            mRequestUpdateStatusAsyncTask.cancel(true);
//            mRequestUpdateStatusAsyncTask = null;
//        }
//    }
//
//    @Override
//    public void cancenCheckIsFriendRequest() {
//        if (mRequestCheckIsFriendAsyncTask != null) {
//            mRequestCheckIsFriendAsyncTask.cancel(true);
//            mRequestCheckIsFriendAsyncTask = null;
//        }
//    }
//
//    @Override
//    public void cancelAddFriendRequest() {
//        if (mRequestAddFriendAsyncTask != null) {
//            mRequestAddFriendAsyncTask.cancel(true);
//            mRequestAddFriendAsyncTask = null;
//        }
//    }
//
//    @Override
//    public void cancenRemoveFriendRequest() {
//        if (mRequestRemoveFriendAsyncTask != null) {
//            mRequestRemoveFriendAsyncTask.cancel(true);
//            mRequestRemoveFriendAsyncTask = null;
//        }
//    }

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
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            Log.d(TAG, "LoginAsyncTask.onPostExecute()");

            mRequests.put(REQUEST_LOGIN, null);

            if (result.containsKey(RESULT_ERROR) && mLocalListeners.get(REQUEST_LOGIN) != null) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, result.getString(RESULT_ERROR), null);
                mLocalListeners.remove(REQUEST_LOGIN);
            }

            // 1: user didn't set login listener, or pass null, this doesn't have any sence
            // 2: request was canceled...
            if (mLocalListeners.get(REQUEST_LOGIN) == null) {
                Log.e(TAG, "RequestLoginAsyncTask.onPostExecute: mLocalListeners.get(REQUEST_LOGIN) == null");
                return;
            }

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

        private static final String RESULT_ERROR = "Login2AsyncTask.RESULT_ERROR";
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
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            mRequests.put(REQUEST_LOGIN2, null);

            String error = result.containsKey(RESULT_ERROR) ? result.getString(RESULT_ERROR) : null;

            if (error == null) {
                // Shared Preferences
                mSharedPreferences.edit()
                        .putString(SAVE_STATE_KEY_OAUTH_TOKEN, result.getString(RESULT_TOKEN))
                        .putString(SAVE_STATE_KEY_OAUTH_SECRET, result.getString(RESULT_SECRET))
                        .putLong(SAVE_STATE_KEY_USER_ID, result.getLong(RESULT_USER_ID))
                        .apply();

                initTwitterClient();
            }

            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                if (error == null) {
                    ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());

                } else {
                    mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, error, null);
                }
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
            } catch (TwitterException e) {
                Log.e(TAG, "ERROR", e);
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            String error = result.containsKey(RESULT_ERROR) ? result.getString(RESULT_ERROR) : null;

            if (result.getBoolean(RESULT_IS_CURRENT_PERSON)) {
                mRequests.remove(REQUEST_GET_CURRENT_PERSON);

                if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                    if (error == null) {
                        SocialPerson socialPerson = new SocialPerson();
                        socialPerson.id = result.getString(RESULT_ID);
                        socialPerson.name = result.getString(RESULT_NAME);
                        socialPerson.avatarURL = result.getString(RESULT_AVATAR_URL);

                        ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_CURRENT_PERSON))
                                .onRequestSocialPersonSuccess(getID(), socialPerson);
                    } else {
                        mLocalListeners.get(REQUEST_GET_CURRENT_PERSON).onError(getID(), REQUEST_GET_CURRENT_PERSON, error, null);
                    }
                }

            } else {
                mRequests.remove(REQUEST_GET_PERSON);

                if (mLocalListeners.get(REQUEST_GET_PERSON) != null) {
                    if (error == null) {
                        SocialPerson socialPerson = new SocialPerson();
                        socialPerson.id = result.getString(RESULT_ID);
                        socialPerson.name = result.getString(RESULT_NAME);
                        socialPerson.avatarURL = result.getString(RESULT_AVATAR_URL);

                        ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_PERSON))
                                .onRequestSocialPersonSuccess(getID(), socialPerson);
                    } else {
                        mLocalListeners.get(REQUEST_GET_PERSON).onError(getID(), REQUEST_GET_PERSON, error, null);
                    }
                }
            }

        }
    }

//    private class RequestUpdateStatusAsyncTask extends AsyncTask<String, Void, Bundle> {
//        private static final String RESULT_ERROR = "RequestUpdateStatus.RESULT_ERROR";
//
//        @Override
//        protected Bundle doInBackground(String... params) {
//            Log.d(TAG, "RequestUpdateStatus.doInBackground");
//
//            Bundle result = new Bundle();
//
//            try {
//                StatusUpdate status = new StatusUpdate(params[0]);
//
//                if (params.length == 2) {
//                    status.setMedia(new File(params[1]));
//                }
//
//                Log.d(TAG, "RequestUpdateStatus.updateStatus start");
//                mTwitter.updateStatus(status);
//                Log.d(TAG, "RequestUpdateStatus.updateStatus complete");
//            } catch (TwitterException e) {
//                Log.e(TAG, "ERROR", e);
//                result.putString(RESULT_ERROR, e.getMessage());
//            }
//
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Bundle bundle) {
//            Log.d(TAG, "RequestUpdateStatus.onPostExecute");
//            mRequestUpdateStatusAsyncTask = null;
//
//            if (bundle.containsKey(RESULT_ERROR)) {
//                if (mOnPostingListener != null) {
//                    mOnPostingListener.onPostFailed(getID(), bundle.getString(RESULT_ERROR));
//                }
//
//                return;
//            }
//
//            if (mOnPostingListener != null) {
//                mOnPostingListener.onPostSuccessfully(getID());
//            }
//        }
//    }
//
//    private class RequestCheckIsFriendAsyncTask extends AsyncTask<String, Void, Bundle> {
//        private static final String RESULT_ERROR = "RequestCheckIsFriendAsyncTask.RESULT_ERROR";
//        private static final String RESULT_REQUESTED_ID = "RequestCheckIsFriendAsyncTask.RESULT_REQUESTED_ID";
//        private static final String RESULT_IS_FRIEND = "RequestCheckIsFriendAsyncTask.RESULT_IS_FRIEND";
//
//        @Override
//        protected Bundle doInBackground(String... params) {
//            Bundle result = new Bundle();
//
//            final String requestedID = params[0];
//            result.putString(RESULT_REQUESTED_ID, requestedID);
//
//            try {
//                long currentUserID = mSharedPreferences.getLong(SAVE_STATE_KEY_USER_ID, -1);
//                long userID = Long.valueOf(requestedID);
//
//                Relationship relationship = mTwitter.showFriendship(currentUserID, userID);
//                result.putBoolean(RESULT_IS_FRIEND, relationship.isSourceFollowingTarget());
//            } catch (Exception e) {
//                Log.e(TAG, "ERROR", e);
//                result.putString(RESULT_ERROR, e.getMessage());
//            }
//
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Bundle bundle) {
//            mRequestCheckIsFriendAsyncTask = null;
//
//            String error = bundle.containsKey(RESULT_ERROR) ? bundle.getString(RESULT_ERROR) : null;
//
//            if (mOnCheckingIsFriendListener != null) {
//                if (error != null) {
//                    mOnCheckingIsFriendListener.onCheckIsFriendFailed(getID(), bundle.getString(RESULT_REQUESTED_ID), error);
//                } else {
//                    mOnCheckingIsFriendListener.onCheckIsFriendSuccess(
//                            getID(),
//                            bundle.getString(RESULT_REQUESTED_ID),
//                            bundle.getBoolean(RESULT_IS_FRIEND)
//                    );
//                }
//            }
//        }
//    }
//
//    private class RequestAddFriendAsyncTask extends AsyncTask<String, Void, Bundle> {
//        private static final String RESULT_ERROR = "RequestAddFriendAsyncTask.RESULT_ERROR";
//        private static final String RESULT_REQUESTED_ID = "RequestAddFriendAsyncTask.RESULT_REQUESTED_ID";
//
//        @Override
//        protected Bundle doInBackground(String... params) {
//            Bundle result = new Bundle();
//
//            final String requestedID = params[0];
//            result.putString(RESULT_REQUESTED_ID, requestedID);
//
//            try {
//                long id = Long.valueOf(requestedID);
//                mTwitter.createFriendship(id);
//            } catch (Exception e) {
//                Log.e(TAG, "ERROR", e);
//                result.putString(RESULT_ERROR, e.getMessage());
//            }
//
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Bundle bundle) {
//            mRequestAddFriendAsyncTask = null;
//
//            String error = bundle.containsKey(RESULT_ERROR) ? bundle.getString(RESULT_ERROR) : null;
//
//            if (mOnAddFriendListener != null) {
//                if (error != null) {
//                    mOnAddFriendListener.onAddFriendFailed(getID(), bundle.getString(RESULT_REQUESTED_ID), error);
//                } else {
//                    mOnAddFriendListener.onAddFriendSuccess(
//                            getID(),
//                            bundle.getString(RESULT_REQUESTED_ID)
//                    );
//                }
//            }
//        }
//    }
//
//    private class RequestRemoveFriendAsyncTask extends AsyncTask<String, Void, Bundle> {
//        private static final String RESULT_ERROR = "RequestRemoveFriendAsyncTask.RESULT_ERROR";
//        private static final String RESULT_REQUESTED_ID = "RequestRemoveFriendAsyncTask.RESULT_REQUESTED_ID";
//
//        @Override
//        protected Bundle doInBackground(String... params) {
//            Bundle result = new Bundle();
//
//            final String requestedID = params[0];
//            result.putString(RESULT_REQUESTED_ID, requestedID);
//
//            try {
//                long id = Long.valueOf(requestedID);
//                mTwitter.destroyFriendship(id);
//            } catch (Exception e) {
//                Log.e(TAG, "ERROR", e);
//                result.putString(RESULT_ERROR, e.getMessage());
//            }
//
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Bundle bundle) {
//            mRequestRemoveFriendAsyncTask = null;
//
//            String error = bundle.containsKey(RESULT_ERROR) ? bundle.getString(RESULT_ERROR) : null;
//
//            if (mOnRemoveFriendListener != null) {
//                if (error != null) {
//                    mOnRemoveFriendListener.onRemoveFriendFailed(getID(), bundle.getString(RESULT_REQUESTED_ID), error);
//                } else {
//                    mOnRemoveFriendListener.onRemoveFriendSuccess(
//                            getID(),
//                            bundle.getString(RESULT_REQUESTED_ID)
//                    );
//                }
//            }
//        }
//    }
}
