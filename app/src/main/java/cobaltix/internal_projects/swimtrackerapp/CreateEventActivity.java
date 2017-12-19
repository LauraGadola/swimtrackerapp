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

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity
{
    private EditText etEventDate;
    private EditText etEventTitle;
    private EditText etStartDate;

    private Calendar myCalEnd;
    private Calendar myCalStart;
    private DatePickerDialog.OnDateSetListener eventDateListener;
    private DatePickerDialog.OnDateSetListener startDateListener;
    private String dateSelected;
    private Event event;
    private Intent i;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);

        event = (Event) getIntent().getSerializableExtra("event");

        i = new Intent();

        myCalEnd = Calendar.getInstance();
        myCalStart = Calendar.getInstance();

        etEventTitle = (EditText) findViewById(R.id.eventTitle);
        etStartDate = (EditText) findViewById(R.id.startDate);
        etEventDate = (EditText) findViewById(R.id.eventDate);

        //FILL FIELDS IF WE HAVE EVENT
        if(event != null)
        {
            setTitle("Edit Event");

            String title = event.getTitle();
            String startDate = event.getStartDate();
            String eventDate = event.getEventDate();
            if(event.isDone())
            {
                title = title.substring(0, title.indexOf(" "));
            }
            etEventTitle.setText(title);
            etStartDate.setText(startDate);
            etEventDate.setText(eventDate);

            //SET CALENDARS
            myCalStart.setTime(DateFormatter.parse(event.getStartDate()));
            System.out.println("start: "+event.getStartDate());

            myCalEnd.setTime(DateFormatter.parse(event.getEventDate()));
            System.out.println("event: "+event.getEventDate());
        }
        else
        {
            etStartDate.setText(DateFormatter.format(myCalEnd.getTime()));
        }

        startDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalStart.set(year, monthOfYear, dayOfMonth);
                dateSelected = DateFormatter.format(myCalStart.getTime());
                etStartDate.setText(dateSelected);
            }

        };

        eventDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalEnd.set(year, monthOfYear, dayOfMonth);
                dateSelected = DateFormatter.format(myCalEnd.getTime());
                etEventDate.setText(dateSelected);
            }

        };

        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), startDateListener, myCalStart
                        .get(Calendar.YEAR), myCalStart.get(Calendar.MONTH),
                        myCalStart.get(Calendar.DAY_OF_MONTH));
                String date = String.valueOf(etEventDate.getText());
                if(!date.matches(""))
                {
                    datePickerDialog.getDatePicker().setMaxDate(DateFormatter.parse(String.valueOf(etEventDate.getText())).getTime());
                }
                datePickerDialog.show();
            }
        });

        etEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), eventDateListener, myCalEnd
                        .get(Calendar.YEAR), myCalEnd.get(Calendar.MONTH),
                        myCalEnd.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(DateFormatter.parse(String.valueOf(etStartDate.getText())).getTime());
                datePickerDialog.show();
            }
        });

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
            {
                String title = String.valueOf(etEventTitle.getText());
                String startDate = String.valueOf(etStartDate.getText());
                String eventDate = String.valueOf(etEventDate.getText());
                //CREATE NEW EVENT
                if (event == null)
                {
                    //Save new event & get to weekly goals page

                    Event event = dbHelper.addEvent(title, startDate, eventDate);
                    Toast.makeText(this, "A new event has been created!", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(this, OverviewActivity.class);
                    i.putExtra("event", event);
                    startActivity(i);
                    finish();
                }
                else //UPDATE
                {
                    Event updatedEvent = new Event(event.getId(), title, startDate, eventDate);
                    //update database
                    dbHelper.updateEvent(updatedEvent);
                    Toast.makeText(this, "Event has been updated!", Toast.LENGTH_SHORT).show();

                    //Pass details to main
                    setResult(RESULT_OK, i);

                    finish();
                }
                return true;
            }
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
