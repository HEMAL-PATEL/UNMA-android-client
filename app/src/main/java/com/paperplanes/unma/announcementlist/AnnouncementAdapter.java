package com.paperplanes.unma.announcementlist;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.abdularis.dateview.DateBoxView;
import com.paperplanes.unma.R;
import com.paperplanes.unma.common.DateUtil;
import com.paperplanes.unma.common.FileUtil;
import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Attachment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private static final int ITEM_HEADER_READ_TODAY = 4;
    private static final int ITEM_DATA_TOP_HEADING = 5;

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

        Announcement topHeading = null;
        ArrayList<Object> unread = new ArrayList<>();
        ArrayList<Object> read = new ArrayList<>();
        ArrayList<Object> readToday = new ArrayList<>();

        Date todayDate = new Date();
        for (Announcement announcement : announcements) {
            if (announcement.isRead()) {
                if (DateUtil.isSameDay(todayDate, announcement.getLastUpdated()))
                    readToday.add(announcement);
                else
                    read.add(announcement);
            }
            else if (topHeading == null && DateUtil.isSameDay(todayDate, announcement.getLastUpdated())) {
                topHeading = announcement;
            }
            else {
                unread.add(announcement);
            }
        }

        String todayDateString = new SimpleDateFormat("dd MMM", Locale.US).format(todayDate);

        mDataList.clear();
        if (topHeading != null) {
            mDataList.add(new AnnouncementTopHeading(topHeading, todayDateString));
        }

        if (!unread.isEmpty()) {
            mDataList.add(new UnreadHeader(unread.size()));
            mDataList.addAll(unread);
        }

        if (!readToday.isEmpty()) {
            mDataList.add(new ReadTodayHeader(readToday.size(), todayDateString));
            mDataList.addAll(readToday);
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
            case ITEM_HEADER_READ_TODAY:
                view = inflater.inflate(R.layout.item_announcement_header_read_today, viewGroup, false);
                return new ViewHolderReadTodayHeader(view);
            case ITEM_DATA_TOP_HEADING:
                view = inflater.inflate(R.layout.item_announcement_top_heading, viewGroup, false);
                return new ViewHolderTopHeading(view);
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
            case ITEM_HEADER_READ_TODAY:
                ViewHolderReadTodayHeader viewHolderReadTodayHeader = (ViewHolderReadTodayHeader) viewHolder;
                viewHolderReadTodayHeader.bindData((ReadTodayHeader) mDataList.get(i));
                break;
            case ITEM_DATA_TOP_HEADING:
                ViewHolderTopHeading viewHolderTopHeading = (ViewHolderTopHeading) viewHolder;
                viewHolderTopHeading.bind((AnnouncementTopHeading) mDataList.get(i));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataList.get(position) instanceof Announcement)
            return ITEM_DATA;
        else if (mDataList.get(position) instanceof ReadTodayHeader)
            return ITEM_HEADER_READ_TODAY;
        else if (mDataList.get(position) instanceof ReadHeader)
            return ITEM_HEADER_READ;
        else if (mDataList.get(position) instanceof AnnouncementTopHeading)
            return ITEM_DATA_TOP_HEADING;
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

    class AnnouncementTopHeading {
        AnnouncementTopHeading(Announcement announcement, String date) {
            this.announcement = announcement;
            this.date = date;
        }
        String date;
        Announcement announcement;
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

    class ReadTodayHeader extends ReadHeader {
        ReadTodayHeader(int readCount, String date) {
            super(readCount);
            this.date = date;
        }
        String date;
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

    class ViewHolderReadTodayHeader extends RecyclerView.ViewHolder {
        @BindView(R.id.read_count) TextView readCount;
        @BindView(R.id.date) TextView date;
        ViewHolderReadTodayHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(ReadTodayHeader readTodayHeader) {
            readCount.setText(String.valueOf(readTodayHeader.readCount));
            date.setText(readTodayHeader.date);
        }
    }

    class ViewHolderTopHeading extends RecyclerView.ViewHolder {

        @BindView(R.id.anc_title) TextView title;
        @BindView(R.id.publisher) TextView publisher;
        @BindView(R.id.time) TextView time;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.attachment_layout) LinearLayout attachmentLayout;
        @BindView(R.id.file_type_image) ImageView attachmentFileIcon;
        @BindView(R.id.attachment_filename) TextView attachmentFilename;

        private AnnouncementTopHeading mCurrentAnn;

        ViewHolderTopHeading(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> callClickListener(v, mCurrentAnn.announcement));
        }

        void bind(AnnouncementTopHeading ann) {
            mCurrentAnn = ann;

            title.setText(ann.announcement.getTitle());
            publisher.setText(ann.announcement.getPublisher());
            time.setText(new SimpleDateFormat("HH:mm", Locale.US).format(ann.announcement.getLastUpdated()));
            date.setText(ann.date);

            Attachment attachment = ann.announcement.getAttachment();
            if (attachment != null) {
                attachmentLayout.setVisibility(View.VISIBLE);
                attachmentFileIcon.setImageResource(FileUtil.getDrawableResourceForFileExt(attachment.getName()));
                attachmentFilename.setText(attachment.getName());
            } else {
                attachmentLayout.setVisibility(View.GONE);
            }
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
