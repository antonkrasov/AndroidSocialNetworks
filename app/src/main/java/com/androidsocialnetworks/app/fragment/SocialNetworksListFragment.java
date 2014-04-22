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

    private static final String LINKED_IN = "LinkedIn";
    private static final String TWITTER = "Twitter";

    public static SocialNetworksListFragment newInstance() {
        return new SocialNetworksListFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> items = new ArrayList<String>();
        items.add(LINKED_IN);
        items.add(TWITTER);
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
        if (item.equals(LINKED_IN)) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.root_container, LinkedInFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        } else if (item.equals(TWITTER)) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.root_container, TwitterFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
