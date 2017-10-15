package cobaltix.internal_projects.swimtrackerapp;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeeklyGoalsActivity extends AppCompatActivity
{
    private EditText etWeek;
    private EditText etWeeklyMiles;
    private EditText etLongest;
    private EditText etWeight;
    private EditText etDescription;

    private String sunday;
    private Event event;
    private WeeklyGoal weeklyGoal;

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etWeek = (EditText) findViewById(R.id.etWeek);
        etWeeklyMiles = (EditText) findViewById(R.id.etWeekMiles);
        etLongest = (EditText) findViewById(R.id.etLongest);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etDescription = (EditText) findViewById(R.id.etDescription);

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(year,monthOfYear,dayOfMonth);
                int dayOfWeek = myCalendar.get(Calendar.DAY_OF_WEEK);
                updateLabel(dayOfWeek);
            }

        };
        etWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        updateLabel(Calendar.DAY_OF_WEEK);
        retrieveWeekGoal();

        // Retrieve Event obj sent from Main Activity
        event = (Event) getIntent().getSerializableExtra("event");

        dbHelper = new DatabaseHelper(this);
    }

    private void updateLabel(int dayOfWeek) {
        if(dayOfWeek != Calendar.SUNDAY)
        {
            //Set the date to the Sunday of that week
            myCalendar.add(Calendar.DATE, -(dayOfWeek-1));
        }

        String sunFormat = "MMM dd";
        String satFormat = "MMM dd, yyyy";
        SimpleDateFormat sdfSun = new SimpleDateFormat(sunFormat);
        SimpleDateFormat sdfSat = new SimpleDateFormat(satFormat);

        sunday = sdfSun.format(myCalendar.getTime());

        //Move the calendar to the last day of the week (Sat)
        myCalendar.add(myCalendar.DATE,6);
        String sat = sdfSat.format(myCalendar.getTime());

        etWeek.setText(sunday+" - "+sat);
    }

    private WeeklyGoal retrieveWeekGoal() {
        db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.WeeklyGoals._ID,
                DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK,
                DatabaseContract.WeeklyGoals.COLUMN_NAME_MILES,
                DatabaseContract.WeeklyGoals.COLUMN_NAME_LONGEST,
                DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK,
                DatabaseContract.WeeklyGoals.COLUMN_NAME_DESCRIPTION
        };
        String selection = DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK + " = ?";
        String[] selectionArgs = { sunday };

        Cursor cursor = db.query(
                DatabaseContract.Events.TABLE_NAME,     // The table to query
                projection,                             // The columns to return
                selection,                              // The columns for the WHERE clause
                selectionArgs,                          // The values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                null                                    // The sort order
        );

        String s;
        while(cursor.moveToNext()) {
            System.out.println("-----------------> "+cursor.getInt(0));
//            s = cursor.getString(1);
//            if(s != null)
//            {
//                //TODO retrieve goal
//                return true;
//            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done)
        {
            double miles = Double.parseDouble(etWeeklyMiles.getText().toString());
            double longest = Double.parseDouble(etLongest.getText().toString());
            double weight = Double.parseDouble(etWeight.getText().toString());
            String description = etDescription.getText().toString();

            //Add values to database
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK, sunday);
            values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_MILES, miles);
            values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_LONGEST, longest);
            values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_WEIGHT, weight);
            values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_DESCRIPTION, description);
            values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_EVENT_ID, event.getId());

            long newRowId = db.insert(DatabaseContract.WeeklyGoals.TABLE_NAME, null, values);
            WeeklyGoal wg = new WeeklyGoal(newRowId, sunday, miles, longest, weight, description, event.getId());

        }

        return super.onOptionsItemSelected(item);
    }
}
