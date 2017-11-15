package com.paperplanes.udas.announcementdetail;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paperplanes.udas.R;
import com.paperplanes.udas.model.Announcement;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AnnouncementDetailFragment extends Fragment {

    @BindView(R.id.ann_id)
    TextView mAnnId;

    public AnnouncementDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_announcement_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    public void setCurrentAnnouncement(Announcement announcement) {

    }
}
