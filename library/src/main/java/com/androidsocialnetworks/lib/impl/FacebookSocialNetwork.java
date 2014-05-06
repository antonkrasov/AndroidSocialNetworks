package com.androidsocialnetworks.lib.impl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkException;
import com.androidsocialnetworks.lib.SocialPerson;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.internal.SessionTracker;
import com.facebook.internal.Utility;
import com.facebook.model.GraphUser;

import java.io.File;
import java.util.Collections;

/**
 * TODO: think about canceling requests
 */
public class FacebookSocialNetwork extends SocialNetwork {

    public static final int ID = 4;

    private static final String TAG = FacebookSocialNetwork.class.getSimpleName();
    private static final String PERMISSION = "publish_actions";
    private SessionTracker mSessionTracker;
    private UiLifecycleHelper mUILifecycleHelper;
    private String mApplicationId;
    private SessionState mSessionState;
    private String mPhotoPath;
    private String mStatus;
    private PendingAction mPendingAction = PendingAction.NONE;
    private Session.StatusCallback mSessionStatusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    public FacebookSocialNetwork(Fragment fragment) {
        super(fragment);
    }

    @Override
    public boolean isConnected() {
        Session session = Session.getActiveSession();
        return (session != null && session.isOpened());
    }

    @Override
    public void requestLogin() throws SocialNetworkException {
        final Session openSession = mSessionTracker.getOpenSession();

        if (openSession != null) {
            if (mOnLoginCompleteListener != null) {
                mOnLoginCompleteListener.onLoginFailed(getID(), "already loginned");
            }
        }

        Session currentSession = mSessionTracker.getSession();
        if (currentSession == null || currentSession.getState().isClosed()) {
            mSessionTracker.setSession(null);
            Session session = new Session.Builder(mSocialNetworkManager.getActivity())
                    .setApplicationId(mApplicationId).build();
            Session.setActiveSession(session);
            currentSession = session;
        }

        if (!currentSession.isOpened()) {
            Session.OpenRequest openRequest = null;
            openRequest = new Session.OpenRequest(mSocialNetworkManager);

            openRequest.setDefaultAudience(SessionDefaultAudience.EVERYONE);
            openRequest.setPermissions(Collections.<String>emptyList());
            openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);

            currentSession.openForRead(openRequest);
        }
    }

    @Override
    public void logout() {
        final Session openSession = mSessionTracker.getOpenSession();

        if (openSession != null) {
            openSession.closeAndClearTokenInformation();
        }
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void requestPerson() throws SocialNetworkException {
        final Session currentSession = mSessionTracker.getOpenSession();

        if (currentSession == null) {
            if (mOnRequestSocialPersonListener != null) {
                mOnRequestSocialPersonListener.onRequestSocialPersonFailed(getID(), "please login first");
            }

            return;
        }

        Request request = Request.newMeRequest(currentSession, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser me, Response response) {
                if (response.getError() != null) {
                    if (mOnRequestSocialPersonListener != null) {
                        mOnRequestSocialPersonListener.onRequestSocialPersonFailed(getID(), response.getError().getErrorMessage());
                    }

                    return;
                }

                if (mOnRequestSocialPersonListener != null) {
                    SocialPerson socialPerson = new SocialPerson();
                    socialPerson.id = me.getId();
                    socialPerson.name = me.getName();
                    socialPerson.avatarURL =
                            String.format("http://graph.facebook.com/%s/picture?width=200&height=200", me.getUsername());

                    mOnRequestSocialPersonListener.onRequestSocialPersonSuccess(getID(), socialPerson);
                }
            }
        });
        Request.executeBatchAsync(request);
    }

    @Override
    public void requestPostMessage(String message) throws SocialNetworkException {
        mStatus = message;
        performPublish(PendingAction.POST_STATUS_UPDATE);
    }

    @Override
    public void requestPostPhoto(File photo, String message) throws SocialNetworkException {
        mPhotoPath = photo.getAbsolutePath();
        performPublish(PendingAction.POST_PHOTO);
    }

    private void performPublish(PendingAction action) {
        Session session = Session.getActiveSession();
        if (session != null) {
            mPendingAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                handlePendingAction();
                return;
            } else if (session.isOpened()) {
                // We need to get new permissions, then complete the action when we get called back.
                Session.NewPermissionsRequest newPermissionsRequest =
                        new Session.NewPermissionsRequest(mSocialNetworkManager, PERMISSION);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }
        }

        if (mOnPostingListener != null) {
            mOnPostingListener.onPostFailed(getID(), "no session");
        }
    }

    @Override
    public void requestCheckIsFriend(String userID) throws SocialNetworkException {
        throw new SocialNetworkException("requestCheckIsFriend isn't allowed for FacebookSocialNetwork");
    }

    @Override
    public void requestAddFriend(String userID) throws SocialNetworkException {
        throw new SocialNetworkException("requestAddFriend isn't allowed for FacebookSocialNetwork");
    }

    @Override
    public void requestRemoveFriend(String userID) throws SocialNetworkException {
        throw new SocialNetworkException("requestRemoveFriend isn't allowed for FacebookSocialNetwork");
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        Log.d(TAG, "onSessionStateChange: " + state + " : " + exception);

        if (mSessionState == SessionState.OPENING && state == SessionState.OPENED) {
            if (mOnLoginCompleteListener != null) {
                mOnLoginCompleteListener.onLoginSuccess(getID());
            }
        }

        if (state == SessionState.CLOSED_LOGIN_FAILED) {
            if (mOnLoginCompleteListener != null) {
                mOnLoginCompleteListener.onLoginFailed(getID(), exception.getMessage());
            }
        }

        mSessionState = state;

        if (mPendingAction != PendingAction.NONE &&
                (exception instanceof FacebookOperationCanceledException ||
                        exception instanceof FacebookAuthorizationException)) {
            mPendingAction = PendingAction.NONE;

            if (mOnPostingListener != null) {
                mOnPostingListener.onPostFailed(getID(), "permission not granted");
            }
        }

        if (session.getPermissions().contains(PERMISSION)
                && state == SessionState.OPENED_TOKEN_UPDATED) {
            handlePendingAction();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUILifecycleHelper = new UiLifecycleHelper(mSocialNetworkManager.getActivity(), mSessionStatusCallback);
        mUILifecycleHelper.onCreate(savedInstanceState);

        initializeActiveSessionWithCachedToken(mSocialNetworkManager.getActivity());
        finishInit();
    }

    private boolean initializeActiveSessionWithCachedToken(Context context) {
        if (context == null) {
            return false;
        }

        Session session = Session.getActiveSession();
        if (session != null) {
            return session.isOpened();
        }

        mApplicationId = Utility.getMetadataApplicationId(context);
        if (mApplicationId == null) {
            return false;
        }

        return Session.openActiveSessionFromCache(context) != null;
    }

    private void finishInit() {
        mSessionTracker = new SessionTracker(
                mSocialNetworkManager.getActivity(), mSessionStatusCallback, null, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUILifecycleHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUILifecycleHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUILifecycleHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUILifecycleHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mUILifecycleHelper.onActivityResult(requestCode, resultCode, data, null);

        Session session = mSessionTracker.getOpenSession();
        if (session != null) {
            session.onActivityResult(mSocialNetworkManager.getActivity(), requestCode, resultCode, data);
        }
    }

    private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains(PERMISSION);
    }

    @SuppressWarnings("incomplete-switch")
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = mPendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        mPendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case POST_PHOTO:
                postPhoto(mPhotoPath);
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate(mStatus);
                break;
        }
    }

    private void postStatusUpdate(String message) {
        if (isConnected() && hasPublishPermission()) {
            Request request = Request
                    .newStatusUpdateRequest(Session.getActiveSession(), message, null, null, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            publishSuccess(response.getError() == null ? null : response.getError().getErrorMessage());
                        }
                    });
            request.executeAsync();
        } else {
            mPendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    private void postPhoto(final String path) {
        if (hasPublishPermission()) {
            Bitmap image = BitmapFactory.decodeFile(path);
            Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), image, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    publishSuccess(response.getError() == null ? null : response.getError().getErrorMessage());
                }
            });
            request.executeAsync();
        } else {
            mPendingAction = PendingAction.POST_PHOTO;
        }
    }

    private void publishSuccess(String error) {
        if (error != null) {
            if (mOnPostingListener != null) {
                mOnPostingListener.onPostFailed(getID(), error);
            }

            return;
        }

        if (mOnPostingListener != null) {
            mOnPostingListener.onPostSuccessfully(getID());
        }
    }

    /**
     * We can't cancel login request for facebook
     */
    @Override
    public void cancelLoginRequest() {

    }

    @Override
    public void cancelGetPersonRequest() {

    }

    @Override
    public void cancelPostMessageRequest() {

    }

    @Override
    public void cancelPostPhotoRequest() {

    }

    @Override
    public void cancenCheckIsFriendRequest() {

    }

    @Override
    public void cancelAddFriendRequest() {

    }

    @Override
    public void cancenRemoveFriendRequest() {

    }

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }
}
