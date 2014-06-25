package com.androidsocialnetworks.lib.impl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkException;
import com.androidsocialnetworks.lib.SocialPerson;
import com.androidsocialnetworks.lib.listener.OnCheckIsFriendCompleteListener;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnPostingCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestAddFriendCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestRemoveFriendCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
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
    public AccessToken getAccessToken() {
        return new AccessToken(Session.getActiveSession().getAccessToken(), null);
    }

    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);

        final Session openSession = mSessionTracker.getOpenSession();

        if (openSession != null) {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, "already loginned", null);
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
        if (mSessionTracker == null) return;

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
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);

        final Session currentSession = mSessionTracker.getOpenSession();

        if (currentSession == null) {
            if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                mLocalListeners.get(REQUEST_GET_CURRENT_PERSON).onError(getID(),
                        REQUEST_GET_CURRENT_PERSON, "please login first", null);
            }

            return;
        }

        Request request = Request.newMeRequest(currentSession, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser me, Response response) {
                if (response.getError() != null) {
                    if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                        mLocalListeners.get(REQUEST_GET_CURRENT_PERSON).onError(
                                getID(), REQUEST_GET_CURRENT_PERSON, response.getError().getErrorMessage()
                                , null);
                    }

                    return;
                }

                if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                    SocialPerson socialPerson = new SocialPerson();
                    socialPerson.id = me.getId();
                    socialPerson.name = me.getName();
                    socialPerson.avatarURL =
                            String.format("http://graph.facebook.com/%s/picture?width=200&height=200", me.getId());

                    socialPerson.profileURL = me.getLink();
                    socialPerson.nickname = me.getUsername();

                    ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_CURRENT_PERSON))
                            .onRequestSocialPersonSuccess(getID(), socialPerson);
                }
            }
        });
        request.executeAsync();
    }

    @Override
    public void requestSocialPerson(String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        throw new SocialNetworkException("requestSocialPerson isn't allowed for FacebookSocialNetwork");
    }

    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostMessage(message, onPostingCompleteListener);
        mStatus = message;
        performPublish(PendingAction.POST_STATUS_UPDATE);
    }

    @Override
    public void requestPostPhoto(File photo, String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostPhoto(photo, message, onPostingCompleteListener);
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
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(mSocialNetworkManager.getActivity(), PERMISSION));
                return;
            }
        }

        if (action == PendingAction.POST_STATUS_UPDATE) {
            if (mLocalListeners.get(REQUEST_POST_MESSAGE) != null) {
                mLocalListeners.get(REQUEST_POST_MESSAGE).onError(getID(),
                        REQUEST_POST_MESSAGE, "no session", null);
            }
        }

        if (action == PendingAction.POST_PHOTO) {
            if (mLocalListeners.get(REQUEST_POST_PHOTO) != null) {
                mLocalListeners.get(REQUEST_POST_PHOTO).onError(getID(),
                        REQUEST_POST_PHOTO, "no session", null);
            }
        }
    }

    @Override
    public void requestCheckIsFriend(String userID, OnCheckIsFriendCompleteListener onCheckIsFriendCompleteListener) {
        throw new SocialNetworkException("requestCheckIsFriend isn't allowed for FacebookSocialNetwork");
    }

    @Override
    public void requestAddFriend(String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        throw new SocialNetworkException("requestAddFriend isn't allowed for FacebookSocialNetwork");
    }

    @Override
    public void requestRemoveFriend(String userID, OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener) {
        throw new SocialNetworkException("requestRemoveFriend isn't allowed for FacebookSocialNetwork");
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        Log.d(TAG, "onSessionStateChange: " + state + " : " + exception);

        if (mSessionState == SessionState.OPENING && state == SessionState.OPENED) {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
                mLocalListeners.remove(REQUEST_LOGIN);
            }
        }

        if (state == SessionState.CLOSED_LOGIN_FAILED) {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, exception.getMessage(), null);
                mLocalListeners.remove(REQUEST_LOGIN);
            }
        }

        mSessionState = state;

        if (mPendingAction != PendingAction.NONE &&
                (exception instanceof FacebookOperationCanceledException ||
                        exception instanceof FacebookAuthorizationException)) {
            mPendingAction = PendingAction.NONE;

            if (mLocalListeners.get(REQUEST_POST_MESSAGE) != null) {
                mLocalListeners.get(REQUEST_POST_MESSAGE).onError(getID(),
                        REQUEST_POST_MESSAGE, "permission not granted", null);
            }

            if (mLocalListeners.get(REQUEST_POST_PHOTO) != null) {
                mLocalListeners.get(REQUEST_POST_PHOTO).onError(getID(),
                        REQUEST_POST_PHOTO, "permission not granted", null);
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
                            publishSuccess(REQUEST_POST_MESSAGE,
                                    response.getError() == null ? null : response.getError().getErrorMessage());
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
                    publishSuccess(REQUEST_POST_PHOTO,
                            response.getError() == null ? null : response.getError().getErrorMessage());
                }
            });
            request.executeAsync();
        } else {
            mPendingAction = PendingAction.POST_PHOTO;
        }
    }

    private void publishSuccess(String requestID, String error) {
        if (mLocalListeners.get(requestID) == null) return;

        if (error != null) {
            mLocalListeners.get(requestID).onError(getID(), requestID, error, null);
            return;
        }

        ((OnPostingCompleteListener) mLocalListeners.get(requestID)).onPostSuccessfully(getID());
        mLocalListeners.remove(requestID);
    }

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }
}
