package cobaltix.internal_projects.swimtrackerapp;

import android.provider.BaseColumns;

public final class DatabaseContract
{
    // private constructor to prevent someone from accidentally instantiating the contract class
    private DatabaseContract() {}

    /* Inner class that defines the table contents */

    //EVENTS TABLE
    public static class Events implements BaseColumns
    {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_START_DATE = "start_date";
        public static final String COLUMN_NAME_EVENT_DATE = "event_date";

        public static final String CREATE_TABLE_EVENTS =
                "CREATE TABLE " + Events.TABLE_NAME + " ("
                        + Events._ID + " INTEGER PRIMARY KEY,"
                        + Events.COLUMN_NAME_TITLE + " TEXT,"
                        + Events.COLUMN_NAME_START_DATE + " TEXT,"
                        + Events.COLUMN_NAME_EVENT_DATE + " TEXT);";

        public static final String DELETE_TABLE_EVENTS =
                "DROP TABLE IF EXISTS " + Events.TABLE_NAME;
    }

    //WEEKLY GOALS TABLE
    public static class WeeklyGoals implements BaseColumns
    {
        public static final String TABLE_NAME = "weekly_goals";
        public static final String COLUMN_NAME_WEEK = "week";
        public static final String COLUMN_NAME_MILES = "miles";
        public static final String COLUMN_NAME_LONGEST = "longest";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_EVENT_ID = "event_id";

        public static final String CREATE_TABLE_WEEKLY_GOALS =
                "CREATE TABLE " + WeeklyGoals.TABLE_NAME + " ("
                        + WeeklyGoals._ID + " INTEGER PRIMARY KEY,"
                        + WeeklyGoals.COLUMN_NAME_WEEK + " TEXT,"
                        + WeeklyGoals.COLUMN_NAME_MILES + " FLOAT,"
                        + WeeklyGoals.COLUMN_NAME_LONGEST + " FLOAT,"
                        + WeeklyGoals.COLUMN_NAME_WEIGHT + " FLOAT,"
                        + WeeklyGoals.COLUMN_NAME_DESCRIPTION + " TEXT,"
                        + WeeklyGoals.COLUMN_NAME_EVENT_ID + " INTEGER,"
                        + "FOREIGN KEY(" + COLUMN_NAME_EVENT_ID + ") REFERENCES " + Events.TABLE_NAME + "(" + Events._ID + "));";

        public static final String DELETE_TABLE_WEEKLY_EVENTS =
                "DROP TABLE IF EXIST " + WeeklyGoals.TABLE_NAME;
    }

    //DAILY GOALS TABLE
    public static class DailyLogs implements BaseColumns
    {
        public static final String TABLE_NAME = "daily_logs";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_TEMP = "temperature";
        public static final String COLUMN_NAME_HRS = "hours";
        public static final String COLUMN_NAME_MIN = "minutes";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_MILES = "miles";
        public static final String COLUMN_NAME_HONEST = "honest";
        public static final String COLUMN_NAME_NOTES = "notes";
        public static final String COLUMN_NAME_WEEKLY_ID = "weekly_id";
        public static final String COLUMN_NAME_EVENT_ID = "event_id";

        public static final String CREATE_TABLE_DAILY_LOGS =
                "CREATE TABLE " + DailyLogs.TABLE_NAME + " ("
                        + DailyLogs._ID + " INTEGER PRIMARY KEY,"
                        + DailyLogs.COLUMN_NAME_DATE + " TEXT,"
                        + DailyLogs.COLUMN_NAME_LOCATION + " TEXT,"
                        + DailyLogs.COLUMN_NAME_TEMP + " FLOAT,"
                        + DailyLogs.COLUMN_NAME_HRS + " INTEGER,"
                        + DailyLogs.COLUMN_NAME_MIN + " INTEGER,"
                        + DailyLogs.COLUMN_NAME_WEIGHT + " FLOAT,"
                        + DailyLogs.COLUMN_NAME_MILES + " FLOAT,"
                        + DailyLogs.COLUMN_NAME_HONEST + " FLOAT,"
                        + DailyLogs.COLUMN_NAME_NOTES + " TEXT,"
                        + DailyLogs.COLUMN_NAME_WEEKLY_ID + " INTEGER,"
                        + DailyLogs.COLUMN_NAME_EVENT_ID + " INTEGER,"
                        + "FOREIGN KEY(" + COLUMN_NAME_EVENT_ID + ") REFERENCES " + Events.TABLE_NAME + "(" + Events._ID + ")"
                        + "FOREIGN KEY(" + COLUMN_NAME_WEEKLY_ID + ") REFERENCES " + WeeklyGoals.TABLE_NAME + "(" + WeeklyGoals._ID + "));";

        public static final String DELETE_TABLE_DAILY_GOALS =
                "DROP TABLE IF EXISTS " + DailyLogs.TABLE_NAME;

    }
}


