package com.androidsocialnetworks.lib;

import android.os.AsyncTask;
import android.os.Bundle;

public abstract class SocialNetworkAsyncTask extends AsyncTask<Bundle, Void, Bundle> {

    public static final String RESULT_ERROR = "SocialNetworkAsyncTask.RESULT_ERROR";

    @Override
    protected Bundle doInBackground(Bundle... params) {
        return null;
    }
}
