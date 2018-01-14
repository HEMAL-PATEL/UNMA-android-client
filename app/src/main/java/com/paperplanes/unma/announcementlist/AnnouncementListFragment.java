package com.paperplanes.unma.announcementlist;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.paperplanes.unma.App;
import com.paperplanes.unma.R;
import com.paperplanes.unma.announcementdetail.AnnouncementDetailActivity;
import com.paperplanes.unma.announcementmedialist.FilterAttachment;
import com.paperplanes.unma.common.ErrorUtil;
import com.paperplanes.unma.model.Announcement;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Predicate;

/**
 * Created by abdularis on 14/11/17.
 */

public class AnnouncementListFragment extends Fragment {

    @BindView(R.id.rv_announcements) RecyclerView mRvAnnouncements;
    private AnnouncementAdapter mAnnouncementAdapter;

    @BindView(R.id.snackbar_container) View mSnackContainer;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.no_data_layout) LinearLayout mNoDataLayout;

    private Menu mMenu;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    AnnouncementListViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_announcement_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this, view);

        initListView();
        initViewModel();

        mSwipeRefreshLayout.setOnRefreshListener(mViewModel::refresh);
        mViewModel.startListenToData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_announcement_list, menu);
        MenuItem selectedMenuItem = menu.findItem(mViewModel.getFilterId());
        if (selectedMenuItem != null)
            onOptionsItemSelected(selectedMenuItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Predicate<Announcement> filter = null;
        int filterId = 0;
        switch (item.getItemId()) {
            case R.id.menu_filter_list_all:
                filter = mViewModel.getDefaultFilter();
                filterId = R.id.menu_filter_list_all;
                break;
            case R.id.menu_filter_list_by_unread:
                filter = new FilterUnread();
                filterId = R.id.menu_filter_list_by_unread;
                break;
            case R.id.menu_filter_list_by_read:
                filter = new FilterRead();
                filterId = R.id.menu_filter_list_by_read;
                break;
            case R.id.menu_filter_list_with_attachment:
                filter = new FilterAttachment();
                filterId = R.id.menu_filter_list_with_attachment;
                break;
            case R.id.menu_force_refresh:
                mViewModel.forceRefresh();
                return true;
        }

        if (filter != null) {
            item.setChecked(true);
            mViewModel.setFilter(filter, filterId);
            mViewModel.reSubscribeToData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initListView() {
        mAnnouncementAdapter = new AnnouncementAdapter(getActivity());
        mAnnouncementAdapter.setOnClickListener(this::onAnnouncementItemClicked);

        DividerItemDecoration divider =
                new DividerItemDecoration(mRvAnnouncements.getContext(), LinearLayoutManager.VERTICAL);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        mRvAnnouncements.setLayoutManager(layoutManager);
        mRvAnnouncements.setAdapter(mAnnouncementAdapter);
        mRvAnnouncements.addItemDecoration(divider);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(AnnouncementListViewModel.class);
        mViewModel.getLoading().observe(this, this::showLoading);
        mViewModel.getError().observe(this, this::showError);
        mViewModel.getAnnouncements().observe(this, announcements -> {
            mAnnouncementAdapter.replaceAll(announcements);
            if (announcements != null && announcements.isEmpty()) {
                mRvAnnouncements.setVisibility(View.GONE);
                mNoDataLayout.setVisibility(View.VISIBLE);
            }
            else {
                mRvAnnouncements.setVisibility(View.VISIBLE);
                mNoDataLayout.setVisibility(View.GONE);
            }
        });
    }

    private void showLoading(boolean active) {
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(active));
    }

    private void showError(Throwable throwable) {
        Snackbar.make(mSnackContainer,
                ErrorUtil.getErrorStringForThrowable(getContext(), throwable),
                Snackbar.LENGTH_LONG).show();
    }

    private void onAnnouncementItemClicked(View itemView, Announcement announcement) {
        Intent i = new Intent(getActivity(), AnnouncementDetailActivity.class);
        i.putExtra(AnnouncementDetailActivity.EXTRA_ANNOUNCEMENT_ID, announcement.getId());
        startActivity(i);
    }
}
