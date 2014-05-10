package com.github.androidsocialnetworks.apidemos.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.github.androidsocialnetworks.apidemos.R;
import com.github.androidsocialnetworks.apidemos.activity.MainActivity;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseDemoFragment;
import com.github.androidsocialnetworks.apidemos.fragment.demo.AddFriendFragment;
import com.github.androidsocialnetworks.apidemos.fragment.demo.CheckIsFriendFragment;
import com.github.androidsocialnetworks.apidemos.fragment.demo.CurrentUserProfileFragment;
import com.github.androidsocialnetworks.apidemos.fragment.demo.LoginUsingGlobalListenersFragment;
import com.github.androidsocialnetworks.apidemos.fragment.demo.LoginUsingLocalListenersFragment;
import com.github.androidsocialnetworks.apidemos.fragment.demo.OtherUsersProfile;
import com.github.androidsocialnetworks.apidemos.fragment.demo.PostMessageFragment;
import com.github.androidsocialnetworks.apidemos.fragment.demo.PostPhotoFragment;
import com.github.androidsocialnetworks.apidemos.fragment.demo.RemoveFriendFragment;

import java.util.ArrayList;
import java.util.List;

public class APIDemosListFragment extends ListFragment {

    public static final Pair<String, String> LOGIN_WITH_GLOBAL_LISTENERS = new Pair<String, String>("Login", "Using global listeners");
    public static final Pair<String, String> LOGIN_WITH_LOCAL_LISTENERS = new Pair<String, String>("Login", "Using local listeners");
    public static final Pair<String, String> CURRENT_PROFILE = new Pair<String, String>("Current Profile", "Load current user info");
    public static final Pair<String, String> LOAD_PROFILE = new Pair<String, String>("Other User's Profile", "Load custom user profile");
    public static final Pair<String, String> POST_MESSAGE = new Pair<String, String>("Post Message", "Post tweet or update status");
    public static final Pair<String, String> POST_PHOTO = new Pair<String, String>("Post Photo", "Post tweet with photo");
    public static final Pair<String, String> CHECK_IS_FRIEND = new Pair<String, String>("Check is friend", "Is other user in your friend list");
    public static final Pair<String, String> ADD_FRIEND = new Pair<String, String>("Add friend", "Add other user to your friends list");
    public static final Pair<String, String> REMOVE_FRIEND = new Pair<String, String>("Remove friend", "Remove other user from your friends list");

    public static APIDemosListFragment newInstance() {
        return new APIDemosListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Pair<String, String>> items = new ArrayList<Pair<String, String>>();

        items.add(LOGIN_WITH_GLOBAL_LISTENERS);
        items.add(LOGIN_WITH_LOCAL_LISTENERS);
        items.add(CURRENT_PROFILE);
        items.add(LOAD_PROFILE);
        items.add(POST_MESSAGE);
        items.add(POST_PHOTO);
        items.add(CHECK_IS_FRIEND);
        items.add(ADD_FRIEND);
        items.add(REMOVE_FRIEND);

        setListAdapter(new APIDemosAdater(getActivity(), items));
    }

    @Override
    public void onResume() {
        super.onResume();

        ActionBar actionBar = getMainActivity().getSupportActionBar();

        actionBar.setTitle("ASN API Demos");
        actionBar.setSubtitle(null);

        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Pair<String, String> item = (Pair<String, String>) l.getAdapter().getItem(position);

        Fragment fragment;
        if (item.equals(LOGIN_WITH_GLOBAL_LISTENERS)) {
            fragment = LoginUsingGlobalListenersFragment.newInstance();
        } else if (item.equals(LOGIN_WITH_LOCAL_LISTENERS)) {
            fragment = LoginUsingLocalListenersFragment.newInstance();
        } else if (item.equals(CURRENT_PROFILE)) {
            fragment = CurrentUserProfileFragment.newInstance();
        } else if (item.equals(LOAD_PROFILE)) {
            fragment = OtherUsersProfile.newInstance();
        } else if (item.equals(POST_MESSAGE)) {
            fragment = PostMessageFragment.newInstance();
        } else if (item.equals(POST_PHOTO)) {
            fragment = PostPhotoFragment.newInstance();
        } else if (item.equals(CHECK_IS_FRIEND)) {
            fragment = CheckIsFriendFragment.newInstance();
        } else if (item.equals(ADD_FRIEND)) {
            fragment = AddFriendFragment.newInstance();
        } else if (item.equals(REMOVE_FRIEND)) {
            fragment = RemoveFriendFragment.newInstance();
        } else {
            throw new IllegalStateException("Can't find fragment for item: " + item);
        }

        getMainActivity().getSupportActionBar().setTitle(item.first);
        getMainActivity().getSupportActionBar().setSubtitle(item.second);

        getMainActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_container, fragment)
                .addToBackStack(null)
                .commit();
        getMainActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                SocialNetworkManager socialNetworkManager =
                        (SocialNetworkManager) getFragmentManager().findFragmentByTag(BaseDemoFragment.SOCIAL_NETWORK_TAG);

                for (SocialNetwork socialNetwork : socialNetworkManager.getInitializedSocialNetworks()) {
                    socialNetwork.logout();
                }

                getFragmentManager().beginTransaction().remove(socialNetworkManager).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    public static class APIDemosAdater extends ArrayAdapter<Pair<String, String>> {
        public APIDemosAdater(Context context, List<Pair<String, String>> items) {
            super(context, android.R.layout.simple_list_item_2, android.R.id.text1, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            Pair<String, String> pair = getItem(position);

            ((TextView) view.findViewById(android.R.id.text1)).setText(pair.first);
            ((TextView) view.findViewById(android.R.id.text2)).setText(pair.second);

            return view;
        }
    }
}
