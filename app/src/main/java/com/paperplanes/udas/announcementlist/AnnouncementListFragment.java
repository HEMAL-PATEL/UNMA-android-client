package com.paperplanes.udas.announcementlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.paperplanes.udas.App;
import com.paperplanes.udas.R;
import com.paperplanes.udas.model.Announcement;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abdularis on 14/11/17.
 */

public class AnnouncementListFragment extends Fragment implements AnnouncementListView {

    @BindView(R.id.rv_announcements)
    RecyclerView mRvAnnouncements;
    AnnouncementAdapter mAnnouncementAdapter;

    @BindView(R.id.progress)
    ProgressBar mProgress;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    AnnouncementListPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_announcement_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        ((App) getActivity().getApplication()).getAppComponent().inject(this);

        mPresenter.setView(this);
        mAnnouncementAdapter = new AnnouncementAdapter(new ArrayList<>());
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        mRvAnnouncements.setLayoutManager(layoutManager);
        mRvAnnouncements.setAdapter(mAnnouncementAdapter);

        mPresenter.retrieveData();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshData();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dispose();
    }

    @Override
    public void showLoading(boolean active) {
        if (active) {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
        }
        else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showAnnouncementList(List<Announcement> announcements) {
        mAnnouncementAdapter.replaceAll(announcements);
        showLoading(false);
    }

    @Override
    public void showError(String errMsg) {
        Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
    }
}
