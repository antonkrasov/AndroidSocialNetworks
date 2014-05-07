package com.androidsocialnetworks.lib.impl;

import android.support.v4.app.Fragment;

import com.androidsocialnetworks.lib.SocialNetwork;

public class TwitterSocialNetwork extends SocialNetwork {

    public TwitterSocialNetwork(Fragment fragment) {
        super(fragment);
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void logout() {

    }

    @Override
    public int getID() {
        return 0;
    }

    //    public static final int ID = 1;
//
//    private static final String TAG = TwitterSocialNetwork.class.getSimpleName();
//    private static final String SAVE_STATE_KEY_OAUTH_TOKEN = "TwitterSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
//    private static final String SAVE_STATE_KEY_OAUTH_SECRET = "TwitterSocialNetwork.SAVE_STATE_KEY_OAUTH_SECRET";
//    private static final String SAVE_STATE_KEY_USER_ID = "TwitterSocialNetwork.SAVE_STATE_KEY_USER_ID";
//
//    private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
//
//    // max 16 bit to use in startActivityForResult
//    private static final int REQUEST_AUTH = UUID.randomUUID().hashCode() & 0xFFFF;
//
//    private final String TWITTER_CALLBACK_URL = "oauth://AndroidSocialNetworks";
//    private final String fConsumerKey;
//    private final String fConsumerSecret;
//
//    private Twitter mTwitter;
//    private RequestToken mRequestToken;
//
//    private RequestLoginAsyncTask mRequestLoginAsyncTask;
//    private RequestLogin2AsyncTask mRequestLogin2AsyncTask;
//    private RequestGetPersonAsyncTask mRequestGetPersonAsyncTask;
//    private RequestUpdateStatusAsyncTask mRequestUpdateStatusAsyncTask;
//    private RequestCheckIsFriendAsyncTask mRequestCheckIsFriendAsyncTask;
//    private RequestAddFriendAsyncTask mRequestAddFriendAsyncTask;
//    private RequestRemoveFriendAsyncTask mRequestRemoveFriendAsyncTask;
//
//    public TwitterSocialNetwork(Fragment fragment, String consumerKey, String consumerSecret) {
//        super(fragment);
//        Log.d(TAG, "new TwitterSocialNetwork: " + consumerKey + " : " + consumerSecret);
//
//        fConsumerKey = consumerKey;
//        fConsumerSecret = consumerSecret;
//
//        if (TextUtils.isEmpty(fConsumerKey) || TextUtils.isEmpty(fConsumerSecret)) {
//            throw new IllegalArgumentException("consumerKey and consumerSecret are invalid");
//        }
//
//        initTwitterClient();
//    }
//
//    private void initTwitterClient() {
//        ConfigurationBuilder builder = new ConfigurationBuilder();
//        builder.setOAuthConsumerKey(fConsumerKey);
//        builder.setOAuthConsumerSecret(fConsumerSecret);
//
//        String accessToken = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
//        String accessTokenSecret = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null);
//
//        TwitterFactory factory = new TwitterFactory(builder.build());
//
//        if (TextUtils.isEmpty(accessToken) && TextUtils.isEmpty(accessTokenSecret)) {
//            mTwitter = factory.getInstance();
//        } else {
//            mTwitter = factory.getInstance(new AccessToken(accessToken, accessTokenSecret));
//        }
//    }
//
//    @Override
//    public int getID() {
//        return ID;
//    }
//
//    @Override
//    public boolean isConnected() {
//        String accessToken = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
//        String accessTokenSecret = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null);
//        return accessToken != null && accessTokenSecret != null;
//    }
//
//    @Override
//    public void requestLogin() throws SocialNetworkException {
//        if (isConnected()) {
//            if (mOnLoginCompleteListener != null) {
//                mOnLoginCompleteListener.onLoginSuccess(getID());
//            }
//
//            return;
//        }
//
//        checkRequestState(mRequestLoginAsyncTask);
//
//        mRequestLoginAsyncTask = new RequestLoginAsyncTask();
//        mRequestLoginAsyncTask.execute();
//    }
//
//    @Override
//    public void logout() {
//        mSharedPreferences.edit()
//                .remove(SAVE_STATE_KEY_OAUTH_TOKEN)
//                .remove(SAVE_STATE_KEY_OAUTH_SECRET)
//                .apply();
//
//        mTwitter = null;
//        initTwitterClient();
//    }
//
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
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode != REQUEST_AUTH) return;
//
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: " + requestCode + " : " + resultCode + " : " + data);
//
//        Uri uri = data != null ? data.getData() : null;
//
//        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
//            String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
//
//            mRequestLogin2AsyncTask = new RequestLogin2AsyncTask();
//            mRequestLogin2AsyncTask.execute(verifier);
//        } else {
//            if (mOnLoginCompleteListener != null) {
//                mOnLoginCompleteListener.onLoginFailed(getID(), "incorrect URI returned: " + uri);
//            }
//        }
//    }
//
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
//
//    private class RequestLoginAsyncTask extends AsyncTask<String, String, Bundle> {
//        private static final String RESULT_ERROR = "LoginAsyncTask.RESULT_ERROR";
//        private static final String RESULT_OAUTH_LOGIN = "LoginAsyncTask.RESULT_OAUTH_LOGIN";
//
//        @Override
//        protected void onPreExecute() {
//            Log.d(TAG, "LoginAsyncTask.onPreExecute()");
//        }
//
//        @Override
//        protected Bundle doInBackground(String... params) {
//            Bundle result = new Bundle();
//
//            Log.d(TAG, "LoginAsyncTask.doInBackground()");
//
//            try {
//                mRequestToken = mTwitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
//                Uri oauthLoginURL = Uri.parse(mRequestToken.getAuthenticationURL() + "&force_login=true");
//
//                Log.d(TAG, "oauthLoginURL: " + oauthLoginURL);
//
//                result.putString(RESULT_OAUTH_LOGIN, oauthLoginURL.toString());
//            } catch (TwitterException e) {
//                Log.e(TAG, "ERROR", e);
//                result.putString(RESULT_ERROR, e.getMessage());
//            }
//
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Bundle result) {
//            Log.d(TAG, "LoginAsyncTask.onPostExecute()");
//
//            mRequestLoginAsyncTask = null;
//
//            if (result.containsKey(RESULT_ERROR) && mOnLoginCompleteListener != null) {
//                mOnLoginCompleteListener.onLoginFailed(getID(), result.getString(RESULT_ERROR));
//            }
//
//            if (result.containsKey(RESULT_OAUTH_LOGIN)) {
//                Intent intent = new Intent(mSocialNetworkManager.getActivity(), OAuthActivity.class)
//                        .putExtra(OAuthActivity.PARAM_CALLBACK, TWITTER_CALLBACK_URL)
//                        .putExtra(OAuthActivity.PARAM_URL_TO_LOAD, result.getString(RESULT_OAUTH_LOGIN));
//
//                mSocialNetworkManager.startActivityForResult(intent, REQUEST_AUTH);
//            }
//        }
//    }
//
//    private class RequestLogin2AsyncTask extends AsyncTask<String, Void, Bundle> {
//        private static final String RESULT_ERROR = "Login2AsyncTask.RESULT_ERROR";
//        private static final String RESULT_TOKEN = "Login2AsyncTask.RESULT_TOKEN";
//        private static final String RESULT_SECRET = "Login2AsyncTask.RESULT_SECRET";
//        private static final String RESULT_USER_ID = "Login2AsyncTask.RESULT_USER_ID";
//
//        @Override
//        protected Bundle doInBackground(String... params) {
//            String verifier = params[0];
//
//            Bundle result = new Bundle();
//
//            try {
//                AccessToken accessToken = mTwitter.getOAuthAccessToken(mRequestToken, verifier);
//
//                result.putString(RESULT_TOKEN, accessToken.getToken());
//                result.putString(RESULT_SECRET, accessToken.getTokenSecret());
//                result.putLong(RESULT_USER_ID, accessToken.getUserId());
//            } catch (Exception e) {
//                Log.e(TAG, "ERROR", e);
//                result.putString(RESULT_ERROR, e.getMessage());
//            }
//
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Bundle result) {
//            mRequestLogin2AsyncTask = null;
//
//            String error = result.containsKey(RESULT_ERROR) ? result.getString(RESULT_ERROR) : null;
//
//            if (error == null) {
//                // Shared Preferences
//                mSharedPreferences.edit()
//                        .putString(SAVE_STATE_KEY_OAUTH_TOKEN, result.getString(RESULT_TOKEN))
//                        .putString(SAVE_STATE_KEY_OAUTH_SECRET, result.getString(RESULT_SECRET))
//                        .putLong(SAVE_STATE_KEY_USER_ID, result.getLong(RESULT_USER_ID))
//                        .apply();
//
//                initTwitterClient();
//            }
//
//            if (mOnLoginCompleteListener != null) {
//                if (error == null) {
//                    mOnLoginCompleteListener.onLoginSuccess(getID());
//                } else {
//                    mOnLoginCompleteListener.onLoginFailed(getID(), error);
//                }
//            }
//        }
//    }
//
//    private class RequestGetPersonAsyncTask extends AsyncTask<Long, Void, Bundle> {
//        private static final String RESULT_ERROR = "RequestPersonAsyncTask.RESULT_ERROR";
//        private static final String RESULT_ID = "RequestPersonAsyncTask.RESULT_ID";
//        private static final String RESULT_NAME = "RequestPersonAsyncTask.RESULT_NAME";
//        private static final String RESULT_AVATAR_URL = "RequestPersonAsyncTask.RESULT_AVATAR_URL";
//
//        @Override
//        protected Bundle doInBackground(Long... params) {
//            Bundle result = new Bundle();
//
//            try {
//                long currentUserID = mSharedPreferences.getLong(SAVE_STATE_KEY_USER_ID, -1);
//                User user = mTwitter.showUser(currentUserID);
//
//                result.putString(RESULT_ID, user.getId() + "");
//                result.putString(RESULT_NAME, user.getName());
//                result.putString(RESULT_AVATAR_URL, user.getBiggerProfileImageURL());
//            } catch (TwitterException e) {
//                Log.e(TAG, "ERROR", e);
//                result.putString(RESULT_ERROR, e.getMessage());
//            }
//
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Bundle result) {
//            mRequestGetPersonAsyncTask = null;
//
//            String error = result.containsKey(RESULT_ERROR) ? result.getString(RESULT_ERROR) : null;
//
//            if (mOnRequestSocialPersonListener != null) {
//                if (error == null) {
//                    SocialPerson socialPerson = new SocialPerson();
//                    socialPerson.id = result.getString(RESULT_ID);
//                    socialPerson.name = result.getString(RESULT_NAME);
//                    socialPerson.avatarURL = result.getString(RESULT_AVATAR_URL);
//
//                    mOnRequestSocialPersonListener.onRequestSocialPersonSuccess(getID(), socialPerson);
//                } else {
//                    mOnRequestSocialPersonListener.onRequestSocialPersonFailed(getID(), error);
//                }
//            }
//        }
//    }
//
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
