package com.paperplanes.unma.announcementmedialist;

import com.paperplanes.unma.model.Announcement;

import io.reactivex.functions.Predicate;

/**
 * Created by abdularis on 01/12/17.
 *
 * Filter hanya untuk Announcement yang memiliki Attachment
 */

public class FilterAttachment implements Predicate<Announcement> {
    @Override
    public boolean test(Announcement announcement) throws Exception {
        return announcement.getAttachment() != null;
    }
}
