package com.paperplanes.unma.data.database;

import android.provider.BaseColumns;

/**
 * Created by abdularis on 22/11/17.
 */

public final class DatabaseContract {

    public static abstract class Announcement implements BaseColumns {
        public static final String TABLE_NAME = "announcements";

        public static final String TITLE = "title";
        public static final String PUBLISHER = "publisher";
        public static final String LAST_UPDATED = "last_updated";
        public static final String READ = "read";

        public static final String DESC_URL = "desc_url";
        public static final String DESC_CONTENT = "desc_content";
        public static final String DESC_SIZE = "desc_size";
        public static final String DESC_AVAILABLE_OFFLINE = "desc_offline";

        public static final String ATT_URL = "att_url";
        public static final String ATT_FILE_PATH = "att_file_path";
        public static final String ATT_FILE_NAME = "att_file_name";
        public static final String ATT_MIME = "att_mime";
        public static final String ATT_SIZE = "att_size";
    }

}
