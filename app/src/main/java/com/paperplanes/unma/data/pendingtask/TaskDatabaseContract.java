package com.paperplanes.unma.data.pendingtask;

import android.provider.BaseColumns;

/**
 * Created by abdularis on 04/12/17.
 */

class TaskDatabaseContract {

    static abstract class MarkReadTask {
        static final String TABLE_NAME = "announcement_tasks";
        static final String ANNOUNCEMENT_ID = "announcement_id";
    }

}
