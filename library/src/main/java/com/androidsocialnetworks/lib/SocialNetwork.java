package com.androidsocialnetworks.lib;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.androidsocialnetworks.lib.listener.OnCheckIsFriendCompleteListener;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnPostingCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestAddFriendCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestRemoveFriendCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class SocialNetwork {
    protected static final String REQUEST_LOGIN = "SocialNetwork.REQUEST_LOGIN";
    protected static final String REQUEST_GET_CURRENT_PERSON = "SocialNetwork.REQUEST_GET_CURRENT_PERSON";
    protected static final String REQUEST_GET_PERSON = "SocialNetwork.REQUEST_GET_PERSON";
    protected static final String REQUEST_POST = "SocialNetwork.REQUEST_POST";
    protected static final String REQUEST_CHECK_IS_FRIEND = "SocialNetwork.REQUEST_CHECK_IS_FRIEND";
    protected static final String REQUEST_ADD_FRIEND = "SocialNetwork.REQUEST_ADD_FRIEND";
    protected static final String REQUEST_REMOVE_FRIEND = "SocialNetwork.REQUEST_REMOVE_FRIEND";
    private static final String TAG = SocialNetwork.class.getSimpleName();
    private static final String SHARED_PREFERENCES_NAME = "social_networks";
    protected Fragment mSocialNetworkManager;
    protected SharedPreferences mSharedPreferences;
    protected Map<String, SocialNetworkListener> mGlobalListeners = new HashMap<String, SocialNetworkListener>();
    protected Map<String, SocialNetworkListener> mLocalListeners = new HashMap<String, SocialNetworkListener>();

    /**
     * @param fragment ant not activity or context, as we will need to call startActivityForResult,
     *                 we will want to receice on onActivityResult in out SocialNetworkManager
     *                 fragment
     */
    protected SocialNetwork(Fragment fragment) {
        mSocialNetworkManager = fragment;

        mSharedPreferences = mSocialNetworkManager
                .getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    //////////////////// LIFECYCLE ////////////////////

    public void onCreate(Bundle savedInstanceState) {

    }

    public void onStart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroy() {

    }

    public void onSaveInstanceState(Bundle outState) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    //////////////////// API ////////////////////

    public abstract boolean isConnected();

    public void requestLogin() {
        requestLogin(null);
    }

    public abstract void requestLogin(OnLoginCompleteListener onLoginCompleteListener);

    public abstract void logout();

    public abstract int getID();

    public void requestCurrentPerson() {
        requestCurrentPerson(null);
    }

    public abstract void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener);

    public void requestSocialPerson(String userID) {
        requestSocialPerson(userID, null);
    }

    public abstract void requestSocialPerson(String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener);

    public void requestPostMessage(String message) {
        requestPostMessage(message, null);
    }

    public abstract void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener);

    public void requestPostPhoto(File photo, String message) {
        requestPostPhoto(photo, message, null);
    }

    public abstract void requestPostPhoto(File photo, String message, OnPostingCompleteListener onPostingCompleteListener);

    public void requestCheckIsFriend(String userID) {
        requestCheckIsFriend(userID, null);
    }

    public abstract void requestCheckIsFriend(String userID, OnCheckIsFriendCompleteListener onCheckIsFriendCompleteListener);

    public void requestAddFriend(String userID) {
        requestAddFriend(userID, null);
    }

    public abstract void requestAddFriend(String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener);

    public void requestRemoveFriend(String userID) {
        requestRemoveFriend(userID, null);
    }

    public abstract void requestRemoveFriend(String userID, OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener);

    public void cancelLoginRequest() {

    }

    public void cancelGetPersonRequest() {

    }

    public void cancelPostMessageRequest() {

    }

    public void cancelPostPhotoRequest() {

    }

    public void cancenCheckIsFriendRequest() {

    }

    public void cancelAddFriendRequest() {

    }

    public void cancenRemoveFriendRequest() {

    }

    //////////////////// UTIL METHODS ////////////////////

    protected void checkRequestState(AsyncTask request) throws SocialNetworkException {
        if (request != null) {
            throw new SocialNetworkException("Request is already running");
        }
    }

}
