package com.androidsocialnetworks.lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public abstract class SocialNetwork {
    private static final String TAG = SocialNetwork.class.getSimpleName();

    private static final String SHARED_PREFERENCES_NAME = "social_networks";

    protected Activity mActivity;
    protected SharedPreferences mSharedPreferences;

    protected SocialNetwork(Activity activity) {
        mActivity = activity;

        mSharedPreferences = mActivity.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: " + savedInstanceState);
    }

    public void onStart() {
        Log.d(TAG, "onStart");
    }

    public void onResume() {
        Log.d(TAG, "onResume");
    }

    public void onPause() {
        Log.d(TAG, "onPause");
    }

    public void onStop() {
        Log.d(TAG, "onStop");
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: " + outState);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onRestoreInstanceState: " + requestCode + " : " + resultCode + " : " + data);
    }

}
