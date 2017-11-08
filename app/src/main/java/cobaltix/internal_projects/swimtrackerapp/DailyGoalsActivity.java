package cobaltix.internal_projects.swimtrackerapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyGoalsActivity extends AppCompatActivity
{
    private ProgressBar pbMiles;
    private ProgressBar pbWeight;
    private ProgressBar pbLongest;

    private EditText etDate;
    private EditText etWeeksLeft;
    private EditText etLocation;
    private EditText etTime;
    private EditText etLongest;
    private EditText etHonest;
    private EditText etNotes;
    private EditText etWeight;

    private NumberPicker minPicker;
    private NumberPicker hrsPicker;

    private EditText etTempC;
    private EditText etTempF;

    private EditText etYards;
    private EditText etMiles;

    private DatabaseHelper dbHelper;
    private Event event;
    private Calendar myCal;

    String myFormat = "EEE, MM/dd/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);

        // Retrieve DailyGoal obj sent from main Activity
        event = (Event) getIntent().getSerializableExtra("event");

        pbMiles = (ProgressBar) findViewById(R.id.progressBarMiles);
        pbWeight = (ProgressBar) findViewById(R.id.progressBarWeight);
        pbLongest = (ProgressBar) findViewById(R.id.progressBarLongest);

        //TODO below are default to test it
        pbMiles.setProgress(25);
        pbWeight.setProgress(75);
        pbLongest.setProgress(40);

        etLocation = (EditText) findViewById(R.id.etLocation);
        etLongest = (EditText) findViewById(R.id.etLongest);
        etHonest = (EditText) findViewById(R.id.etHonest);
        etNotes = (EditText) findViewById(R.id.etNotes);
        etWeight = (EditText) findViewById(R.id.etWeight);

        etTempC = (EditText) findViewById(R.id.etTempC);
        etTempC.setOnFocusChangeListener(new CustomOnFocusChangeListener(this));

        etTempF = (EditText) findViewById(R.id.etTempF);
        etTempF.setOnFocusChangeListener(new CustomOnFocusChangeListener(this));

        etYards = (EditText) findViewById(R.id.etYards);
        etYards.setOnFocusChangeListener(new CustomOnFocusChangeListener(this));

        etMiles = (EditText) findViewById(R.id.etMiles);
        etMiles.setOnFocusChangeListener(new CustomOnFocusChangeListener(this));

        etTime = (EditText) findViewById(R.id.etTime);
        etTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.custom_number_picker, null);
                AlertDialog d = new AlertDialog.Builder(v.getContext()).create();

                d.setTitle("Select time");
                d.setView(v);

                hrsPicker = (NumberPicker) v.findViewById(R.id.hrsPicker);
                hrsPicker.setMaxValue(99);
                hrsPicker.setMinValue(0);
                hrsPicker.setWrapSelectorWheel(true);

                minPicker = (NumberPicker) v.findViewById(R.id.minPicker);
                minPicker.setMaxValue(59);
                minPicker.setMinValue(0);
                minPicker.setWrapSelectorWheel(true);

                String time = etTime.getText().toString();
                if(!time.isEmpty()) //set the number pickers to the time in the field
                {
                    int hrs = Integer.parseInt(time.substring(0, time.indexOf(":")));
                    int min = Integer.parseInt(time.substring(time.indexOf(":")+1, time.length()));
                    hrsPicker.setValue(hrs);
                    minPicker.setValue(min);
                }
                d.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Clearing focus enables keyboard use
                                hrsPicker.clearFocus();
                                minPicker.clearFocus();

                                etTime.setText(String.format("%02d:%02d", hrsPicker.getValue(), minPicker.getValue()));
                                dialog.dismiss();
                            }
                        });
                d.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });
                d.show();
            }
        });

        DailyGoal dg = dbHelper.getLastDailyGoal(event.getId());

        etDate = (EditText) findViewById(R.id.etDate);
        myCal = Calendar.getInstance();

        if(dg != null)
        {
            try
            {
                String date = dg.getDate();
                Date d = sdf.parse(dg.getDate());
                //if we reach today or the the day prior to the event- no more entries allowed
                //TODO test the before statement
                if( date.equals(sdf.format(myCal.getTime())) || date.equals(getEndDate()) )
                {
                    fillFields(dg);
                    //TODO hide 'next' button if date == date before the swim?
                }
                else //move to next day
                {
                    myCal.setTime(d);
                    myCal.add(myCal.DATE, 1);
                    etDate.setText(sdf.format(myCal.getTime()));
                }
            }
            catch (ParseException e) {e.printStackTrace();}
        }
        else //today is the first entry for this event
            etDate.setText(sdf.format(myCal.getTime()));

        etWeeksLeft = (EditText) findViewById(R.id.etWeeksLeft);
        Date eventDate = null;
        try
        {
            eventDate = sdf.parse(event.getDate());
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        int currentWeek = myCal.get(Calendar.WEEK_OF_YEAR);
        myCal.setTime(eventDate);
        int eventWeek = myCal.get(Calendar.WEEK_OF_YEAR);
        int weeks;
        if(eventWeek < currentWeek)
        {
            weeks = 52 - currentWeek + eventWeek;
        }
        else
            weeks = eventWeek - currentWeek;

        etWeeksLeft.setText(String.valueOf(weeks));
    }

    public String getEndDate()
    {
        String eventDate = event.getDate();
        try
        {
            Date d = sdf.parse(eventDate);
            myCal.setTime(d);
            myCal.add(Calendar.DATE, -1);   //Day prior to the event
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        String endDate = sdf.format(myCal.getTime());

        //TODO delete
        System.out.println("/Dga/--------- end date: "+endDate);

        return endDate;
    }

    private void fillFields(DailyGoal dg)
    {
        etDate.setText(dg.getDate());
        //calculate weeks left
        etLocation.setText(dg.getLocation());

        float tempF = dg.getTemp();
        etTempF.setText(String.valueOf(tempF));
        convertFtoC(tempF);

        etTime.setText(String.format("%02d:%02d", dg.getHrs(), dg.getMin()));
        etWeight.setText(String.valueOf(dg.getWeight()));

        float miles = dg.getMiles();
        etMiles.setText(String.valueOf(miles));
        convertMtoY(miles);

        etLongest.setText(String.valueOf(dg.getLongest()));
        etHonest.setText(String.valueOf(dg.getHonest()));
        etNotes.setText(dg.getNotes());
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
            //TODO
            if(true) //create new entry
            {
                String date = etDate.getText().toString();
                String location = etLocation.getText().toString();
                float temp = Float.parseFloat(etTempF.getText().toString());
                int hrs = hrsPicker.getValue();
                int min = minPicker.getValue();
                float weight = Float.parseFloat(etWeight.getText().toString());
                float miles = Float.parseFloat(etMiles.getText().toString());
                float longest = Float.parseFloat(etLongest.getText().toString());
                float honest = Float.parseFloat(etHonest.getText().toString());
                String notes = etNotes.getText().toString();

                //TODO the last weekly goal should be the only one in place?
                WeeklyGoal wg = dbHelper.getLastWeeklyGoal(event.getId());
                int weekly_id = wg.getId();
                DailyGoal dg = new DailyGoal(date, location, temp, hrs, min, weight, miles, longest, honest, notes, weekly_id, event.getId());
                dbHelper.addDailyGoal(dg);
                Toast.makeText(this, "Daily goal was saved", Toast.LENGTH_SHORT).show();
            }
            else  //update
            {

            }
        }

        return super.onOptionsItemSelected(item);

        //TODO return to main page
    }

    public void convertFtoC(float tempF)
    {
        float tempC = (float) ((tempF - 32) / 1.8);
        etTempC.setText(String.valueOf(round(tempC)));
    }

    public void convertCtoF(float tempC)
    {
        float tempF = (float) (tempC * 1.8 + 32);
        etTempF.setText(String.valueOf(round(tempF)));
    }

    public void convertYtoM(float yards)
    {
        float miles = yards / 1760;
        etMiles.setText(String.valueOf(round(miles)));
    }

    public void convertMtoY(float miles)
    {
        float yards = miles * 1760;
        etYards.setText(String.valueOf(round(yards)));
    }

    //Round number to 1 decimal digit
    private float round(float num)
    {
        float round = Math.round(num * 10);
        float result = round / 10;
        return result;
    }

    //TODO improve below
    public void clearF()
    {
        etTempF.setText("");
    }

    public void clearC()
    {
        etTempC.setText("");
    }

    public void clearY()
    {
        etYards.setText("");
    }

    public void clearM()
    {
        etMiles.setText("");
    }

}
