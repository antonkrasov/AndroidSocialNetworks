package com.github.androidsocialnetworks.apidemos;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class APIDemosListFragment extends ListFragment {

    public static final Pair<String, String> LOGIN_WITH_GLOBAL_LISTENERS = new Pair<String, String>("Login", "Using global listeners");
    public static final Pair<String, String> LOGIN_WITH_LOCAL_LISTENERS = new Pair<String, String>("Login", "Using local listeners");

    public static APIDemosListFragment newInstance() {
        return new APIDemosListFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Pair<String, String>> items = new ArrayList<Pair<String, String>>();

        items.add(LOGIN_WITH_GLOBAL_LISTENERS);
        items.add(LOGIN_WITH_LOCAL_LISTENERS);

        setListAdapter(new APIDemosAdater(getActivity(), items));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Object item = l.getAdapter().getItem(position);

        Fragment fragment;
        if (item.equals(LOGIN_WITH_GLOBAL_LISTENERS)) {
            fragment = LoginUsingGlobalListenersFragment.newInstance();
        } else if (item.equals(LOGIN_WITH_LOCAL_LISTENERS)) {
            fragment = LoginUsingLocalListenersFragment.newInstance();
        } else {
            throw new IllegalStateException("Can't find fragment for item: " + item);
        }

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_container, fragment)
                .addToBackStack(null)
                .commit();
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
