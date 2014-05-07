package com.androidsocialnetworks.lib.listener;

import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnLoginCompleteListener extends SocialNetworkListener {
    public void onLoginSuccess(int socialNetworkID);
}
