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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by abdularis on 23/11/17.
 */

@RunWith(AndroidJUnit4.class)
public class DatabaseAccessTest {

    DatabaseAccess dbAccess;
    DatabaseHelper dbHelper;

    @Before
    public void setup() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(DatabaseHelper.DB_NAME);
        dbHelper = new DatabaseHelper(InstrumentationRegistry.getTargetContext());
        dbAccess = new DatabaseAccess(dbHelper);
    }

    @After
    public void teardown() {
        dbHelper.close();
    }
//
//    private List<String> getAnnouncementIds() {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor c = db.rawQuery("SELECT id FROM " + DatabaseContract.Announcement.TABLE_NAME, null);
//        if (c == null) {
//            throw
//        }
//    }

    private List<Announcement> getTestData() throws MalformedURLException {
        List<Announcement> list = new ArrayList<>();
        list.add(new Announcement(
                "id-sd8fs9fhs9ds9fhs",
                "This is title",
                "http://localhost",
                null,
                "pub",
                new Date(),
                null,
                false
        ));
        list.add(new Announcement(
                "id-a9s8dfuasksjdf98d",
                "This is title",
                "http://localhost",
                new Description("http://localhost", "content", 100),
                "pub",
                new Date(),
                new Attachment("http://localhost", "path/home/file.pdf", "file.pdf", "app/pdf", 1000),
                false
        ));

        return list;
    }

    @Test
    public void insertAnnouncement_test() throws MalformedURLException {
        Announcement ann = getTestData().get(1);

        dbAccess.insert(ann);

        List<Announcement> res = dbAccess.getAnnouncements();
        assertThat(res.size(), is(1));
        assertTrue(res.get(0).getId().equals(ann.getId()));
        assertTrue(res.get(0).getDescription() != null);
        assertTrue(res.get(0).getAttachment() != null);
    }

}
