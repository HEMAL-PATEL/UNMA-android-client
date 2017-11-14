package com.paperplanes.udas.announcementlist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paperplanes.udas.R;
import com.paperplanes.udas.model.Announcement;
import com.paperplanes.udas.model.Attachment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

/**
 * Created by abdularis on 13/11/17.
 */

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private List<Announcement> mAnnouncements;

    public AnnouncementAdapter(@NonNull List<Announcement> announcementList) {
        mAnnouncements = announcementList;
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
        viewHolder.title.setText(ann.getTitle());
        viewHolder.publisher.setText(ann.getPublisher());
//        viewHolder.date.setText(ann.getLastUpdated().toString());
        SimpleDateFormat dt = new SimpleDateFormat("d MMM yyyy", Locale.US);
        viewHolder.date.setText(dt.format(ann.getLastUpdated()));

        Attachment attach = ann.getAttachment();
        if (attach != null) {
            viewHolder.attachmentLayout.setVisibility(View.VISIBLE);
            viewHolder.attachmentFilename.setText(attach.getName());
        }
        else {
            viewHolder.attachmentLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mAnnouncements.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.anc_title) TextView title;
        @BindView(R.id.publisher) TextView publisher;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.attachment_layout) LinearLayout attachmentLayout;
        @BindView(R.id.attachment_filename) TextView attachmentFilename;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
