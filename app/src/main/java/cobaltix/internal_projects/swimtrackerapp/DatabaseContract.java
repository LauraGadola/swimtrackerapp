package cobaltix.internal_projects.swimtrackerapp;

import android.provider.BaseColumns;

public final class DatabaseContract
{

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static class Events implements BaseColumns
    {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "date";

        public static final String CREATE_TABLE_EVENTS =
                "CREATE TABLE " + Events.TABLE_NAME + " (" +
                        Events._ID + " INTEGER PRIMARY KEY," +
                        Events.COLUMN_NAME_TITLE + " TEXT," +
                        Events.COLUMN_NAME_SUBTITLE + " TEXT)";

        public static final String DELETE_TABLE_EVENTS =
                "DROP TABLE IF EXISTS " + Events.TABLE_NAME;
    }

}


