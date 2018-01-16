package cobaltix.internal_projects.swimtrackerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;


public class WeeklyGoalsActivity extends AppCompatActivity
{
    private EditText etWeek;
    private EditText etMiles;
    private EditText etLongest;
    private EditText etWeight;
    private EditText etDescription;

    private Event event;
    private WeeklyGoal weeklyGoal;
    private String currentWeek;

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
        currentWeek = getIntent().getStringExtra("week");

        dbHelper = new DatabaseHelper(this);

        etWeek = (EditText) findViewById(R.id.etWeek);
        etMiles = (EditText) findViewById(R.id.etWeekMiles);
        etLongest = (EditText) findViewById(R.id.etWeekLongest);
        etWeight = (EditText) findViewById(R.id.etWeekWeight);
        etDescription = (EditText) findViewById(R.id.etNotes);

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(year,monthOfYear,dayOfMonth);
                updateWeekLabel();
            }

        };
        etWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });

        updateWeekLabel();
        if(wgExists())
        {
            fillFields();
        }
    }

    private boolean wgExists()
    {
        weeklyGoal = dbHelper.getWeeklyGoal(currentWeek, event.getId());
        if(weeklyGoal != null)
            return true;
        return false;
    }

    private void updateWeekLabel() {
        if(currentWeek == null)
        {
            currentWeek = WeekManager.getWeek(myCalendar);
        }
        etWeek.setText(currentWeek);
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

        switch (id)
        {
            case R.id.action_done:
                boolean blank = false;

                String m = etMiles.getText().toString();
                String l = etLongest.getText().toString();
                String w = etWeight.getText().toString();
                String description = etDescription.getText().toString();

                if(m.isEmpty() || l.isEmpty() || w.isEmpty() || description.isEmpty())
                    blank = true;
                if(blank)
                {
                    Toast toast = Toast.makeText(this, "Please enter all information", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                    return false;
                }
                else
                {
                    float miles = Float.parseFloat(m);
                    float longest = Float.parseFloat(l);
                    float weight = Float.parseFloat(w);

                    //ADD NEW
                    if (!wgExists())
                    {
                        weeklyGoal = dbHelper.addWeeklyGoal(currentWeek, miles, longest, weight, description, event.getId());
                        System.out.println("------------ WG: " + weeklyGoal);
                        Toast.makeText(this, "Your weekly goals have been saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    //UPDATE
                    else
                    {
                        dbHelper.updateWeeklyGoal(weeklyGoal, miles, longest, weight, description);
                        Toast.makeText(this, "Weekly goals have been updated!", Toast.LENGTH_SHORT).show();

                        onBackPressed();
                    }
                    return true;
                }

            case android.R.id.home:
                onBackPressed();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, OverviewActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("week", currentWeek);
        startActivity(intent);
        finish();
    }

    private void fillFields()
    {
        if(weeklyGoal != null)
        {
            etMiles.setText(String.valueOf(weeklyGoal.getMiles()));
            etLongest.setText(String.valueOf(weeklyGoal.getLongest()));
            etWeight.setText(String.valueOf(weeklyGoal.getWeight()));
            etDescription.setText(weeklyGoal.getDescription());
        }
        else
            return;
    }
}
