package com.github.androidsocialnetworks.apidemos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoginUsingGlobalListenersFragment extends Fragment {

    public static LoginUsingGlobalListenersFragment newInstance() {
        return new LoginUsingGlobalListenersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_using_global_listeners, container, false);
    }
}
