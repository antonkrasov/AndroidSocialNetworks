package com.androidsocialnetworks.lib.listener;

import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnRequestAddFriendCompleteListener extends SocialNetworkListener {
    public void onRequestAddFriendComplete(int socialNetworkID, String userID);
}
