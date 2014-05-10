package com.androidsocialnetworks.lib;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;

import java.util.HashMap;
import java.util.Map;

import static com.androidsocialnetworks.lib.Consts.TAG;

public abstract class OAuthSocialNetwork extends SocialNetwork {

    protected Map<String, SocialNetworkAsyncTask> mRequests = new HashMap<String, SocialNetworkAsyncTask>();

    protected OAuthSocialNetwork(Fragment fragment) {
        super(fragment);
    }

    protected void executeRequest(SocialNetworkAsyncTask request, Bundle params, String requestID) {
        checkRequestState(mRequests.get(requestID));

        mRequests.put(requestID, request);
        request.execute(params == null ? new Bundle() : params);
    }

    private void cancelRequest(String requestID) {
        Log.d(TAG, "TwitterSocialNetwork.cancelRequest: " + requestID);

        SocialNetworkAsyncTask request = mRequests.get(requestID);

        if (request != null) {
            request.cancel(true);
        }

        mRequests.remove(requestID);
    }

    @Override
    public void cancelLoginRequest() {
        super.cancelLoginRequest();

        cancelRequest(REQUEST_LOGIN);
        cancelRequest(REQUEST_LOGIN2);
    }

    @Override
    public void cancelGetCurrentSocialPersonRequest() {
        super.cancelGetCurrentSocialPersonRequest();

        cancelRequest(REQUEST_GET_CURRENT_PERSON);
    }

    @Override
    public void cancelGetSocialPersonRequest() {
        super.cancelGetSocialPersonRequest();

        cancelRequest(REQUEST_GET_PERSON);
    }

    @Override
    public void cancelPostMessageRequest() {
        super.cancelPostMessageRequest();

        cancelRequest(REQUEST_POST_MESSAGE);
    }

    @Override
    public void cancelPostPhotoRequest() {
        super.cancelPostPhotoRequest();

        cancelRequest(REQUEST_POST_PHOTO);
    }

    @Override
    public void cancelCheckIsFriendRequest() {
        super.cancelCheckIsFriendRequest();

        cancelRequest(REQUEST_CHECK_IS_FRIEND);
    }

    @Override
    public void cancelAddFriendRequest() {
        super.cancelAddFriendRequest();

        cancelRequest(REQUEST_ADD_FRIEND);
    }

    @Override
    public void cancelRemoveFriendRequest() {
        super.cancelRemoveFriendRequest();

        cancelRequest(REQUEST_REMOVE_FRIEND);
    }

    protected boolean handleRequestResult(Bundle result, String requestID) {
        return handleRequestResult(result, requestID, null);
    }

    protected boolean handleRequestResult(Bundle result, String requestID, Object data) {
        Log.d(TAG, this + "handleRequestResult: " + result + " : " + requestID);

        mRequests.remove(requestID);

        SocialNetworkListener socialNetworkListener = mLocalListeners.get(requestID);

        // 1: user didn't set listener, or pass null, this doesn't have any sence
        // 2: request was canceled...
        if (socialNetworkListener == null) {
            Log.e(TAG, "TwitterSocialNetwork.handleRequestResult socialNetworkListener == null");
            return false;
        }

        String error = result.getString(SocialNetworkAsyncTask.RESULT_ERROR);

        if (error != null) {
            socialNetworkListener.onError(getID(), requestID, error, data);
            mLocalListeners.remove(requestID);
            return false;
        }

        return true;
    }

}
