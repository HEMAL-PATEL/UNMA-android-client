package com.paperplanes.unma.announcementmedialist;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.abdularis.buttonprogress.DownloadButtonProgress;
import com.paperplanes.unma.App;
import com.paperplanes.unma.R;
import com.paperplanes.unma.common.AppUtil;
import com.paperplanes.unma.common.ErrorUtil;
import com.paperplanes.unma.common.FileUtil;
import com.paperplanes.unma.data.DownloadManager;
import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Attachment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abdularis on 01/12/17.
 */

public class MediaListFragment extends Fragment implements MediaAdapter.OnClickListener {

    @BindView(R.id.rv_media) RecyclerView mRvAnnouncements;
    private MediaAdapter mMediaAdapter;
    @BindView(R.id.snackbar_container) View mSnackContainer;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.no_data_layout) LinearLayout mNoDataLayout;

    private RecyclerView.LayoutManager layoutManager;
    private DownloadEventHandler mDownloadEventHandler = new DownloadEventHandler();

    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    MediaListViewModel mViewModel;

    private int mCurrentClickedPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_list, container, false);
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
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getDownloadManager().removeDownloadEventListener(mDownloadEventHandler);
    }

    private void initListView() {
        mMediaAdapter = new MediaAdapter();
        mMediaAdapter.setOnClickListener(this);

        DividerItemDecoration divider =
                new DividerItemDecoration(mRvAnnouncements.getContext(), LinearLayoutManager.VERTICAL);
        layoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        mRvAnnouncements.setLayoutManager(layoutManager);
        mRvAnnouncements.setAdapter(mMediaAdapter);
        mRvAnnouncements.addItemDecoration(divider);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MediaListViewModel.class);
        mViewModel.getLoading().observe(this, this::showLoading);
        mViewModel.getError().observe(this, this::showError);
        mViewModel.getAnnouncements().observe(this, announcements -> {
            mMediaAdapter.replaceAll(announcements);
            if (announcements != null && announcements.isEmpty()) {
                mRvAnnouncements.setVisibility(View.GONE);
                mNoDataLayout.setVisibility(View.VISIBLE);
            }
            else {
                mRvAnnouncements.setVisibility(View.VISIBLE);
                mNoDataLayout.setVisibility(View.GONE);
            }
        });

        mViewModel.getDownloadManager().addDownloadEventListener(mDownloadEventHandler);
    }

    private void showLoading(boolean active) {
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(active));
    }

    private void showError(Throwable throwable) {
        Snackbar.make(mSnackContainer,
                ErrorUtil.getErrorStringForThrowable(getContext(), throwable),
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void itemClicked(int position) {
        Announcement announcement = mMediaAdapter.getItem(position);
        Attachment attachment = announcement.getAttachment();
        if (attachment != null) {
            if (attachment.getState() == Attachment.STATE_OFFLINE)
                FileUtil.viewFile(getContext(), attachment.getFilePath(), attachment.getMimeType());
            else
                doDownloadAttachment(position);
        }
    }

    @Override
    public boolean itemLongClicked(int position) {
        Announcement announcement = mMediaAdapter.getItem(position);
        Attachment attachment = announcement.getAttachment();
        if (attachment != null &&
                attachment.getState() == Attachment.STATE_OFFLINE) {
            // TODO show context menu
        }
        return true;
    }

    @Override
    public void itemDownloadButtonClicked(int position) {
        mCurrentClickedPosition = position;
        doDownloadAttachment(position);
    }

    private void doDownloadAttachment(int position) {
        if (AppUtil.checkWritePermission(getActivity())) {
            Announcement announcement = mMediaAdapter.getItem(position);
            mViewModel.downloadAttachment(announcement);
        }
        else {
            AppUtil.requestWritePermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppUtil.REQUEST_WRITE_PERM_CODE &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doDownloadAttachment(mCurrentClickedPosition);
        }
        else {
            Toast.makeText(getActivity(), "Permission denied, please allow to download!", Toast.LENGTH_LONG).show();
        }
    }

    private class DownloadEventHandler implements DownloadManager.DownloadEventListener {
        @Override
        public void onDownloadStarted(Announcement announcement) {
            mMediaAdapter.notifyItemChanged(announcement);
        }

        @Override
        public void onDownloadProgressed(Announcement announcement, int progress) {
            View view = layoutManager.findViewByPosition(mMediaAdapter.getPosition(announcement));
            if (view != null) {
                DownloadButtonProgress progressBar = view.findViewById(R.id.btn_download);
                if (progressBar != null) {
                    progressBar.setProgress(progress);
                }
            }
        }

        @Override
        public void onDownloadFailed(Announcement announcement) {
            mMediaAdapter.notifyItemChanged(announcement);
            Toast.makeText(getActivity(),
                    getString(R.string.text_download_failed) + " " + announcement.getAttachment().getName(),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDownloadFinished(Announcement announcement) {
            mMediaAdapter.notifyItemChanged(announcement);
        }

        @Override
        public void onDownloadConnecting(Announcement announcement) {
            mMediaAdapter.notifyItemChanged(announcement);
        }

    }
}
