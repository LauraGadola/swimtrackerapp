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
        db.execSQL(DatabaseContract.DailyLogs.CREATE_TABLE_DAILY_LOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //discard the data and start over
        db.execSQL(DatabaseContract.Events.DELETE_TABLE_EVENTS);
        db.execSQL(DatabaseContract.WeeklyGoals.DELETE_TABLE_WEEKLY_EVENTS);
        db.execSQL(DatabaseContract.DailyLogs.DELETE_TABLE_DAILY_GOALS);

        onCreate(db);
    }

    public boolean exportDatabase()
    {
        try {
            File sd = Environment.getExternalStorageDirectory();
            String db_path = "/data/data/cobaltix.internal_projects.swimtrackerapp/databases/";

            if (sd.canWrite()) {
                System.out.println("----------- sd can write");
                String backupDBPath = "events.db";
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

    public void exportToCSV(Event e)
    {
        File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (sd.canWrite())
        {
            String title = e.getTitle();
            if(e.isDone())
            {
                title = e.getTitle().substring(0, e.getTitle().indexOf("(")-1);
            }
            File file = new File(sd, title+".csv");
            try
            {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                SQLiteDatabase db = getReadableDatabase();

                String selectQuery = "SELECT " +
                        "dl." + DatabaseContract.DailyLogs.COLUMN_NAME_DATE + "," +
                        "dl." + DatabaseContract.DailyLogs.COLUMN_NAME_LOCATION + "," +
                        "dl." + DatabaseContract.DailyLogs.COLUMN_NAME_TEMP + "," +
                        "dl." + DatabaseContract.DailyLogs.COLUMN_NAME_HRS + "," +
                        "dl." + DatabaseContract.DailyLogs.COLUMN_NAME_MIN + "," +
                        "dl." + DatabaseContract.DailyLogs.COLUMN_NAME_WEIGHT + "," +
                        "dl." + DatabaseContract.DailyLogs.COLUMN_NAME_MILES + "," +
                        "dl." + DatabaseContract.DailyLogs.COLUMN_NAME_HONEST + "," +
                        "dl." + DatabaseContract.DailyLogs.COLUMN_NAME_NOTES + "," +
                        "wg." + DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK + "," +
                        "wg." + DatabaseContract.WeeklyGoals.COLUMN_NAME_MILES + "," +
                        "wg." + DatabaseContract.WeeklyGoals.COLUMN_NAME_LONGEST + "," +
                        "wg." + DatabaseContract.WeeklyGoals.COLUMN_NAME_WEIGHT + "," +
                        "wg." + DatabaseContract.WeeklyGoals.COLUMN_NAME_DESCRIPTION

                        + " FROM " + DatabaseContract.DailyLogs.TABLE_NAME + " AS dl"
                        + " JOIN " + DatabaseContract.WeeklyGoals.TABLE_NAME + " AS wg"
                        + " ON dl." + DatabaseContract.DailyLogs.COLUMN_NAME_WEEKLY_ID
                        + " = wg." + DatabaseContract.WeeklyGoals._ID

                        + " WHERE dl." + DatabaseContract.DailyLogs.COLUMN_NAME_EVENT_ID + " = "+ e.getId();

                Cursor curCSV = db.rawQuery(selectQuery, null);
                csvWrite.writeNext(curCSV.getColumnNames());
                System.out.println(selectQuery);
                while (curCSV.moveToNext())
                {
                    //Which column you want to export
                    String arrStr[] = {curCSV.getString(0), //date
                            curCSV.getString(1),    //location
                            curCSV.getString(2),    //temp
                            curCSV.getString(3),    //hours
                            curCSV.getString(4),    //min
                            curCSV.getString(5),    //weight
                            curCSV.getString(6),    //miles
                            curCSV.getString(7),    //honest
                            curCSV.getString(8),    //notes
                            curCSV.getString(9),   //week
                            curCSV.getString(10),   //miles goal
                            curCSV.getString(11),   //longest goal
                            curCSV.getString(12),   //weight goal
                            curCSV.getString(13),   //description
                    };
                    csvWrite.writeNext(arrStr);
                }

                csvWrite.close();
                curCSV.close();
                Toast.makeText(context, "CSV file has been exported into Downloads folder", Toast.LENGTH_SHORT).show();
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
        db = getWritableDatabase();

        db.delete(DatabaseContract.WeeklyGoals.TABLE_NAME, DatabaseContract.WeeklyGoals.COLUMN_NAME_EVENT_ID + " = " +e.getId(), null);
        db.delete(DatabaseContract.DailyLogs.TABLE_NAME, DatabaseContract.DailyLogs.COLUMN_NAME_EVENT_ID + " = " +e.getId(), null);
        db.delete(DatabaseContract.Events.TABLE_NAME, DatabaseContract.Events._ID + " = " +e.getId(), null);

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

    public int addDailyLog(DailyLog log)
    {
        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_DATE, formatToDB(log.getDate()));
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_LOCATION, log.getLocation());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_TEMP, log.getTemp());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_HRS, log.getHrs());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_MIN, log.getMin());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_WEIGHT, log.getWeight());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_MILES, log.getMiles());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_HONEST, log.getHonest());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_NOTES, log.getNotes());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_WEEKLY_ID, log.getWeekly_id());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_EVENT_ID, log.getEvent_id());

        int newRowId = (int) db.insert(DatabaseContract.DailyLogs.TABLE_NAME, null, values);
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

    public void updateDailyLog(DailyLog oldLog, DailyLog newLog)
    {
        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_LOCATION, newLog.getLocation());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_TEMP, newLog.getTemp());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_HRS, newLog.getHrs());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_MIN, newLog.getMin());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_WEIGHT, newLog.getWeight());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_MILES, newLog.getMiles());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_HONEST, newLog.getHonest());
        values.put(DatabaseContract.DailyLogs.COLUMN_NAME_NOTES, newLog.getNotes());

        db.update(DatabaseContract.DailyLogs.TABLE_NAME, values, DatabaseContract.DailyLogs._ID +"=" + oldLog.getId(), null);
        db.close();
        exportDatabase();
    }

    public void removeDailyLog(DailyLog currentLog)
    {
        db = getWritableDatabase();
        String query = "DELETE FROM "+ DatabaseContract.DailyLogs.TABLE_NAME +" WHERE "+ DatabaseContract.DailyLogs._ID +" = "+ currentLog.getId();
        db.execSQL(query);
        db.close();
        exportDatabase();
    }

    public WeeklyGoal getWeeklyGoal(String week, int event_id)
    {
        db = getReadableDatabase();

        //TODO Needed to check both week and event_id?
        String selectQuery = "SELECT * FROM " + DatabaseContract.WeeklyGoals.TABLE_NAME
                + " WHERE " + DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK + " = '" + week + "'"
                + " AND " + DatabaseContract.WeeklyGoals.COLUMN_NAME_EVENT_ID + " = " + event_id;
        Log.e("dbHelper", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            float miles = cursor.getFloat(2);
            float longest = cursor.getFloat(3);
            float weight = cursor.getFloat(4);
            String description = cursor.getString(5);
//            int event_id = cursor.getShort(6);
            WeeklyGoal weeklyGoal = new WeeklyGoal(id, week, miles, longest, weight, description, event_id);
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

    public LinkedList<DailyLog> getDailyLogList(int event_id)
    {
        db = getReadableDatabase();
        String query = "SELECT * FROM "+ DatabaseContract.DailyLogs.TABLE_NAME
                +" WHERE "+ DatabaseContract.DailyLogs.COLUMN_NAME_EVENT_ID +" = "+ event_id
                +" ORDER BY "+ DatabaseContract.DailyLogs.COLUMN_NAME_DATE;

        return queryForDailyLogs(query);
    }

    public LinkedList<DailyLog> getDailyLogList(String week, int event_id)
    {
        WeeklyGoal wg = getWeeklyGoal(week, event_id);
        LinkedList<DailyLog> dgList = new LinkedList<>();
        if(wg != null)
        {
            db = getReadableDatabase();
            String query = "SELECT * FROM " + DatabaseContract.DailyLogs.TABLE_NAME
                    + " WHERE " + DatabaseContract.DailyLogs.COLUMN_NAME_WEEKLY_ID + " = " + wg.getId()
                    + " ORDER BY " + DatabaseContract.DailyLogs.COLUMN_NAME_DATE + " DESC";
            dgList = queryForDailyLogs(query);
        }
        return dgList;
    }

    private LinkedList<DailyLog> queryForDailyLogs(String query)
    {
        Cursor cursor = db.rawQuery(query, null);
        LinkedList<DailyLog> dailyLogList = new LinkedList<>();
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

            DailyLog dg = new DailyLog(id, date, location, temp, hrs, min, weight, miles, honest, notes, weekly_id, event_id);
            dailyLogList.add(dg);
        }
        return dailyLogList;
    }
}
