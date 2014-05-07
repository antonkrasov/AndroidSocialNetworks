package com.androidsocialnetworks.lib.listener;

import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnCheckIsFriendCompleteListener extends SocialNetworkListener {
    public void onCheckIsFriendComplete(int socialNetworkID, String userID, boolean isFriend);
}
