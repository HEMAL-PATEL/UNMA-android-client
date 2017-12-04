package com.paperplanes.unma.data.pendingtask;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abdularis on 04/12/17.
 */

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "UnmaAppTasks.db";
    public static final int DB_VERSION = 1;

    private static final String CREATE_TASK_TABLE =
            "CREATE TABLE " + TaskDatabaseContract.MarkReadTask.TABLE_NAME + "(" +
                    TaskDatabaseContract.MarkReadTask.ANNOUNCEMENT_ID + " TEXT PRIMARY KEY)";

    public TaskDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskDatabaseContract.MarkReadTask.TABLE_NAME);
        onCreate(db);
    }
}
