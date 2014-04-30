package com.androidsocialnetworks.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.androidsocialnetworks.lib.SocialNetworkManager;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_container, MainFragment.newInstance())
                    .commit();
        }
    }

    // We require this, only if we use Google Plus, Google Play Services don't allow to
    // run there login intent from Fragment, so we need this hack :(
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

        SocialNetworkManager socialNetworkManager = (SocialNetworkManager)
                getSupportFragmentManager().findFragmentByTag(MainFragment.SOCIAL_NETWORK_TAG);
        if (socialNetworkManager != null) {
            Log.d(TAG, "socialNetworkManager != null");
            socialNetworkManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
