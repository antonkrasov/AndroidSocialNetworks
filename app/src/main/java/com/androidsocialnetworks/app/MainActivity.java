package com.androidsocialnetworks.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.androidsocialnetworks.lib.SocialNetworkManager;

public class MainActivity extends ActionBarActivity {

    private SocialNetworkManager mSocialNetworkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mSocialNetworkManager = SocialNetworkManager.Builder.create()
                    .twitter("KXJXZZItkNGiV3m2b5Q4GkGlM", "ggBxzt3Q7CcOM29tveliMJuBrCb8Y3AhXEi2K5M9m2IoGQYktk")
                    .linkedIn("77ieoe71pon7wq", "pp5E8hkdY9voGC9y", "r_basicprofile+rw_nus+r_network+w_messages")
                    .build();

            getSupportFragmentManager().beginTransaction().add(mSocialNetworkManager, SocialNetworkManager.SAVE_KEY).commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_container, SocialNetworksListFragment.newInstance())
                    .commit();
        } else {
            mSocialNetworkManager = (SocialNetworkManager) getSupportFragmentManager().getFragment(savedInstanceState, SocialNetworkManager.SAVE_KEY);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, SocialNetworkManager.SAVE_KEY, mSocialNetworkManager);

        super.onSaveInstanceState(outState);
    }
}
