package cobaltix.internal_projects.swimtrackerapp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

public class DailyGoalsActivity extends AppCompatActivity
{
    private ProgressBar pbMiles;
    private ProgressBar pbWeight;
    private ProgressBar pbLongest;

    private EditText etLocation;
    private EditText etTime;

    private NumberPicker minPicker;
    private NumberPicker hrsPicker;

    private EditText etTempC;
    private EditText etTempF;

    private EditText etYards;
    private EditText etMiles;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private WeeklyGoal wg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);

        // Retrieve WeeklyGoal obj sent from Create Event Activity
        wg = (WeeklyGoal) getIntent().getSerializableExtra("weekly_goal");

        pbMiles = (ProgressBar) findViewById(R.id.progressBarMiles);
        pbWeight = (ProgressBar) findViewById(R.id.progressBarWeight);
        pbLongest = (ProgressBar) findViewById(R.id.progressBarLongest);


        //TODO below are default to test it
        pbMiles.setProgress(25);
        pbWeight.setProgress(75);
        pbLongest.setProgress(40);

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
                if(!time.isEmpty())
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
//            String location =
//            float miles = Float.parseFloat(etWeeklyMiles.getText().toString());
//            float longest = Float.parseFloat(etLongest.getText().toString());
//            float weight = Float.parseFloat(etWeight.getText().toString());
//            String description = etDescription.getText().toString();
//
//            if(!wgExists())
//            {
//                weeklyGoal = dbHelper.addWeeklyGoal(sunday, miles, longest, weight, description, event.getId());
//                Toast.makeText(this, "Your weekly goal was saved", Toast.LENGTH_SHORT).show();
//            }
//
//            //TODO Do I need to get the wg back?
//            else
//            {
//                dbHelper.updateWeeklyGoal(weeklyGoal, miles, longest, weight, description);
//                Toast.makeText(this, "You have updated this weekly goal", Toast.LENGTH_SHORT).show();
//            }

        }

        return super.onOptionsItemSelected(item);
    }

    public void covertFtoC(float tempF)
    {
        float tempC = (float) ((tempF - 32) / 1.8);
        etTempC.setText(String.valueOf(round(tempC)));
    }

    public void covertCtoF(float tempC)
    {
        float tempF = (float) (tempC * 1.8 + 32);
        etTempF.setText(String.valueOf(round(tempF)));
    }

    public void convertYtoM(float yards)
    {
        float miles = yards / 1760;
        etMiles.setText(String.valueOf(round(miles)));
    }

    public void covertMtoY(float miles)
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

    //TODO improve
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
