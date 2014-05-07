package com.github.androidsocialnetworks.apidemos.fragment.base;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

import com.github.androidsocialnetworks.apidemos.fragment.dialog.AlertDialogFragment;

public class BaseDemoFragment extends Fragment {

    private ProgressDialog mProgressDialog;

    protected void showProgress(String text) {
        hideProgress();

        mProgressDialog = ProgressDialog.show(getActivity(), "Executing request", text);
        mProgressDialog.show();
    }

    protected void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    protected void handleError(String text) {
        AlertDialogFragment.newInstance("Error", text).show(getFragmentManager(), null);
    }

    protected void handleSuccess(String title, String message) {
        AlertDialogFragment.newInstance(title, message).show(getFragmentManager(), null);
    }

}
