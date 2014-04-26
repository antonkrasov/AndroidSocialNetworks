package com.androidsocialnetworks.app.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidsocialnetworks.app.R;
import com.androidsocialnetworks.app.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class SocialNetworksListFragment extends ListFragment {

    public static final String LINKED_IN = "LinkedIn";
    public static final String TWITTER = "Twitter";
    public static final String FACEBOOK = "Facebook";

    public static SocialNetworksListFragment newInstance() {
        return new SocialNetworksListFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> items = new ArrayList<String>();
        items.add(LINKED_IN);
        items.add(TWITTER);
        items.add(FACEBOOK);
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Android Social Networks");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) l.getAdapter().getItem(position);
        getFragmentManager().beginTransaction()
                .replace(R.id.root_container, SocialNetworkFragment.newInstance(item))
                .addToBackStack(null)
                .commit();
    }
}
