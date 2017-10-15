package cobaltix.internal_projects.swimtrackerapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Events.db";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DatabaseContract.Events.CREATE_TABLE_EVENTS);
        db.execSQL(DatabaseContract.WeeklyGoals.CREATE_TABLE_WEEKLY_GOALS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //discard the data and start over
        db.execSQL(DatabaseContract.Events.DELETE_TABLE_EVENTS);
        db.execSQL(DatabaseContract.WeeklyGoals.DELETE_TABLE_WEEKLY_EVENTS);

        onCreate(db);
    }


}
