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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper
{
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy");
    SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy/MM/dd");


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
        values.put(DatabaseContract.Events.COLUMN_NAME_DATE, formatToDB(date));

        // Insert the new row, returning the primary key value of the new row
        int newRowId = (int) db.insert(DatabaseContract.Events.TABLE_NAME, null, values);
        Event event = new Event(newRowId, title, date);

        db.close();
        exportDatabase();

        return event;
    }

    public WeeklyGoal addWeeklyGoal(String weekStart, float miles, float longest, float weight, String description, int event_id)
    {
        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK_START, weekStart);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_MILES, miles);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_LONGEST, longest);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_WEIGHT, weight);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_DESCRIPTION, description);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_EVENT_ID, event_id);

        //TODO delete
        System.out.println("-------- Creating new wg");
        int newRowId = (int) db.insert(DatabaseContract.WeeklyGoals.TABLE_NAME, null, values);

        //TODO create it first and send it to this method
        WeeklyGoal wg = new WeeklyGoal(newRowId, weekStart, miles, longest, weight, description, event_id);

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
        db.update(DatabaseContract.WeeklyGoals.TABLE_NAME, values, DatabaseContract.WeeklyGoals._ID +"=" + wg.getId(), null);
        db.close();
        exportDatabase();
    }

    public int addDailyGoal(DailyGoal dg)
    {
        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_DATE, formatToDB(dg.getDate()));
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_LOCATION, dg.getLocation());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_TEMP, dg.getTemp());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_HRS, dg.getHrs());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_MIN, dg.getMin());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_WEIGHT, dg.getWeight());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_MILES, dg.getMiles());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_HONEST, dg.getHonest());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_NOTES, dg.getNotes());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_WEEKLY_ID, dg.getWeekly_id());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_EVENT_ID, dg.getEvent_id());

        int newRowId = (int) db.insert(DatabaseContract.DailyGoals.TABLE_NAME, null, values);
        db.close();
        exportDatabase();
        return newRowId;
    }

    private String formatToDB(String date)
    {
        Date d = null;
        try
        {
            d = sdf.parse(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        String dateDB = sdfDB.format(d);
        return dateDB;
    }

    private String formatFromDB(String date)
    {
        Date d = null;
        try
        {
            d = sdfDB.parse(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        String newDate = sdf.format(d);
        return newDate;
    }

    public void updateDailyGoal(DailyGoal oldDG, DailyGoal newDG)
    {
        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_LOCATION, newDG.getLocation());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_TEMP, newDG.getTemp());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_HRS, newDG.getHrs());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_MIN, newDG.getMin());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_WEIGHT, newDG.getWeight());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_MILES, newDG.getMiles());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_HONEST, newDG.getHonest());
        values.put(DatabaseContract.DailyGoals.COLUMN_NAME_NOTES, newDG.getNotes());

        db.update(DatabaseContract.DailyGoals.TABLE_NAME, values, DatabaseContract.DailyGoals._ID +"=" + oldDG.getId(), null);
        db.close();
        exportDatabase();
    }

    public void removeDailyGoal(DailyGoal currentDG)
    {
        db = getWritableDatabase();
        String query = "DELETE FROM "+ DatabaseContract.DailyGoals.TABLE_NAME +" WHERE "+ DatabaseContract.DailyGoals._ID +" = "+ currentDG.getId();
        db.execSQL(query);
        db.close();
        exportDatabase();
    }

    public WeeklyGoal getLastWeeklyGoal(int event_id)
    {
        db = getReadableDatabase();
        String query = "SELECT * FROM "+ DatabaseContract.WeeklyGoals.TABLE_NAME +" ORDER BY "
                + DatabaseContract.WeeklyGoals._ID +" DESC LIMIT 1";
        System.out.println(query);
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String weekStart = cursor.getString(1);
            float miles = cursor.getFloat(2);
            float longest = cursor.getFloat(3);
            float weight = cursor.getFloat(4);
            String description = cursor.getString(5);

            WeeklyGoal wg = new WeeklyGoal(id, weekStart, miles, longest, weight, description, event_id);
            return wg;
        }
        return null;
    }

    public WeeklyGoal weekGoalExist(String weekStart, int event_id)
    {
        db = getReadableDatabase();

        //TODO Needed to check both week and event_id?
        String selectQuery = "SELECT * FROM " + DatabaseContract.WeeklyGoals.TABLE_NAME
                + " WHERE " + DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK_START + " = '" + weekStart + "'"
                + " AND " + DatabaseContract.WeeklyGoals.COLUMN_NAME_EVENT_ID + " = " + event_id;

        Cursor cursor = db.rawQuery(selectQuery, null);
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            float miles = cursor.getFloat(2);
            float longest = cursor.getFloat(3);
            float weight = cursor.getFloat(4);
            String description = cursor.getString(5);
            WeeklyGoal weeklyGoal = new WeeklyGoal(id, weekStart, miles, longest, weight, description, event_id);
            return weeklyGoal;
        }
        return null;
    }

    public ArrayList<Event> getEventList()
    {
        db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DatabaseContract.Events.TABLE_NAME
                + " ORDER BY " + DatabaseContract.Events.COLUMN_NAME_DATE + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Event> events = new ArrayList<>();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String date = formatFromDB(cursor.getString(2));
            Event event = new Event(id, title, date);
            events.add(event);
        }
        cursor.close();
        return events;
    }

    public ArrayList<DailyGoal> getDailyGoalList(int event_id)
    {
        db = getReadableDatabase();
        String query = "SELECT * FROM "+ DatabaseContract.DailyGoals.TABLE_NAME
                +" WHERE "+ DatabaseContract.DailyGoals.COLUMN_NAME_EVENT_ID +" = "+ event_id
                +" ORDER BY "+DatabaseContract.DailyGoals.COLUMN_NAME_DATE;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<DailyGoal> dailyGoalList = new ArrayList<>();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String date = formatFromDB(cursor.getString(1));
            String location = cursor.getString(2);
            float temp = cursor.getFloat(3);
            int hrs = cursor.getInt(4);
            int min = cursor.getInt(5);
            float weight = cursor.getFloat(6);
            float miles = cursor.getFloat(7);

            //TODO change numbers below
            float honest = cursor.getFloat(9);
            String notes = cursor.getString(10);
            int weekly_id = cursor.getInt(11);

            DailyGoal dg = new DailyGoal(id, date, location, temp, hrs, min, weight, miles, honest, notes, weekly_id, event_id);
            dailyGoalList.add(dg);
        }
        return dailyGoalList;
    }
}
