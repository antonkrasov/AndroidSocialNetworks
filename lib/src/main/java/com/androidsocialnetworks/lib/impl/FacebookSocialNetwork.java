package com.androidsocialnetworks.lib.impl;

import android.support.v4.app.Fragment;

import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkException;
import com.facebook.Session;

import java.io.File;

public class FacebookSocialNetwork extends SocialNetwork {

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

    }

    @Override
    public void logout() {

    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void requestPerson() throws SocialNetworkException {

    }

    @Override
    public void requestPostMessage(String message) throws SocialNetworkException {

    }

    @Override
    public void requestPostPhoto(File photo, String message) throws SocialNetworkException {

    }

    @Override
    public void requestCheckIsFriend(String userID) throws SocialNetworkException {

    }

    @Override
    public void requestAddFriend(String userID) throws SocialNetworkException {

    }

    @Override
    public void requestRemoveFriend(String userID) throws SocialNetworkException {
        throw new SocialNetworkException("requestRemoveFriend isn't allowed for FacebookSocialNetwork");
    }
}
