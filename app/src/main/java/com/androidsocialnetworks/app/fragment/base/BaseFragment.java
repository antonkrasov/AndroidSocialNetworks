package com.androidsocialnetworks.app.fragment.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidsocialnetworks.app.R;
import com.androidsocialnetworks.app.activity.MainActivity;

public abstract class BaseFragment extends Fragment {

    private static final String SAVE_STATE_KEY_UI_STATE = "BaseFragment.SAVE_STATE_KEY_UI_STATE";
    private View mContainerContent;
    private View mContainerProgress;
    private UIState mUIState = UIState.CONTENT;

    protected abstract void onConnectDisconnectButtonClick();

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social_network, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.connect_disconnect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConnectDisconnectButtonClick();
            }
        });

        mContainerContent = view.findViewById(R.id.container_content);
        mContainerProgress = view.findViewById(R.id.container_progress);

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_STATE_KEY_UI_STATE)) {
            switchUIState((UIState.values()[savedInstanceState.getInt(SAVE_STATE_KEY_UI_STATE)]));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_STATE_KEY_UI_STATE, mUIState.ordinal());
        super.onSaveInstanceState(outState);
    }

    protected void switchUIState(UIState state) {
        mUIState = state;

        mContainerContent.setVisibility(View.GONE);
        mContainerProgress.setVisibility(View.GONE);

        switch (state) {
            case CONTENT:
                mContainerContent.setVisibility(View.VISIBLE);
                break;
            case PROGRESS:
                mContainerProgress.setVisibility(View.VISIBLE);
                break;
        }
    }

    public static enum UIState {
        CONTENT,
        PROGRESS
    }

}
