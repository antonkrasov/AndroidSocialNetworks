package com.github.androidsocialnetworks.apidemos.fragment.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsocialnetworks.lib.SocialPerson;
import com.github.androidsocialnetworks.apidemos.R;
import com.squareup.picasso.Picasso;

public class ShowProfileFragment extends Fragment {

    public static final String PARAM_SOCIAL_PERSON = "ShowProfileFragment.PARAM_SOCIAL_PERSON";

    public static ShowProfileFragment newInstance(SocialPerson socialPerson) {
        Bundle args = new Bundle();
        args.putParcelable(PARAM_SOCIAL_PERSON, socialPerson);

        ShowProfileFragment showProfileFragment = new ShowProfileFragment();
        showProfileFragment.setArguments(args);
        return showProfileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView avatarImageView = (ImageView) view.findViewById(R.id.avatar_image_view);
        TextView profileURLTextView = (TextView) view.findViewById(R.id.profile_url_text_view);
        TextView textView = (TextView) view.findViewById(R.id.text_view);

        SocialPerson socialPerson = getArguments().getParcelable(PARAM_SOCIAL_PERSON);

        profileURLTextView.setText(socialPerson.profileURL);

        Picasso.with(getActivity()).load(socialPerson.avatarURL).into(avatarImageView);

        StringBuilder builder = new StringBuilder();
        builder.append("ID: ");
        builder.append(socialPerson.id);
        builder.append('\n');
        builder.append("Name: ");
        builder.append(socialPerson.name);
        builder.append('\n');
        builder.append("Company: ");
        builder.append(socialPerson.company);
        builder.append('\n');
        builder.append("Position: ");
        builder.append(socialPerson.position);
        builder.append('\n');
        builder.append("Nickname: ");
        builder.append(socialPerson.nickname);
        builder.append('\n');
        textView.setText(builder.toString());
    }
}
