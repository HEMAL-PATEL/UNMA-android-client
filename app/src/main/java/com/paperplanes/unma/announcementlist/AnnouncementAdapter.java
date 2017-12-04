package com.paperplanes.unma.announcementlist;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paperplanes.unma.R;
import com.paperplanes.unma.model.Announcement;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

/**
 * Created by abdularis on 13/11/17.
 */

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private Context mContext;
    private List<Announcement> mAnnouncements;
    private OnItemClickListener mClickListener;

    AnnouncementAdapter(@NonNull Context context) {
        mAnnouncements = new ArrayList<>();
        mContext = context;
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
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_announcement, viewGroup, false);
        return new ViewHolder(view);
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

    void setOnClickListener(OnItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    private void callClickListener(View itemView, Announcement ann) {
        if (mClickListener != null) {
            mClickListener.onClick(itemView, ann);
        }
    }

    public interface OnItemClickListener {
        void onClick(View itemView, Announcement announcement);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.anc_title) TextView title;
        @BindView(R.id.publisher) TextView publisher;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.unread_indicator) ImageView unreadIndicator;
        @BindView(R.id.thumbnail) ImageView imageView;
        @BindView(R.id.attachment_indicator) ImageView attachIndicator;

        private Announcement mCurrentAnn;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> callClickListener(v, mCurrentAnn));
        }

        void bindData(Announcement ann) {
            mCurrentAnn = ann;

            title.setText(ann.getTitle());
            publisher.setText(ann.getPublisher());
            String tm = (String) DateUtils.getRelativeDateTimeString(mContext,
                    ann.getLastUpdated().getTime(),
                    DateUtils.DAY_IN_MILLIS,
                    DateUtils.DAY_IN_MILLIS,
                    DateUtils.FORMAT_24HOUR);
            date.setText(tm);

            if (ann.getThumbnailUrl() != null) {
                Glide.with(mContext)
                        .load(ann.getThumbnailUrl())
                        .error(R.drawable.ic_no_content)
                        .into(imageView);
            }
            else {
                imageView.setImageResource(R.drawable.ic_no_content);
            }

            if (ann.getAttachment() != null) {
                attachIndicator.setVisibility(View.VISIBLE);
            }
            else {
                attachIndicator.setVisibility(View.GONE);
            }

            if (ann.isRead()) {
                unreadIndicator.setVisibility(View.GONE);
                date.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
                title.setTypeface(null, Typeface.NORMAL);
            }
            else {
                unreadIndicator.setVisibility(View.VISIBLE);
                date.setTextColor(mContext.getResources().getColor(R.color.color_unread));
                title.setTypeface(null, Typeface.BOLD);
            }
        }
    }

//    class ViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.anc_title) TextView title;
//        @BindView(R.id.publisher) TextView publisher;
//        @BindView(R.id.date) TextView date;
//        @BindView(R.id.attachment_layout) LinearLayout attachmentLayout;
//        @BindView(R.id.attachment_filename) TextView attachmentFilename;
//        @BindView(R.id.file_type_image) ImageView fileTypeImage;
//        @BindView(R.id.unread_indicator) ImageView unreadIndicator;
//        @BindView(R.id.file_ext) TextView fileExt;
//        @BindView(R.id.file_size) TextView fileSize;
//        @BindView(R.id.thumbnail) RoundedImageView imageView;
//        @BindView(R.id.thumbnail_layout) FrameLayout thumbLayout;
//
//        private Announcement mCurrentAnn;
//
//        ViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//            itemView.setOnClickListener(v -> callClickListener(v, mCurrentAnn));
//        }
//
//        void bindData(Announcement ann) {
//            mCurrentAnn = ann;
//
//            title.setText(ann.getTitle());
//            publisher.setText(ann.getPublisher());
//            String tm = (String) DateUtils.getRelativeDateTimeString(mContext,
//                    ann.getLastUpdated().getTime(),
//                    DateUtils.DAY_IN_MILLIS,
//                    DateUtils.DAY_IN_MILLIS,
//                    DateUtils.FORMAT_24HOUR);
//            date.setText(tm);
//
//            if (ann.getThumbnailUrl() != null) {
//                Log.d("DownloadThumb", ann.getThumbnailUrl());
//                thumbLayout.setVisibility(View.VISIBLE);
//                Glide.with(mContext)
//                        .load(ann.getThumbnailUrl())
//                        .listener(new RequestListener<String, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                Log.d("AnnouncementAdapter", e.toString());
//                                thumbLayout.setVisibility(View.GONE);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                return false;
//                            }
//                        })
//                        .into(imageView);
//            }
//            else {
//                thumbLayout.setVisibility(View.GONE);
//            }
//
//            if (ann.isRead()) {
//                unreadIndicator.setVisibility(View.GONE);
//                date.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
//                title.setTypeface(null, Typeface.NORMAL);
//            }
//            else {
//                unreadIndicator.setVisibility(View.VISIBLE);
//                date.setTextColor(mContext.getResources().getColor(R.color.color_unread));
//                title.setTypeface(null, Typeface.BOLD);
//            }
//
//            Attachment attach = ann.getAttachment();
//            if (attach != null) {
//                attachmentLayout.setVisibility(View.VISIBLE);
//                attachmentFilename.setText(attach.getName());
//                fileTypeImage.setImageResource(FileUtil.getDrawableResourceForFileExt(attach.getName()));
//                fileSize.setText(FileUtil.getFormattedFileSize(attach.getSize()));
//                fileExt.setText(FileUtil.getFileExtension(attach.getName()));
//            } else {
//                attachmentLayout.setVisibility(View.GONE);
//            }
//        }
//    }
}
