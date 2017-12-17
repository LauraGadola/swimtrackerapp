package cobaltix.internal_projects.swimtrackerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class DatabaseHelper extends SQLiteOpenHelper
{
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy");
    SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy/MM/dd");


    public static final String DATABASE_NAME = "events.db";
    public static SQLiteDatabase db;
    private Context context;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
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

    //todo delete - To have an initial database
    public boolean importDatabase()
    {
        InputStream myInput = null;

        try {
            myInput = context.getAssets().open("databases/events.db");
            // Set the output file stream up:
            db = getReadableDatabase();
            OutputStream myOutput = new FileOutputStream(db.getPath());
            db.close();

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0)
            {
                myOutput.write(buffer, 0, length);
            }
            // Close and clear the streams

            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (FileNotFoundException e)
        {
            Log.e("Restoring Database", "file not found");
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            Log.e("Restoring Database", "IO exception");
            e.printStackTrace();
            return false;
        }
        Log.e("Restoring Database", "Restored");
        return true;
    }

    public static boolean isDatabaseExist(Context context)
    {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    //todo export the whole db - only exporting the events table now
    public void exportToCVS()
    {
        File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (sd.canWrite())
        {
            File file = new File(sd, "csvname.csv");
            try
            {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                SQLiteDatabase db = getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM " + DatabaseContract.Events.TABLE_NAME, null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while (curCSV.moveToNext())
                {
                    //Which column you want to exprort
                    String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2)};
                    csvWrite.writeNext(arrStr);
                }
                System.out.println("DONE CVS EXPORT!!!!!!");
                csvWrite.close();
                curCSV.close();
                Toast.makeText(context, "CVS file exported into Downloads folder", Toast.LENGTH_SHORT).show();
            } catch (Exception sqlEx)
            {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            }
        }
    }

    public Event addEvent(String title, String startDate, String eventDate)
    {
        db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Events.COLUMN_NAME_TITLE, title);
        values.put(DatabaseContract.Events.COLUMN_NAME_START_DATE, formatToDB(startDate));
        values.put(DatabaseContract.Events.COLUMN_NAME_EVENT_DATE, formatToDB(eventDate));

        // Insert the new row, returning the primary key value of the new row
        int newRowId = (int) db.insert(DatabaseContract.Events.TABLE_NAME, null, values);
        Event event = new Event(newRowId, title, startDate, eventDate);

        db.close();
        exportDatabase();

        return event;
    }

    public void updateEvent(Event event)
    {
        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Events.COLUMN_NAME_TITLE, event.getTitle());
        values.put(DatabaseContract.Events.COLUMN_NAME_START_DATE, formatToDB(event.getStartDate()));
        values.put(DatabaseContract.Events.COLUMN_NAME_EVENT_DATE, formatToDB(event.getEventDate()));

        db.update(DatabaseContract.Events.TABLE_NAME, values, DatabaseContract.Events._ID + "=" + event.getId(), null);
        db.close();
        exportDatabase();
    }

    public void removeEvent(Event e)
    {
        //// TODO: 12/13/17 Test!! 
        db = getWritableDatabase();

        db.delete(DatabaseContract.Events.TABLE_NAME, DatabaseContract.Events._ID + " = " +e.getId(), null);
        db.delete(DatabaseContract.WeeklyGoals.TABLE_NAME, DatabaseContract.WeeklyGoals.COLUMN_NAME_EVENT_ID + " = " +e.getId(), null);
        db.delete(DatabaseContract.DailyGoals.TABLE_NAME, DatabaseContract.DailyGoals.COLUMN_NAME_EVENT_ID + " = " +e.getId(), null);

//        String query = "DELETE FROM "+DatabaseContract.Events.TABLE_NAME + " WHERE "+DatabaseContract.Events._ID + " = " + e.getId();
//        db.execSQL(query);
//        query = "DELETE FROM "+DatabaseContract.WeeklyGoals.TABLE_NAME + " WHERE "+DatabaseContract.WeeklyGoals.COLUMN_NAME_EVENT_ID+ " = " + e.getId();
//        db.execSQL(query);
//        query = "DELETE FROM "+DatabaseContract.DailyGoals.TABLE_NAME + " WHERE "+DatabaseContract.DailyGoals.COLUMN_NAME_EVENT_ID + " = " + e.getId();
//        db.execSQL(query);

        db.close();
        exportDatabase();
    }

    public WeeklyGoal addWeeklyGoal(String weekStart, float miles, float longest, float weight, String description, int event_id)
    {
        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK, weekStart);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_MILES, miles);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_LONGEST, longest);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_WEIGHT, weight);
        values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_DESCRIPTION, description);

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

    public WeeklyGoal getWeeklyGoal(String week)
    {
        db = getReadableDatabase();

        //TODO Needed to check both week and event_id?
        String selectQuery = "SELECT * FROM " + DatabaseContract.WeeklyGoals.TABLE_NAME
                + " WHERE " + DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK + " = '" + week + "'";
        Log.e("dbHelper", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            float miles = cursor.getFloat(2);
            float longest = cursor.getFloat(3);
            float weight = cursor.getFloat(4);
            String description = cursor.getString(5);
            int event_id = cursor.getShort(6);
            WeeklyGoal weeklyGoal = new WeeklyGoal(id, week, miles, longest, weight, description, event_id);
            return weeklyGoal;
        }
        return null;
    }

    public WeeklyGoal getWeeklyGoal(int id)
    {
        db = getReadableDatabase();

        //TODO Needed to check both week and event_id?
        String selectQuery = "SELECT * FROM " + DatabaseContract.WeeklyGoals.TABLE_NAME
                + " WHERE " + DatabaseContract.WeeklyGoals._ID + " = " + id;
        Log.e("dbHelper", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        while(cursor.moveToNext()) {
            String week = cursor.getString(1);
            float miles = cursor.getFloat(2);
            float longest = cursor.getFloat(3);
            float weight = cursor.getFloat(4);
            String description = cursor.getString(5);
            int event_id = cursor.getShort(6);
            WeeklyGoal weeklyGoal = new WeeklyGoal(id, week, miles, longest, weight, description, event_id);
            System.out.println("dbHelper: WG: "+weeklyGoal);
            return weeklyGoal;
        }
        return null;
    }

    public ArrayList<Event> getEventList()
    {
        db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DatabaseContract.Events.TABLE_NAME
                + " ORDER BY " + DatabaseContract.Events.COLUMN_NAME_EVENT_DATE + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Event> events = new ArrayList<>();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String startDate = formatFromDB(cursor.getString(2));
            String eventDate = formatFromDB(cursor.getString(3));
            Event event = new Event(id, title, startDate, eventDate);
            System.out.println("dbHelper: Event: "+event);
            events.add(event);
        }
        cursor.close();
        return events;
    }

    public LinkedList<DailyGoal> getDailyGoalList(int event_id)
    {
        db = getReadableDatabase();
        String query = "SELECT * FROM "+ DatabaseContract.DailyGoals.TABLE_NAME
                +" WHERE "+ DatabaseContract.DailyGoals.COLUMN_NAME_EVENT_ID +" = "+ event_id
                +" ORDER BY "+ DatabaseContract.DailyGoals.COLUMN_NAME_DATE;

        return queryForDGList(query);
    }

    public LinkedList<DailyGoal> getDailyGoalList(String week)
    {
        WeeklyGoal wg = getWeeklyGoal(week);
        LinkedList<DailyGoal> dgList = new LinkedList<>();
        if(wg != null)
        {
            db = getReadableDatabase();
            String query = "SELECT * FROM " + DatabaseContract.DailyGoals.TABLE_NAME
                    + " WHERE " + DatabaseContract.DailyGoals.COLUMN_NAME_WEEKLY_ID + " = " + wg.getId()
                    + " ORDER BY " + DatabaseContract.DailyGoals.COLUMN_NAME_DATE + " DESC";
            dgList = queryForDGList(query);
        }
        return dgList;
    }

    private LinkedList<DailyGoal> queryForDGList(String query)
    {
        Cursor cursor = db.rawQuery(query, null);
        LinkedList<DailyGoal> dailyGoalList = new LinkedList<>();
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
            float honest = cursor.getFloat(8);
            String notes = cursor.getString(9);
            int weekly_id = cursor.getInt(10);
            int event_id = cursor.getInt(11);

            DailyGoal dg = new DailyGoal(id, date, location, temp, hrs, min, weight, miles, honest, notes, weekly_id, event_id);
            dailyGoalList.add(dg);
        }
        return dailyGoalList;
    }
}
