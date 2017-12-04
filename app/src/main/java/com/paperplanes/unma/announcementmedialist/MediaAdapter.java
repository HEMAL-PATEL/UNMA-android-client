package com.paperplanes.unma.announcementmedialist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.paperplanes.unma.R;
import com.paperplanes.unma.common.FileUtil;
import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Attachment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

/**
 * Created by abdularis on 01/12/17.
 */

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private List<Announcement> mAnnouncements;
    private OnClickListener mClickListener;

    public MediaAdapter() {
        mAnnouncements = new ArrayList<>();
    }

    void replaceAll(@NonNull List<Announcement> announcements) {
        if (announcements.isEmpty())
            return;
        mAnnouncements.clear();
        mAnnouncements.addAll(announcements);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_media_attachment, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Announcement ann = mAnnouncements.get(i);
        viewHolder.bindData(ann);
    }

    @Override
    public int getItemCount() {
        return mAnnouncements.size();
    }

    public void notifyItemChanged(Announcement announcement) {
        notifyItemChanged(mAnnouncements.indexOf(announcement));
    }

    public int getPosition(Announcement announcement) {
        return mAnnouncements.indexOf(announcement);
    }

    public Announcement getItem(int position) {
        return mAnnouncements.get(position);
    }

    public void setOnClickListener(OnClickListener listener) {
        mClickListener = listener;
    }

    public interface OnClickListener {

        void itemClicked(int position);

        boolean itemLongClicked(int position);

        void itemDownloadButtonClicked(int position);

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.attachment_filename) TextView filename;
        @BindView(R.id.file_type_image) ImageView fileTypeImage;
        @BindView(R.id.file_ext) TextView fileExt;
        @BindView(R.id.file_size) TextView fileSize;
        @BindView(R.id.download_btn) ImageButton downloadBtn;
        @BindView(R.id.offline_pin) ImageView offlinePin;
        @BindView(R.id.download_progress) CircleProgress downloadProgress;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                if (mClickListener != null)
                    mClickListener.itemClicked(getAdapterPosition());
            });
            itemView.setOnLongClickListener(v -> mClickListener != null && mClickListener.itemLongClicked(getAdapterPosition()));
            downloadBtn.setOnClickListener(v -> {
                if (mClickListener != null)
                    mClickListener.itemDownloadButtonClicked(getAdapterPosition());
            });
        }

        void bindData(Announcement announcement) {
            if (announcement != null && announcement.getAttachment() != null) {
                String filenameStr = announcement.getAttachment().getName();

                filename.setText(filenameStr);
                fileTypeImage.setImageResource(FileUtil.getDrawableResourceForFileExt(filenameStr));
                fileExt.setText(FileUtil.getFileExtension(filenameStr));
                fileSize.setText(FileUtil.getFormattedFileSize(announcement.getAttachment().getSize()));

                int state = announcement.getAttachment().getState();
                if (state == Attachment.STATE_OFFLINE) {
                    downloadBtn.setVisibility(View.GONE);
                    downloadProgress.setVisibility(View.GONE);
                    offlinePin.setVisibility(View.VISIBLE);
                } else if (state == Attachment.STATE_DOWNLOADING) {
                    downloadBtn.setVisibility(View.GONE);
                    downloadProgress.setVisibility(View.VISIBLE);
                    offlinePin.setVisibility(View.GONE);
                } else {
                    downloadBtn.setVisibility(View.VISIBLE);
                    downloadProgress.setVisibility(View.GONE);
                    offlinePin.setVisibility(View.GONE);
                }
            }
        }
    }
}
