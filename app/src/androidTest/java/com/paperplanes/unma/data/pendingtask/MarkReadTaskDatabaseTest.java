package com.paperplanes.unma.data.pendingtask;

import android.database.DatabaseUtils;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by abdularis on 21/01/18.
 */

@RunWith(AndroidJUnit4.class)
public class MarkReadTaskDatabaseTest {

    MarkReadTaskDatabase db;
    TaskDatabaseHelper dbHelper;

    private void reloadDatabase() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(TaskDatabaseHelper.DB_NAME);
        dbHelper = new TaskDatabaseHelper(InstrumentationRegistry.getTargetContext());
        db = new MarkReadTaskDatabase(dbHelper);
    }

    @After
    public void teardown() {
        dbHelper.close();
    }

    @Test
    public void getAll_test() {
        reloadDatabase();
        insert_raw_sample_data("id1");
        insert_raw_sample_data("id2");

        List<MarkReadTask> tasks = db.getAll();

        assertEquals(2, tasks.size());
        assertEquals("id1", tasks.get(0).getAnnouncementId());
        assertEquals("id2", tasks.get(1).getAnnouncementId());
    }

    @Test
    public void insert_test() {
        reloadDatabase();

        db.insert(new MarkReadTask("id1"));
        db.insert(new MarkReadTask("id2"));
        db.insert(new MarkReadTask("id3"));

        assertEquals(3, get_record_count());
    }

    @Test
    public void clear_test() {
        reloadDatabase();
        insert_raw_sample_data("id1");
        insert_raw_sample_data("id2");
        insert_raw_sample_data("id3");

        db.clearAll();

        assertEquals(0, get_record_count());
    }

    private long get_record_count() {
        return DatabaseUtils.queryNumEntries(dbHelper.getReadableDatabase(),
                TaskDatabaseContract.MarkReadTask.TABLE_NAME);
    }

    private void insert_raw_sample_data(String ann_id) {
        dbHelper.getWritableDatabase()
                .execSQL(
                        "INSERT INTO " + TaskDatabaseContract.MarkReadTask.TABLE_NAME +
                                "(" +
                                TaskDatabaseContract.MarkReadTask.ANNOUNCEMENT_ID + ") VALUES (" +
                                "'" + ann_id + "')"
                );
    }

}
