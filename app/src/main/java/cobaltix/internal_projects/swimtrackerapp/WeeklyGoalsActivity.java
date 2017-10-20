package cobaltix.internal_projects.swimtrackerapp;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;


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

        // Retrieve Event obj sent from Main Activity
        event = (Event) getIntent().getSerializableExtra("event");

        dbHelper = new DatabaseHelper(this);

        etWeek = (EditText) findViewById(R.id.etWeek);
        etWeeklyMiles = (EditText) findViewById(R.id.etWeekMiles);
        etLongest = (EditText) findViewById(R.id.etYards);
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
                clearFields();
                fillFields();
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

        updateLabel(myCalendar.get(Calendar.DAY_OF_WEEK));
        clearFields();
        fillFields();

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

    private boolean WeekGoalExist() {

        db = dbHelper.getReadableDatabase();
        boolean b = (db == null);
        System.out.println(b);

        String[] projection = {
                DatabaseContract.WeeklyGoals._ID,
                DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK,
                DatabaseContract.WeeklyGoals.COLUMN_NAME_MILES,
                DatabaseContract.WeeklyGoals.COLUMN_NAME_LONGEST,
                DatabaseContract.WeeklyGoals.COLUMN_NAME_WEIGHT,
                DatabaseContract.WeeklyGoals.COLUMN_NAME_DESCRIPTION
        };
        String selection = DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK + " = ?";
        String[] selectionArgs = { sunday };

        Cursor cursor = db.query(
                DatabaseContract.WeeklyGoals.TABLE_NAME,    // The table to query
                projection,                                 // The columns to return
                selection,                                  // The columns for the WHERE clause
                selectionArgs,                              // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null                                        // The sort order
        );

        while(cursor.moveToNext()) {
            long id = cursor.getInt(0);
            float miles = cursor.getFloat(2);
            float longest = cursor.getFloat(3);
            float weight = cursor.getFloat(4);
            String description = cursor.getString(5);
            weeklyGoal = new WeeklyGoal(id, sunday, miles, longest, weight, description, event.getId());
            return true;
        }
        return false;
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
        //TODO in case the goal existed update the entry in the database. DON'T create a new one

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done)
        {
            float miles = Float.parseFloat(etWeeklyMiles.getText().toString());
            float longest = Float.parseFloat(etLongest.getText().toString());
            float weight = Float.parseFloat(etWeight.getText().toString());
            String description = etDescription.getText().toString();

            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_WEEK, sunday);
            values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_MILES, miles);
            values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_LONGEST, longest);
            values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_WEIGHT, weight);
            values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_DESCRIPTION, description);
            values.put(DatabaseContract.WeeklyGoals.COLUMN_NAME_EVENT_ID, event.getId());

            if(!WeekGoalExist())
            {
                long newRowId = db.insert(DatabaseContract.WeeklyGoals.TABLE_NAME, null, values);

                //TODO Do I need it - should I have a list?
                WeeklyGoal wg = new WeeklyGoal(newRowId, sunday, miles, longest, weight, description, event.getId());
            }
            else {
                db.update(DatabaseContract.WeeklyGoals.TABLE_NAME, values, "_id=" + weeklyGoal.getId(), null);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearFields()
    {
        etWeeklyMiles.setText("");
        etLongest.setText("");
        etWeight.setText("");
        etDescription.setText("");
    }

    private void fillFields()
    {
        if(WeekGoalExist())
        {
            etWeeklyMiles.setText(String.valueOf(weeklyGoal.getMiles()));
            etLongest.setText(String.valueOf(weeklyGoal.getLongest()));
            etWeight.setText(String.valueOf(weeklyGoal.getWeight()));
            etDescription.setText(weeklyGoal.getDescription());
        }
        else {
            return;
        }
    }
}
