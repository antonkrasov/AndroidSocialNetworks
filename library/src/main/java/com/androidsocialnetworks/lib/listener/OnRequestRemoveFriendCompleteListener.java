package com.androidsocialnetworks.lib.listener;

import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnRequestRemoveFriendCompleteListener extends SocialNetworkListener {
    public void onRequestRemoveFriendComplete(int socialNetworkID, String userID);
}
