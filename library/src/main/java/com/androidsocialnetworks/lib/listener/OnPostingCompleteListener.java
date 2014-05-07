package com.androidsocialnetworks.lib.listener;

import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnPostingCompleteListener extends SocialNetworkListener {
    public void onPostSuccessfully(int socialNetworkID);
}
