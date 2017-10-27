package cobaltix.internal_projects.swimtrackerapp;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity
{
    EditText eventDate;
    EditText eventTitle;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    String dateSelected; //TO ELIMINATE??

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventTitle = (EditText) findViewById(R.id.eventTitle);

        myCalendar = Calendar.getInstance();
        eventDate = (EditText) findViewById(R.id.eventDate);
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(year, monthOfYear, dayOfMonth);
                updateLabel();
            }

        };

        eventDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dbHelper = new DatabaseHelper(this);
    }

    private void updateLabel() {
        String myFormat = "EEE, MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        dateSelected = sdf.format(myCalendar.getTime());
        eventDate.setText(dateSelected);
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
            //Save new event & get to weekly goals page
            String title = eventTitle.getText().toString();
            Event event = dbHelper.addEvent(title, dateSelected);
            MainActivity.updateListView(event);

            Intent i = new Intent(getApplicationContext(), WeeklyGoalsActivity.class);
            i.putExtra("event", event);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
