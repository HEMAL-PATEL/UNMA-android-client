package com.paperplanes.udas.announcementlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paperplanes.udas.R;
import com.paperplanes.udas.model.Announcement;
import com.paperplanes.udas.model.Attachment;

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

    public AnnouncementAdapter(Context context, @NonNull List<Announcement> announcementList) {
        mAnnouncements = announcementList;
        mContext = context;
    }

    public void replaceAll(@NonNull List<Announcement> announcements) {
        mAnnouncements = announcements;
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

    public void setOnClickListener(OnItemClickListener clickListener) {
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
        @BindView(R.id.anc_title)
        TextView title;
        @BindView(R.id.publisher)
        TextView publisher;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.attachment_layout)
        LinearLayout attachmentLayout;
        @BindView(R.id.attachment_filename)
        TextView attachmentFilename;

        private Announcement mCurrentAnn;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            View.OnClickListener clickListener = v -> {
                callClickListener(v, mCurrentAnn);
            };

            itemView.setOnClickListener(clickListener);
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

            Attachment attach = ann.getAttachment();
            if (attach != null) {
                attachmentLayout.setVisibility(View.VISIBLE);
                attachmentFilename.setText(attach.getName());
            } else {
                attachmentLayout.setVisibility(View.GONE);
            }
        }
    }
}
