package com.androidsocialnetworks.app;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class SocialNetworksListFragment extends ListFragment {

    public static SocialNetworksListFragment newInstance() {
        return new SocialNetworksListFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> items = new ArrayList<String>();
        items.add("LinekdIn");
        items.add("Twitter");
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items));
    }

}
