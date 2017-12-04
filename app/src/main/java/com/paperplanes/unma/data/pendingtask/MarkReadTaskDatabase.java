package com.paperplanes.unma.data.pendingtask;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdularis on 04/12/17.
 */

public class MarkReadTaskDatabase {

    private TaskDatabaseHelper mDbHelper;

    public MarkReadTaskDatabase(TaskDatabaseHelper helper) {
        mDbHelper = helper;
    }

    public void insert(MarkReadTask markReadTask) {
        ContentValues vals = new ContentValues();
        vals.put(TaskDatabaseContract.MarkReadTask.ANNOUNCEMENT_ID, markReadTask.getAnnouncementId());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert(TaskDatabaseContract.MarkReadTask.TABLE_NAME, null, vals);
    }

    public List<MarkReadTask> getAll() {
        String cols[] = {TaskDatabaseContract.MarkReadTask.ANNOUNCEMENT_ID};
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.query(TaskDatabaseContract.MarkReadTask.TABLE_NAME, cols, null, null, null, null, null);
        List<MarkReadTask> list = new ArrayList<>();
        if (c != null && c.moveToFirst()) {
            while (!c.isAfterLast()) {
                MarkReadTask task = new MarkReadTask(c.getString(0));
                list.add(task);
                c.moveToNext();
            }
            c.close();
        }

        return list;
    }

    public void clearAll() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TaskDatabaseContract.MarkReadTask.TABLE_NAME, null, null);
    }
}
