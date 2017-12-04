package com.paperplanes.unma.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abdularis on 22/11/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "UnmaApp.db";
    public static final int DB_VERSION = 1;

    private static final String CREATE_ANNOUNCEMENT_TABLE =
            "CREATE TABLE " + DatabaseContract.Announcement.TABLE_NAME + "(" +
                    DatabaseContract.Announcement._ID + " TEXT PRIMARY KEY," +
                    DatabaseContract.Announcement.TITLE + " TEXT," +
                    DatabaseContract.Announcement.THUMBNAIL_URL + " TEXT," +
                    DatabaseContract.Announcement.PUBLISHER + " TEXT," +
                    DatabaseContract.Announcement.LAST_UPDATED + " INTEGER," +
                    DatabaseContract.Announcement.READ + " BOOLEAN," +

                    DatabaseContract.Announcement.DESC_URL + " TEXT," +
                    DatabaseContract.Announcement.DESC_CONTENT + " TEXT," +
                    DatabaseContract.Announcement.DESC_SIZE + " INTEGER," +

                    DatabaseContract.Announcement.ATT_URL + " TEXT," +
                    DatabaseContract.Announcement.ATT_FILE_PATH + " TEXT," +
                    DatabaseContract.Announcement.ATT_FILE_NAME + " TEXT," +
                    DatabaseContract.Announcement.ATT_MIME + " TEXT," +
                    DatabaseContract.Announcement.ATT_SIZE + " INTEGER" +
                    ")";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ANNOUNCEMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Announcement.TABLE_NAME);
        onCreate(db);
    }
}
