package com.paperplanes.unma.data.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Attachment;
import com.paperplanes.unma.model.Description;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
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

//    private int queary_announcement_count() {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor c = db.rawQuery("SELECT id FROM " + DatabaseContract.Announcement.TABLE_NAME, null);
//        if (c == null) {
//            return -1;
//        }
//        return c.getCount();
//    }

    private List<Announcement> getTestData() throws MalformedURLException {
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

        String updated = dbAccess.getDescriptionContent("id1");

        assertNotEquals("desc content", updated);
        assertEquals("update", updated);
    }

    @Test
    public void getAnnouncement_test() throws MalformedURLException {
        reloadDatabase();
        insert_raw_test_data("id_data");

        Announcement res = dbAccess.getAnnouncement("id_data");
        assertTrue(res.getId().equals("id_data"));
        assertEquals("title", res.getTitle());
        assertEquals(1500000, res.getLastUpdated().getTime());
        assertTrue(res.isRead());

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

        Announcement ann = getTestData().get(1);

        dbAccess.insert(ann);

        List<Announcement> res = dbAccess.getAnnouncements();
        assertThat(res.size(), is(1));
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
                        "'title', 'publisher', 1500000, 'true', " +
                        "'desc url', 'desc content', 500, 'true', " +
                        "'att url', 'att path', 'att fname', 'mime', 1024)"
        );
    }

}
