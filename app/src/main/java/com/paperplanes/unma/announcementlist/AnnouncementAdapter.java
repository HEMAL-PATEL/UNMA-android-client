package com.paperplanes.unma.announcementlist;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.abdularis.dateview.DateBoxView;
import com.paperplanes.unma.R;
import com.paperplanes.unma.common.FileUtil;
import com.paperplanes.unma.model.Announcement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import abdularis.github.com.materialcolorrandomizer.MaterialColorRandom;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

/**
 * Created by abdularis on 13/11/17.
 */

public class AnnouncementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_DATA = 1;
    private static final int ITEM_HEADER_UNREAD = 2;
    private static final int ITEM_HEADER_READ = 3;

    private Context mContext;
    private OnItemClickListener mClickListener;

    private List<Object> mDataList = new ArrayList<>();

    private MaterialColorRandom mColorRandom;

    AnnouncementAdapter(@NonNull Context context) {
        mContext = context;
        mColorRandom = MaterialColorRandom.getInstance(context);
    }

    void replaceAll(@NonNull List<Announcement> announcements) {
        if (announcements.isEmpty())
            return;

        ArrayList<Object> unread = new ArrayList<>();
        ArrayList<Object> read = new ArrayList<>();

        for (Announcement announcement : announcements) {
            if (announcement.isRead()) read.add(announcement);
            else unread.add(announcement);
        }

        mDataList.clear();
        if (!unread.isEmpty()) {
            mDataList.add(new UnreadHeader(unread.size()));
            mDataList.addAll(unread);
        }

        if (!read.isEmpty()) {
            mDataList.add(new ReadHeader(read.size()));
            mDataList.addAll(read);
        }

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view;
        switch (viewType) {
            case ITEM_HEADER_UNREAD:
                view = inflater.inflate(R.layout.item_announcement_header_unread, viewGroup, false);
                return new ViewHolderUnreadHeader(view);
            case ITEM_HEADER_READ:
                view = inflater.inflate(R.layout.item_announcement_header_read, viewGroup, false);
                return new ViewHolderReadHeader(view);
        }

        view = inflater.inflate(R.layout.item_announcement, viewGroup, false);
        return new ViewHolderAnnouncement(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {
            case ITEM_DATA:
                ViewHolderAnnouncement viewHolderAnnouncementAnnouncement = (ViewHolderAnnouncement) viewHolder;
                viewHolderAnnouncementAnnouncement.bindData((Announcement) mDataList.get(i));
                break;
            case ITEM_HEADER_UNREAD:
                ViewHolderUnreadHeader viewHolderUnread = (ViewHolderUnreadHeader) viewHolder;
                viewHolderUnread.bindData((UnreadHeader) mDataList.get(i));
                break;
            case ITEM_HEADER_READ:
                ViewHolderReadHeader viewHolderRead = (ViewHolderReadHeader) viewHolder;
                viewHolderRead.bindData((ReadHeader) mDataList.get(i));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataList.get(position) instanceof Announcement)
            return ITEM_DATA;
        else if (mDataList.get(position) instanceof ReadHeader)
            return ITEM_HEADER_READ;
        return ITEM_HEADER_UNREAD;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
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

    class UnreadHeader {
        UnreadHeader(int unreadCount) {
            this.unreadCount = unreadCount;
        }
        int unreadCount;
    }

    class ReadHeader {
        ReadHeader(int readCount) {
            this.readCount = readCount;
        }
        int readCount;
    }

    class ViewHolderUnreadHeader extends RecyclerView.ViewHolder {
        @BindView(R.id.unread_count) TextView unreadCount;
        ViewHolderUnreadHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(UnreadHeader unreadHeader) {
            unreadCount.setText(String.valueOf(unreadHeader.unreadCount));
        }
    }

    class ViewHolderReadHeader extends RecyclerView.ViewHolder {
        @BindView(R.id.read_count) TextView readCount;
        ViewHolderReadHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(ReadHeader readHeader) {
            readCount.setText(String.valueOf(readHeader.readCount));
        }
    }

    class ViewHolderAnnouncement extends RecyclerView.ViewHolder {
        private final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);
        private final SimpleDateFormat DATE_FORMAT_FOR_COLOR_ID = new SimpleDateFormat("dd-MM", Locale.US);

        @BindView(R.id.anc_title) TextView title;
        @BindView(R.id.publisher) TextView publisher;
        @BindView(R.id.date) TextView time;
        @BindView(R.id.attachment_indicator) ImageView attachIndicator;
        @BindView(R.id.date_box) DateBoxView dateBox;
        @BindView(R.id.description_summary) TextView descSummary;
        @BindView(R.id.description_avail_offline) ImageView descOffline;

        private Announcement mCurrentAnn;

        ViewHolderAnnouncement(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> callClickListener(v, mCurrentAnn));
        }

        void bindData(Announcement ann) {
            mCurrentAnn = ann;

            title.setText(ann.getTitle());
            publisher.setText(ann.getPublisher());
            dateBox.setDate(ann.getLastUpdated());
            time.setText(TIME_FORMAT.format(ann.getLastUpdated()));

            Resources res = mContext.getResources();

            if (ann.getDescription() != null) {
                String summary = res.getString(R.string.description) + " " +
                        FileUtil.getFormattedFileSize(ann.getDescription().getSize());
                descSummary.setText(summary);
                descSummary.setTextColor(res.getColor(R.color.sub_text));

                if (ann.getDescription().isOffline()) {
                    descOffline.setVisibility(View.VISIBLE);
                } else {
                    descOffline.setVisibility(View.GONE);
                }
            }
            else {
                descSummary.setText(R.string.no_description);
                descSummary.setTextColor(res.getColor(R.color.no_description_text));
                descOffline.setVisibility(View.GONE);
            }

            if (ann.getAttachment() != null) {
                attachIndicator.setVisibility(View.VISIBLE);
            }
            else {
                attachIndicator.setVisibility(View.GONE);
            }

            if (ann.isRead()) {
                time.setTextColor(res.getColor(android.R.color.darker_gray));
                time.setTypeface(null, Typeface.NORMAL);
                title.setTypeface(null, Typeface.NORMAL);
            }
            else {
                time.setTextColor(res.getColor(R.color.color_unread));
                time.setTypeface(null, Typeface.BOLD);
                title.setTypeface(null, Typeface.BOLD);
            }

            String date = DATE_FORMAT_FOR_COLOR_ID.format(ann.getLastUpdated());
            int color = mColorRandom.getRandomMaterialColor(date);
            dateBox.setDayBgColor(color);
            dateBox.setYearBgColor(MaterialColorRandom.getLightenedColor(color, 0.94f));
        }
    }

}
