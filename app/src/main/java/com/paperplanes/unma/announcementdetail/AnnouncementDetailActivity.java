package com.paperplanes.unma.announcementdetail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import butterknife.OnClick;

public class AnnouncementDetailActivity extends AppCompatActivity implements DownloadManager.DownloadEventListener {

    public static final String EXTRA_ANNOUNCEMENT_ID = "ANNOUNCEMENT_ID";

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @BindView(R.id.snackbar_container) CoordinatorLayout mSnackbarContainer;
    @BindView(R.id.attachment_layout) ConstraintLayout mAttachmentLayout;
    @BindView(R.id.attachment_filename) TextView mAttachmentFilename;
    @BindView(R.id.file_type_image) ImageView mFileTypeImage;
    @BindView(R.id.file_ext) TextView mFileExt;
    @BindView(R.id.file_size) TextView mFileSize;

    @BindView(R.id.btn_download)
    DownloadButtonProgress mDownloadBtnProgress;

    @BindView(R.id.anc_title) TextView mAncTitle;
    @BindView(R.id.publisher) TextView mAncPublisher;
    @BindView(R.id.date) TextView mAncDate;

    @BindView(R.id.desc_layout) LinearLayout mDescLayout;
    @BindView(R.id.anc_description) WebView mAncDesc;
    @BindView(R.id.desc_loading_layout) LinearLayout mLoadingLayout;
    @BindView(R.id.desc_loading_text) TextView mLoadingText;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    AnnouncementDetailViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_detail);
        ButterKnife.bind(this);
        initEmpty();

        ((App) getApplication()).getAppComponent().inject(this);

        String announcementId = null;
        if (getIntent().getExtras() != null) {
            announcementId = getIntent().getExtras().getString(EXTRA_ANNOUNCEMENT_ID);
        }

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_announcements);
        }

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(AnnouncementDetailViewModel.class);
        mViewModel.getAnnouncement().observe(this, this::showAnnouncement);
        mViewModel.getLoadingDescription().observe(this, loading -> {
            if (loading) showDescriptionLoading(getString(R.string.text_loading));
            else hideDescriptionLoading();
        });
        mViewModel.getOnDescriptionLoaded().observe(this, description -> {
            mAncDesc.loadUrl("about:blank");
            if (description != null && description.getContent() != null) {
                mAncDesc.loadData(description.getContent(), "text/html", "UTF-8");
                mAncDesc.setVisibility(View.VISIBLE);
            }
            else {
                mAncDesc.setVisibility(View.GONE);
            }
        });

        mViewModel.getError().observe(this, this::onError);
        mViewModel.setCurrentAnnouncementId(announcementId);
        mAncDesc.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showDescriptionLoading(getString(R.string.text_rendering));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                hideDescriptionLoading();
            }
        });

        WebSettings settings = mAncDesc.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setLoadsImagesAutomatically(true);

        mViewModel.getDownloadManager().addDownloadEventListener(this);

        mDownloadBtnProgress.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                onAttachmentClicked(view);
            }

            @Override
            public void onCancelButtonClick(View view) {}

            @Override
            public void onFinishButtonClick(View view) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.getDownloadManager().removeDownloadEventListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.attachment_layout)
    public void onAttachmentClicked(View view) {
        Announcement announcement = mViewModel.getAnnouncement().getValue();
        if (announcement != null) {
            Attachment attachment = announcement.getAttachment();
            if (attachment != null && attachment.getState() == Attachment.STATE_OFFLINE) {
                FileUtil.viewFile(this, attachment.getFilePath(), attachment.getMimeType());
            } else {
                doDownloadAttachment();
            }
        }
    }

    private void onError(Throwable throwable) {
        Snackbar.make(mSnackbarContainer,
                ErrorUtil.getErrorStringForThrowable(this, throwable),
                Snackbar.LENGTH_LONG)
                .show();
    }

    private void showAnnouncement(Announcement announcement) {
        mAncTitle.setText(announcement.getTitle());
        mAncPublisher.setText(announcement.getPublisher());

        String tm = (String) DateUtils.getRelativeDateTimeString(this,
                announcement.getLastUpdated().getTime(),
                DateUtils.DAY_IN_MILLIS,
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_24HOUR);
        mAncDate.setText(tm);

        if (announcement.getDescription() != null) {
            mDescLayout.setVisibility(View.VISIBLE);
        } else {
            mDescLayout.setVisibility(View.GONE);
        }

        Attachment attachment = announcement.getAttachment();
        if (attachment != null) {
            mAttachmentLayout.setVisibility(View.VISIBLE);
            mAttachmentFilename.setText(attachment.getName());
            mFileTypeImage.setImageResource(FileUtil.getDrawableResourceForFileExt(attachment.getName()));
            mFileExt.setText(FileUtil.getFileExtension(attachment.getName()));
            mFileSize.setText(FileUtil.getFormattedFileSize(attachment.getSize()));

            if (attachment.getState() == Attachment.STATE_OFFLINE) {
                showDownloadFinish();
            } else if (attachment.getState() == Attachment.STATE_DOWNLOAD_CONNECTING) {
                showDownloadConnecting();
            } else if (attachment.getState() == Attachment.STATE_DOWNLOADING) {
                showDownloadProgress();
            } else {
                showDownloadButton();
            }
        }
        else {
            mAttachmentLayout.setVisibility(View.GONE);
        }
    }

    private void showDownloadConnecting() {
        mDownloadBtnProgress.setIndeterminate();
    }

    private void showDownloadProgress() {
        mDownloadBtnProgress.setDeterminate();
    }

    private void showDownloadFinish() {
        mDownloadBtnProgress.setFinish();
    }

    private void showDownloadButton() {
        mDownloadBtnProgress.setIdle();
    }

    private void showDescriptionLoading(String text) {
        mLoadingText.setText(text);
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    private void hideDescriptionLoading() {
        mLoadingLayout.setVisibility(View.GONE);
    }

    private void doDownloadAttachment() {
        if (AppUtil.checkWritePermission(this)) {
            mViewModel.downloadAttachment();
        }
        else {
            AppUtil.requestWritePermission(this);
        }
    }

    private void initEmpty() {
        mAncTitle.setText("-");
        mAncPublisher.setText("-");
        mAncDate.setText("-");
        mAttachmentFilename.setText("-");
        mFileSize.setText("-");
        mFileExt.setText("-");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppUtil.REQUEST_WRITE_PERM_CODE &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doDownloadAttachment();
        }
        else {
            Toast.makeText(this, R.string.text_write_permission_denied, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDownloadStarted(Announcement announcement) {
        showDownloadProgress();
    }

    @Override
    public void onDownloadProgressed(Announcement announcement, int progress) {
        mDownloadBtnProgress.setProgress(progress);
    }

    @Override
    public void onDownloadFailed(Announcement announcement) {
        showDownloadButton();
        Toast.makeText(
                AnnouncementDetailActivity.this,
                getString(R.string.text_download_failed) + announcement.getAttachment().getName(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadFinished(Announcement announcement) {
        showDownloadFinish();
    }

    @Override
    public void onDownloadConnecting(Announcement announcement) {
        showDownloadConnecting();
    }

}
