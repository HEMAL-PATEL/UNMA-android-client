package com.paperplanes.unma.profiledetail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paperplanes.unma.App;
import com.paperplanes.unma.R;
import com.paperplanes.unma.ViewModelFactory;
import com.paperplanes.unma.model.Profile;
import com.paperplanes.unma.profileupdate.ProfileUpdateActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by abdularis on 06/12/17.
 */

public class ProfileDetailFragment extends Fragment {

    @BindView(R.id.text_name) TextView mName;
    @BindView(R.id.text_username) TextView mUsername;
    @BindView(R.id.text_class) TextView mClass;
    @BindView(R.id.text_class_type) TextView mClsType;
    @BindView(R.id.progress_layout)
    LinearLayout mLoadingLayout;

    @Inject
    ViewModelFactory mViewModelFactory;
    ProfileDetailViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ((App) getActivity().getApplication()).getAppComponent().inject(this);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ProfileDetailViewModel.class);
        mViewModel.getProfile().observe(this, this::showProfile);
        mViewModel.getLoading().observe(this, this::showLoading);
        mViewModel.loadProfile();
    }

    @OnClick(R.id.edit_profile_btn)
    public void editProfileClicked(View view) {
        Intent i = new Intent(getActivity(), ProfileUpdateActivity.class);
        startActivity(i);
    }

    private void showProfile(Profile profile) {
        if (profile == null) return;
        mName.setText(profile.getName());
        mUsername.setText(profile.getUsername());

        Profile.ProfileClass cls = profile.getProfileClass();
        String str = cls.getStudyProgram() + " " + cls.getClassName() + " " + String.valueOf(cls.getClassYear());
        mClass.setText(str);
        mClsType.setText(cls.getClassType());
    }

    private void showLoading(boolean show) {
        if (show)
            mLoadingLayout.setVisibility(View.VISIBLE);
        else
            mLoadingLayout.setVisibility(View.GONE);
    }
}
