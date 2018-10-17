package com.kl.data;

import android.provider.BaseColumns;

public class DatabaseDefinition {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "kl.db";

    //public DatabaseDefinition() {}
    private final static String CREATE_TABLE = "CREATE TABLE ";
    private final static String T_INTEGER = " INTEGER ";
    private final static String T_FLOAT = " FLOAT ";
    private final static String T_VARCHAR = " VARCHAR ";
    private final static String T_BOOLEAN = " BOOLEAN ";
    private final static String T_TEXT = " TEXT ";
    private final static String T_REAL = " REAL ";
    private final static String T_PRIMARY_KEY = " PRIMARY KEY ";
    private final static String T_DEFAULT = " DEFAULT ";
    private final static String T_AUTOINCREMENT = " AUTOINCREMENT ";

    /* Inner class that defines the table contents */
    public static abstract class RadioStation implements BaseColumns {
        public static final String TABLE_NAME = "RadioStationTB";
        //public static final String C_ID = "station_id";
        public static final String C_NAME               = "station_name";
        public static final String C_STREAMING_URL      = "station_streaming_url";
        public static final String C_SHARED_URL         = "station_shared_url";
        public static final String C_COVER_URL          = "station_cover_url";
        public static final String C_WEBSITE_URL        = "station_website_url";
        public static final String C_BUFFERING_QUALITY  = "station_buff_quality";
        public static final String C_IS_EDITABLE        = "station_is_editable";
        public static final String C_IS_FAVORITE        = "station_is_fav";

        public static String createTableSql() {
            return CREATE_TABLE + RadioStation.TABLE_NAME + " (" +
                    RadioStation._ID + T_TEXT + " PRIMARY KEY," +
                    RadioStation.C_NAME + T_TEXT + "," +

                    RadioStation.C_STREAMING_URL + T_TEXT + "," +
                    RadioStation.C_SHARED_URL + T_TEXT + "," +
                    RadioStation.C_COVER_URL + T_TEXT + "," +
                    RadioStation.C_WEBSITE_URL + T_TEXT + "," +
                    RadioStation.C_IS_FAVORITE + T_BOOLEAN + "," +
                    RadioStation.C_IS_EDITABLE + T_BOOLEAN + "," +
                    RadioStation.C_BUFFERING_QUALITY + T_TEXT +

                    " )";
        }

        public static String dropTableSql() {
            return "DROP TABLE IF EXISTS " + RadioStation.TABLE_NAME;
        }
    }

}
