package com.paperplanes.unma.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Attachment;
import com.paperplanes.unma.model.Description;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by abdularis on 22/11/17.
 */

public class DatabaseAccess {

    private static ArrayList<String> sProjs = new ArrayList<>();

    // hah shit!! java ni rese, pusing ane
    private static String sProjsArray[];

    static {
        sProjs.add(DatabaseContract.Announcement._ID);
        sProjs.add(DatabaseContract.Announcement.TITLE);
        sProjs.add(DatabaseContract.Announcement.THUMBNAIL_URL);
        sProjs.add(DatabaseContract.Announcement.PUBLISHER);
        sProjs.add(DatabaseContract.Announcement.LAST_UPDATED);
        sProjs.add(DatabaseContract.Announcement.READ);

        sProjs.add(DatabaseContract.Announcement.DESC_URL);
//        sProjs.add(DatabaseContract.Announcement.DESC_CONTENT); // load later, when needed
        sProjs.add(DatabaseContract.Announcement.DESC_SIZE);

        sProjs.add(DatabaseContract.Announcement.ATT_URL);
        sProjs.add(DatabaseContract.Announcement.ATT_FILE_PATH);
        sProjs.add(DatabaseContract.Announcement.ATT_FILE_NAME);
        sProjs.add(DatabaseContract.Announcement.ATT_MIME);
        sProjs.add(DatabaseContract.Announcement.ATT_SIZE);

        sProjsArray = new String[sProjs.size()];
        sProjs.toArray(sProjsArray);
    }

    private DatabaseHelper mDbHelper;

    public DatabaseAccess(DatabaseHelper handler) {
        mDbHelper = handler;
    }

    private SQLiteDatabase getReadableDatabase() {
        return mDbHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWriteableDatabase() {
        return mDbHelper.getWritableDatabase();
    }

    private static Announcement fromCurrentCursor(Cursor c) throws MalformedURLException {
        Attachment attachment = null;
        String attUrl = c.getString(sProjs.indexOf(DatabaseContract.Announcement.ATT_URL));
        if (attUrl != null && !attUrl.isEmpty()) {
            attachment = new Attachment(
                    attUrl,
                    c.getString(sProjs.indexOf(DatabaseContract.Announcement.ATT_FILE_PATH)),
                    c.getString(sProjs.indexOf(DatabaseContract.Announcement.ATT_FILE_NAME)),
                    c.getString(sProjs.indexOf(DatabaseContract.Announcement.ATT_MIME)),
                    c.getLong(sProjs.indexOf(DatabaseContract.Announcement.ATT_SIZE))
            );
            if (attachment.getFilePath() != null && !attachment.getFilePath().isEmpty()) {
                attachment.setState(Attachment.STATE_OFFLINE);
            } else {
                attachment.setState(Attachment.STATE_ONLINE);
            }
        }

        Description description = null;
        String descUrl = c.getString(sProjs.indexOf(DatabaseContract.Announcement.DESC_URL));
        if (descUrl != null && !descUrl.isEmpty()) {
            description = new Description(
                    descUrl,
                    null,
                    c.getLong(sProjs.indexOf(DatabaseContract.Announcement.DESC_SIZE))
            );
        }

        return new Announcement(
                c.getString(sProjs.indexOf(DatabaseContract.Announcement._ID)),
                c.getString(sProjs.indexOf(DatabaseContract.Announcement.TITLE)),
                c.getString(sProjs.indexOf(DatabaseContract.Announcement.THUMBNAIL_URL)),
                description,
                c.getString(sProjs.indexOf(DatabaseContract.Announcement.PUBLISHER)),
                new Date(c.getLong(sProjs.indexOf(DatabaseContract.Announcement.LAST_UPDATED))),
                attachment,
                Boolean.valueOf(c.getString(sProjs.indexOf(DatabaseContract.Announcement.READ)))
        );
    }

    public Announcement getAnnouncement(@NonNull String id) throws MalformedURLException {
        String sel = DatabaseContract.Announcement._ID + "=?";
        String selArg[] = {id};

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DatabaseContract.Announcement.TABLE_NAME,
                sProjsArray,
                sel,
                selArg,
                null,
                null,
                null);
        if (c != null && c.moveToFirst()) {
            Announcement ann = fromCurrentCursor(c);
            c.close();
            return ann;
        }

        return null;
    }

    public List<Announcement> getAnnouncements() throws MalformedURLException {
        SQLiteDatabase db = getReadableDatabase();
        String orderBy = DatabaseContract.Announcement.LAST_UPDATED + " DESC";
        Cursor c = db.query(DatabaseContract.Announcement.TABLE_NAME,
                sProjsArray,
                null,
                null,
                null,
                null,
                orderBy);

        List<Announcement> announcementList = new ArrayList<>();
        if (c != null && c.moveToFirst()) {
            while (!c.isAfterLast()) {
                announcementList.add(fromCurrentCursor(c));
                c.moveToNext();
            }
            c.close();
        }

        return announcementList;
    }

    public void insert(@NonNull Announcement ann) {
        ContentValues vals = new ContentValues();
        vals.put(DatabaseContract.Announcement._ID, ann.getId());
        vals.put(DatabaseContract.Announcement.TITLE, ann.getTitle());
        vals.put(DatabaseContract.Announcement.THUMBNAIL_URL, ann.getThumbnailUrl());
        vals.put(DatabaseContract.Announcement.PUBLISHER, ann.getPublisher());
        vals.put(DatabaseContract.Announcement.LAST_UPDATED, ann.getLastUpdated().getTime());
        vals.put(DatabaseContract.Announcement.READ, Boolean.valueOf(ann.isRead()).toString());

        Description desc = ann.getDescription();
        if (desc != null) {
            vals.put(DatabaseContract.Announcement.DESC_URL, desc.getUrl());
            vals.put(DatabaseContract.Announcement.DESC_CONTENT, desc.getContent());
            vals.put(DatabaseContract.Announcement.DESC_SIZE, desc.getSize());
        }

        Attachment att = ann.getAttachment();
        if (att != null) {
            vals.put(DatabaseContract.Announcement.ATT_URL, att.getUrl());
            vals.put(DatabaseContract.Announcement.ATT_FILE_PATH, att.getFilePath());
            vals.put(DatabaseContract.Announcement.ATT_FILE_NAME, att.getName());
            vals.put(DatabaseContract.Announcement.ATT_MIME, att.getMimeType());
            vals.put(DatabaseContract.Announcement.ATT_SIZE, att.getSize());
        }

        SQLiteDatabase db = getWriteableDatabase();
        db.insert(DatabaseContract.Announcement.TABLE_NAME, null, vals);
    }

    public void insertAll(@NonNull List<Announcement> announcementList) {
        SQLiteDatabase db = getWriteableDatabase();
        db.beginTransaction();
        try {
            for (Announcement announcement : announcementList)
                insert(announcement);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void clearAll() {
        SQLiteDatabase db = getWriteableDatabase();
        db.delete(DatabaseContract.Announcement.TABLE_NAME, null, null);
    }

    public void insertOrReplaceAll(@NonNull List<Announcement> announcementList) {
        ContentValues vals = new ContentValues();
        SQLiteDatabase db = getWriteableDatabase();
        for (Announcement ann : announcementList) {
            vals.clear();
            vals.put(DatabaseContract.Announcement._ID, ann.getId());
            vals.put(DatabaseContract.Announcement.TITLE, ann.getTitle());
            vals.put(DatabaseContract.Announcement.THUMBNAIL_URL, ann.getThumbnailUrl());
            vals.put(DatabaseContract.Announcement.PUBLISHER, ann.getPublisher());
            vals.put(DatabaseContract.Announcement.LAST_UPDATED, ann.getLastUpdated().getTime());
            vals.put(DatabaseContract.Announcement.READ, Boolean.valueOf(ann.isRead()).toString());

            Description desc = ann.getDescription();
            if (desc != null) {
                vals.put(DatabaseContract.Announcement.DESC_URL, desc.getUrl());
                vals.put(DatabaseContract.Announcement.DESC_CONTENT, desc.getContent());
                vals.put(DatabaseContract.Announcement.DESC_SIZE, desc.getSize());
            }

            Attachment att = ann.getAttachment();
            if (att != null) {
                vals.put(DatabaseContract.Announcement.ATT_URL, att.getUrl());
                vals.put(DatabaseContract.Announcement.ATT_FILE_PATH, att.getFilePath());
                vals.put(DatabaseContract.Announcement.ATT_FILE_NAME, att.getName());
                vals.put(DatabaseContract.Announcement.ATT_MIME, att.getMimeType());
                vals.put(DatabaseContract.Announcement.ATT_SIZE, att.getSize());
            }

            db.replace(DatabaseContract.Announcement.TABLE_NAME, null, vals);
        }
    }

    public String getDescriptionContent(@NonNull String announcementId) {
        String cols[] = {DatabaseContract.Announcement.DESC_CONTENT};
        String sel = DatabaseContract.Announcement._ID + "=?";
        String selArg[] = {announcementId};

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DatabaseContract.Announcement.TABLE_NAME, cols, sel, selArg, null, null, null);
        String content = "";
        if (c != null && c.moveToFirst()) {
            content = c.getString(0);
            c.close();
        }

        return content;
    }

    public void updateDescriptionContent(@NonNull String announcementId, String descContent) {
        ContentValues vals = new ContentValues();
        vals.put(DatabaseContract.Announcement.DESC_CONTENT, descContent);

        String where = DatabaseContract.Announcement._ID + "=?";
        String whereArg[] = {announcementId};

        SQLiteDatabase db = getWriteableDatabase();
        db.update(DatabaseContract.Announcement.TABLE_NAME, vals, where, whereArg);
    }

    public void updateAttachmentFilePath(@NonNull String announcementId, String attFilePath) {
        ContentValues vals = new ContentValues();
        vals.put(DatabaseContract.Announcement.ATT_FILE_PATH, attFilePath);

        String where = DatabaseContract.Announcement._ID + "=?";
        String whereArg[] = {announcementId};

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.update(DatabaseContract.Announcement.TABLE_NAME, vals, where, whereArg);
    }

    public void updateAnnouncementAsRead(@NonNull String announcementId) {
        ContentValues vals = new ContentValues();
        vals.put(DatabaseContract.Announcement.READ, Boolean.valueOf(true).toString());

        String where = DatabaseContract.Announcement._ID + "=?";
        String whereArg[] = {announcementId};

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.update(DatabaseContract.Announcement.TABLE_NAME, vals, where, whereArg);
    }

}
