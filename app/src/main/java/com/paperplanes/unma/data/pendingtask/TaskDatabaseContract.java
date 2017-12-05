package com.paperplanes.unma.data.pendingtask;


/**
 * Created by abdularis on 04/12/17.
 *
 * Database contract class for pending task
 */

class TaskDatabaseContract {

    static abstract class MarkReadTask {
        static final String TABLE_NAME = "announcement_tasks";
        static final String ANNOUNCEMENT_ID = "announcement_id";
    }

}
