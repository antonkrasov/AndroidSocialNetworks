package com.github.androidsocialnetworks.apidemos.fragment.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.github.androidsocialnetworks.apidemos.fragment.dialog.AlertDialogFragment;
import com.github.androidsocialnetworks.apidemos.fragment.dialog.ProgressDialogFragment;

public class BaseDemoFragment extends Fragment {

    public static final String SOCIAL_NETWORK_TAG = "BaseLoginDemoFragment.SOCIAL_NETWORK_TAG";
    private static final String PROGRESS_DIALOG_TAG = "BaseDemoFragment.PROGRESS_DIALOG_TAG";
    protected SocialNetworkManager mSocialNetworkManager;
    protected boolean mSocialNetworkManagerInitialized = false;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    protected void showProgress(String text) {
        ProgressDialogFragment.newInstance(text).show(getFragmentManager(), PROGRESS_DIALOG_TAG);
    }

    protected void hideProgress() {
        getFragmentManager().beginTransaction()
                .remove(getFragmentManager().findFragmentByTag(PROGRESS_DIALOG_TAG)).commit();
    }

    protected void handleError(String text) {
        AlertDialogFragment.newInstance("Error", text).show(getFragmentManager(), null);
    }

    protected void handleSuccess(String title, String message) {
        AlertDialogFragment.newInstance(title, message).show(getFragmentManager(), null);
    }

}
