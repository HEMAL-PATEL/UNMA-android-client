package com.paperplanes.unma.data.database;

import android.database.DatabaseUtils;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Attachment;
import com.paperplanes.unma.model.Description;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by abdularis on 23/11/17.
 */

@RunWith(AndroidJUnit4.class)
public class DatabaseAccessTest {

    DatabaseAccess dbAccess;
    DatabaseHelper dbHelper;

    private void reloadDatabase() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(DatabaseHelper.DB_NAME);
        dbHelper = new DatabaseHelper(InstrumentationRegistry.getTargetContext());
        dbAccess = new DatabaseAccess(dbHelper);
    }

    @After
    public void teardown() {
        dbHelper.close();
    }

    private List<Announcement> get_announcement_test_data() throws MalformedURLException {
        List<Announcement> list = new ArrayList<>();
        list.add(new Announcement(
                "id-sd8fs9fhs9ds9fhs",
                "This is title",
                null,
                "pub",
                new Date(),
                null,
                false
        ));
        list.add(new Announcement(
                "id-a9s8dfuasksjdf98d",
                "This is title",
                new Description("http://localhost", "content", 100, false),
                "pub",
                new Date(),
                new Attachment("http://localhost", "path/home/file.pdf", "file.pdf", "app/pdf", 1000),
                false
        ));

        return list;
    }

    @Test
    public void getDescContent_test() {
        reloadDatabase();
        insert_raw_test_data("id1");

        String content = dbAccess.getDescriptionContent("id1");

        assertEquals(content, "desc content");
    }

    @Test
    public void updateDescContent_test() {
        reloadDatabase();
        insert_raw_test_data("id1");

        dbAccess.updateDescriptionContent("id1", "update");

        String updated =
                DatabaseUtils.stringForQuery(
                        dbHelper.getReadableDatabase(),
                        "SELECT " + DatabaseContract.Announcement.DESC_CONTENT + " FROM " + DatabaseContract.Announcement.TABLE_NAME,
                        null
                );

        assertNotEquals("desc content", updated);
        assertEquals("update", updated);
    }

    @Test
    public void updateAttachmentFilePath_test() {
        reloadDatabase();
        insert_raw_test_data("id1");

        dbAccess.updateAttachmentFilePath("id1", "new/path");

        String path = DatabaseUtils.stringForQuery(dbHelper.getReadableDatabase(),
                "SELECT " + DatabaseContract.Announcement.ATT_FILE_PATH + " FROM " + DatabaseContract.Announcement.TABLE_NAME, null);

        assertEquals("new/path", path);
    }

    @Test
    public void updateAnnouncementAsRead_test() {
        reloadDatabase();
        insert_raw_test_data("id1");

        dbAccess.updateAnnouncementAsRead("id1");

        String res = DatabaseUtils.stringForQuery(dbHelper.getReadableDatabase(),
                "SELECT " + DatabaseContract.Announcement.READ + " FROM " + DatabaseContract.Announcement.TABLE_NAME,
                null);

        assertTrue(Boolean.valueOf(res));
    }

    @Test
    public void getAnnouncement_test() throws MalformedURLException {
        reloadDatabase();
        insert_raw_test_data("id_data");

        Announcement res = dbAccess.getAnnouncement("id_data");
        assertTrue(res.getId().equals("id_data"));
        assertEquals("title", res.getTitle());
        assertEquals(1500000, res.getLastUpdated().getTime());
        assertFalse(res.isRead());

        assertTrue(res.getDescription() != null);
        assertTrue(res.getAttachment() != null);
        assertEquals("desc url", res.getDescription().getUrl());
        assertEquals(null, res.getDescription().getContent());
        assertEquals(500, res.getDescription().getSize());
        assertTrue(res.getDescription().isOffline());

        assertEquals("att url", res.getAttachment().getUrl());
        assertEquals("att path", res.getAttachment().getFilePath());
        assertEquals("att fname", res.getAttachment().getName());
        assertEquals("mime", res.getAttachment().getMimeType());
        assertEquals(1024, res.getAttachment().getSize());
    }

    @Test
    public void getAnnouncements_test() throws MalformedURLException {
        reloadDatabase();
        insert_raw_test_data("id1");
        insert_raw_test_data("id2");

        List<Announcement> results = dbAccess.getAnnouncements();
        assertEquals(2, results.size());
    }

    @Test
    public void insertAnnouncement_test() throws MalformedURLException {
        reloadDatabase();

        Announcement ann = get_announcement_test_data().get(1);

        dbAccess.insert(ann);

        long count = DatabaseUtils.queryNumEntries(dbHelper.getReadableDatabase(), DatabaseContract.Announcement.TABLE_NAME);

        assertEquals(1, count);
    }

    @Test
    public void insertAll_test() throws MalformedURLException {
        reloadDatabase();

        dbAccess.insertAll(get_announcement_test_data());

        long count = DatabaseUtils.queryNumEntries(dbHelper.getReadableDatabase(), DatabaseContract.Announcement.TABLE_NAME);

        assertEquals(2, count);
    }

    @Test
    public void insertOrReplace_test() throws MalformedURLException {
        reloadDatabase();

        List<Announcement> list = new ArrayList<>();
        list.addAll(get_announcement_test_data());
        list.addAll(get_announcement_test_data());
        list.addAll(get_announcement_test_data());

        dbAccess.insertOrReplaceAll(list);

        long count = DatabaseUtils.queryNumEntries(dbHelper.getReadableDatabase(), DatabaseContract.Announcement.TABLE_NAME);

        assertEquals(2, count);
    }

    @Test
    public void clearAll_test() {
        reloadDatabase();
        insert_raw_test_data("id1");
        insert_raw_test_data("id2");

        dbAccess.clearAll();

        long count = DatabaseUtils.queryNumEntries(dbHelper.getReadableDatabase(), DatabaseContract.Announcement.TABLE_NAME);

        assertEquals(0, count);
    }

    private void insert_raw_test_data(String id) {
        dbHelper.getWritableDatabase().execSQL(
                "INSERT INTO " + DatabaseContract.Announcement.TABLE_NAME +
                        "(" + DatabaseContract.Announcement._ID + "," +
                        DatabaseContract.Announcement.TITLE + "," +
                        DatabaseContract.Announcement.PUBLISHER + "," +
                        DatabaseContract.Announcement.LAST_UPDATED + "," +
                        DatabaseContract.Announcement.READ + "," +
                        DatabaseContract.Announcement.DESC_URL + "," +
                        DatabaseContract.Announcement.DESC_CONTENT + "," +
                        DatabaseContract.Announcement.DESC_SIZE + "," +
                        DatabaseContract.Announcement.DESC_AVAILABLE_OFFLINE + "," +
                        DatabaseContract.Announcement.ATT_URL + "," +
                        DatabaseContract.Announcement.ATT_FILE_PATH + "," +
                        DatabaseContract.Announcement.ATT_FILE_NAME + "," +
                        DatabaseContract.Announcement.ATT_MIME + "," +
                        DatabaseContract.Announcement.ATT_SIZE +
                        ") " +
                        "VALUES (" +
                        "'" + id + "'" + "," +
                        "'title', 'publisher', 1500000, 'false', " +
                        "'desc url', 'desc content', 500, 'true', " +
                        "'att url', 'att path', 'att fname', 'mime', 1024)"
        );
    }

}
