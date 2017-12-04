package cobaltix.internal_projects.swimtrackerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

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
        etLongest = (EditText) findViewById(R.id.etMiles);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etDescription = (EditText) findViewById(R.id.etDescription);

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(year,monthOfYear,dayOfMonth);
                //int dayOfWeek = myCalendar.get(Calendar.DAY_OF_WEEK);
                updateLabel(myCalendar);
                if(wgExists())
                {
//                    clearFields();
                    fillFields();
                }
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

        updateLabel(myCalendar);
        if(wgExists())
        {
//      clearFields();
            fillFields();
        }
    }

    private boolean wgExists()
    {
        weeklyGoal = dbHelper.weekGoalExist(sunday, event.getId());
        if(weeklyGoal != null)
            return true;
        return false;
    }


    //todo delete
    private void updateLabel(Calendar myCalendar) {

        etWeek.setText(HelperClass.getWeek(myCalendar));

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

        if (id == R.id.action_done)
        {
            float miles = Float.parseFloat(etWeeklyMiles.getText().toString());
            float longest = Float.parseFloat(etLongest.getText().toString());
            float weight = Float.parseFloat(etWeight.getText().toString());
            String description = etDescription.getText().toString();

            if(!wgExists())
            {
                weeklyGoal = dbHelper.addWeeklyGoal(sunday, miles, longest, weight, description, event.getId());
                Toast.makeText(this, "Your weekly goal was saved", Toast.LENGTH_SHORT).show();
            }

            //TODO Do I need to get the wg back?
            else
            {
                dbHelper.updateWeeklyGoal(weeklyGoal, miles, longest, weight, description);
                Toast.makeText(this, "You have updated this weekly goal", Toast.LENGTH_SHORT).show();
            }
        }


        //TODO go to main page? or as long as there's a day to fill in prompt the daily page?
        Intent i = new Intent(getApplicationContext(), DailyGoalsActivity.class);
        i.putExtra("event", event);
        i.putExtra("weekly_goal", weeklyGoal);
        startActivity(i);
        finish();
        return super.onOptionsItemSelected(item);
    }

    //TODO Needed?
    private void clearFields()
    {
        etWeeklyMiles.setText("");
        etLongest.setText("");
        etWeight.setText("");
        etDescription.setText("");
    }

    private void fillFields()
    {
        if(weeklyGoal != null)
        {
            etWeeklyMiles.setText(String.valueOf(weeklyGoal.getMiles()));
            etLongest.setText(String.valueOf(weeklyGoal.getLongest()));
            etWeight.setText(String.valueOf(weeklyGoal.getWeight()));
            etDescription.setText(weeklyGoal.getDescription());
        }
        else
            return;
    }
}
