package cobaltix.internal_projects.swimtrackerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Events.db";
    public static SQLiteDatabase db;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DatabaseContract.Events.CREATE_TABLE_EVENTS);
        db.execSQL(DatabaseContract.WeeklyGoals.CREATE_TABLE_WEEKLY_GOALS);
        db.execSQL(DatabaseContract.DailyGoals.CREATE_TABLE_DAILY_GOALS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //discard the data and start over
        db.execSQL(DatabaseContract.Events.DELETE_TABLE_EVENTS);
        db.execSQL(DatabaseContract.WeeklyGoals.DELETE_TABLE_WEEKLY_EVENTS);
        db.execSQL(DatabaseContract.DailyGoals.DELETE_TABLE_DAILY_GOALS);

        onCreate(db);
    }

    public boolean exportDatabase()
    {
        try {
            File sd = Environment.getExternalStorageDirectory();
            String db_path = "/data/data/cobaltix.internal_projects.swimtrackerapp/databases/";

            if (sd.canWrite()) {
                System.out.println("----------- sd can write");
                String backupDBPath = "backupname.db";
                File currentDB = new File(db_path, DATABASE_NAME);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    System.out.println("---------- currentdb exists");
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    return true;
                }
            }
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
        return false;
    }

    public Event addEvent(String title, String date)
    {
        db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Events.COLUMN_NAME_TITLE, title);
        values.put(DatabaseContract.Events.COLUMN_NAME_DATE, date);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseContract.Events.TABLE_NAME, null, values);

        //TODO delete
        System.out.println("----------- id: "+newRowId);
        Event event = new Event(newRowId, title, date);

        db.close();
        exportDatabase();

        return event;
    }

    public WeeklyGoal addWeeklyGoal(String week, float miles, float longest, float weight, String description, long event_id)
    {
        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK, week);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_MILES, miles);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_LONGEST, longest);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_WEIGHT, weight);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_DESCRIPTION, description);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_EVENT_ID, event_id);

            //TODO delete
            System.out.println("-------- Creating new wg");
            long newRowId = db.insert(DatabaseContract.WeeklyGoals.TABLE_NAME, null, values);
            WeeklyGoal wg = new WeeklyGoal(newRowId, week, miles, longest, weight, description, event_id);

        db.close();
        exportDatabase();

        return wg;
    }

    public void updateWeeklyGoal(WeeklyGoal wg, float miles, float longest, float weight, String description)
    {
        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_MILES, miles);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_LONGEST, longest);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_WEIGHT, weight);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_DESCRIPTION, description);

        //TODO delete
        System.out.println("--------- Updating existing wg");
        db.update(DatabaseContract.WeeklyGoals.TABLE_NAME, values, "_id=" + wg.getId(), null);

        db.close();
        exportDatabase();
    }


    public WeeklyGoal weekGoalExist(String week, long event_id)
    {
        db = getReadableDatabase();

        //TODO Needed to check both week and event_id?
        String selectQuery = "SELECT * FROM " + DatabaseContract.WeeklyGoals.TABLE_NAME
                + " WHERE " + DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK + " = '" + week + "'"
                + " AND " + DatabaseContract.WeeklyGoals.COLUMN_NAME_EVENT_ID + " = " + event_id;

        Cursor cursor = db.rawQuery(selectQuery, null);


//        String[] projection = {
//                DatabaseContract.WeeklyGoals._ID,
//                DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK,
//                DatabaseContract.WeeklyGoals.COLUMN_NAME_MILES,
//                DatabaseContract.WeeklyGoals.COLUMN_NAME_LONGEST,
//                DatabaseContract.WeeklyGoals.COLUMN_NAME_WEIGHT,
//                DatabaseContract.WeeklyGoals.COLUMN_NAME_DESCRIPTION
//        };
//        String selection = DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK + " = ?";
//        String[] selectionArgs = { sunday };
//
//        Cursor cursor = db.query(
//                DatabaseContract.WeeklyGoals.TABLE_NAME,    // The table to query
//                projection,                                 // The columns to return
//                selection,                                  // The columns for the WHERE clause
//                selectionArgs,                              // The values for the WHERE clause
//                null,                                       // don't group the rows
//                null,                                       // don't filter by row groups
//                null                                        // The sort order
//        );

        while(cursor.moveToNext()) {
            long id = cursor.getLong(0);
            float miles = cursor.getFloat(2);
            float longest = cursor.getFloat(3);
            float weight = cursor.getFloat(4);
            String description = cursor.getString(5);
            WeeklyGoal weeklyGoal = new WeeklyGoal(id, week, miles, longest, weight, description, event_id);
            return weeklyGoal;
        }
        return null;
    }

    public ArrayList<Event> getEventList()
    {
        db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DatabaseContract.Events.TABLE_NAME + " ORDER BY " + DatabaseContract.Events._ID + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Event> events = new ArrayList<>();
        while(cursor.moveToNext()) {
            long id = cursor.getInt(0);
            String title = cursor.getString(1);
            String date = cursor.getString(2);
            Event event = new Event(id, title, date);
            events.add(event);
        }
        cursor.close();
        return events;
    }


}
