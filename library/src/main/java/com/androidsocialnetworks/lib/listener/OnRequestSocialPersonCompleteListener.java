package com.androidsocialnetworks.lib.listener;

import com.androidsocialnetworks.lib.SocialPerson;
import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnRequestSocialPersonCompleteListener extends SocialNetworkListener {
    public void onRequestSocialPersonSuccess(int socialNetworkID, SocialPerson socialPerson);
}
