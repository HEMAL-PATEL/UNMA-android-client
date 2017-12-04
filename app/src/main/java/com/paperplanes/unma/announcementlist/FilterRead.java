package com.paperplanes.unma.announcementlist;

import com.paperplanes.unma.model.Announcement;

import io.reactivex.functions.Predicate;

/**
 * Created by abdularis on 01/12/17.
 */

public class FilterRead implements Predicate<Announcement> {
    @Override
    public boolean test(Announcement announcement) throws Exception {
        return announcement.isRead();
    }
}
