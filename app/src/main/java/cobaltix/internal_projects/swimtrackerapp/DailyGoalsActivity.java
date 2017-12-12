package cobaltix.internal_projects.swimtrackerapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class DailyGoalsActivity extends AppCompatActivity
{
    private ScrollView scrollView;

    private EditText etDate;
    private EditText etWeeksLeft;
    private EditText etLocation;
    private EditText etTime;
    private EditText etHonest;
    private EditText etNotes;
    private EditText etWeight;
    private EditText etTempC;
    private EditText etTempF;
    private EditText etYards;
    private EditText etMiles;

    private NumberPicker minPicker;
    private NumberPicker hrsPicker;

    private ImageButton btnPrevious;
    private ImageButton btnNext;
    private Button btnDelete;

    private DatabaseHelper dbHelper;
    private Event event;
    private DailyGoal dailyGoal;

    private LinkedList<DailyGoal> dglist;
    private DailyGoal currentDG;
    private DailyGoal lastSavedDG;
    private WeeklyGoal weeklyGoal;

    private Calendar myCal;
    private String today;
//    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        myCal = Calendar.getInstance();
        today = DateFormatter.format(myCal.getTime());
        intent = new Intent();

        System.out.println("----------- Daily Goal Activity ------------");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve obj sent from main Activity
        event = (Event) getIntent().getSerializableExtra("event");
        dailyGoal = (DailyGoal) getIntent().getSerializableExtra("daily_goal");
        String week = getIntent().getStringExtra("week");

        System.out.println("Week: "+week);
        System.out.println("Event: "+event);                                            //TODO delete
        System.out.println("DailyGoal: "+dailyGoal);                                  //TODO delete

        dbHelper = new DatabaseHelper(this);

        dglist = dbHelper.getDailyGoalList(event.getId());

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etHonest = (EditText) findViewById(R.id.etHonest);
        etNotes = (EditText) findViewById(R.id.etNotes);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etTempC = (EditText) findViewById(R.id.etTempC);
        etTempF = (EditText) findViewById(R.id.etTempF);
        etYards = (EditText) findViewById(R.id.etYards);
        etMiles = (EditText) findViewById(R.id.etMiles);
        etTime = (EditText) findViewById(R.id.etTime);
        etDate = (EditText) findViewById(R.id.etDate);
        etWeeksLeft = (EditText) findViewById(R.id.etWeeksLeft);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        etTempC.setOnFocusChangeListener(new CustomOnFocusChangeListener(this));
        etTempF.setOnFocusChangeListener(new CustomOnFocusChangeListener(this));
        etYards.setOnFocusChangeListener(new CustomOnFocusChangeListener(this));
        etMiles.setOnFocusChangeListener(new CustomOnFocusChangeListener(this));

//        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                myCal.set(year, monthOfYear, dayOfMonth);
//                etDate.setText(sdf.formatToDB(myCal.getTime()));
////                fillFields();
//            }
//
//        };
//        etDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), onDateSetListener, myCal
//                        .get(Calendar.YEAR), myCal.get(Calendar.MONTH),
//                        myCal.get(Calendar.DAY_OF_MONTH));
//                System.out.println(myCal.getTime().toString());                       //TODO delete
//                datePickerDialog.show();
//            }
//        });

        //TODO create separate class for onClickListener
        etTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.custom_number_picker, null);
                AlertDialog.Builder d = new AlertDialog.Builder(v.getContext());
                d.setTitle("Select time");
                d.setView(v);
                d.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //Clearing focus enables keyboard use
                        hrsPicker.clearFocus();
                        minPicker.clearFocus();

                        etTime.setText(String.format("%02d:%02d", hrsPicker.getValue(), minPicker.getValue()));
                        dialog.dismiss();
                    }
                });

                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                hrsPicker = (NumberPicker) v.findViewById(R.id.hrsPicker);
                hrsPicker.setMaxValue(99);
                hrsPicker.setMinValue(0);
                hrsPicker.setWrapSelectorWheel(true);

                minPicker = (NumberPicker) v.findViewById(R.id.minPicker);
                minPicker.setMaxValue(59);
                minPicker.setMinValue(0);
                minPicker.setWrapSelectorWheel(true);

                String time = String.valueOf(etTime.getText());
                if(!time.isEmpty()) //set the number pickers to the time in the field
                {
                    int hrs = Integer.parseInt(time.substring(0, time.indexOf(":")));
                    int min = Integer.parseInt(time.substring(time.indexOf(":")+1, time.length()));
                    hrsPicker.setValue(hrs);
                    minPicker.setValue(min);
                }
                d.show();
            }
        });

        //SET BUTTONS
        btnPrevious.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String date = String.valueOf(etDate.getText());
                if (date.contains("Today"))
                {
                    date = date.substring(8);
                }
                String previousDay = getDay(date, -1);
                if(hasWeeklyGoal(previousDay))
                {
                    DailyGoal previousDG = getDG(previousDay);
                    goToDay(previousDG, previousDay);
                    if (previousDay.equals(event.getStartDate()))
                    {
                        btnPrevious.setVisibility(View.INVISIBLE);
                    }
                    btnNext.setVisibility(View.VISIBLE);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String nextDay = getDay(String.valueOf(etDate.getText()), 1);
                if(hasWeeklyGoal(nextDay))
                {
                    DailyGoal nextDG = getDG(nextDay);
                    System.out.println("next: " + nextDay);
                    if (nextDG != null)
                        System.out.println("next from dg: " + nextDG.getDate());
                    goToDay(nextDG, nextDay);
                    btnPrevious.setVisibility(View.VISIBLE);
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dbHelper.removeDailyGoal(currentDG);
                dglist.remove(currentDG);
                updateTabs();
                currentDG = null;
//                clearAll();
                Toast.makeText(DailyGoalsActivity.this, "Daily goal has been deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        //FINISH SETTING BUTTONS

        //FIELDS
        //DAILY GOAL WAS SELECTED
        if(dailyGoal != null)
        {
            goToDay(dailyGoal, dailyGoal.getDate());
        }

        //LOOKING AT SPECIFIC WEEK
        else if(week != null)
        {
            String day = HelperClass.getFirstDay(week);
            hasWeeklyGoal(day);

            while (!isNotBeforeStartDate(day) || getDG(day) != null)
            {
                day = getDay(day, 1);
            }
            if(DateFormatter.parse(day).after(DateFormatter.parse(today)))
            {
                day = today;
            }
            goToDay(null, day);
        }
        //THIS WEEK
        else
        {
            if (!dglist.isEmpty()) //Retrieve goal to display or show blank page
            {
                lastSavedDG = dglist.get(dglist.size() - 1);
            }
            System.out.println("Last saved: " + lastSavedDG);

            if (lastSavedDG != null)
            {
                String date = lastSavedDG.getDate();
                //if we reach today or the the day prior to the event- show last entry
                if (isUpToDate(date))
                {
                    goToDay(lastSavedDG, date);
                } else //move to next day
                {
                    String nextDay = getDay(date, 1);
                    if(hasWeeklyGoal(nextDay))
                    {
                        DailyGoal nextDG = getDG(nextDay);
                        goToDay(nextDG, nextDay);
                    }

                }
            }
            else //no logs for this event yet
            {
                hasWeeklyGoal(event.getStartDate());

                goToDay(null, event.getStartDate());
                btnPrevious.setVisibility(View.INVISIBLE);
            }
        }
    }

    private boolean isNotBeforeStartDate(String day)
    {
        boolean isStartDate = day.equals(event.getStartDate());
        boolean afterStartDate = DateFormatter.parse(day).after(DateFormatter.parse(event.getStartDate()));
        return  isStartDate || afterStartDate;
    }

    private boolean hasWeeklyGoal(String date)
    {
        updateCal(String.valueOf(date));
        String week = HelperClass.getWeek(myCal);
        weeklyGoal = dbHelper.getWeeklyGoal(week);
        if(weeklyGoal == null)
        {
            createWeeklyGoal(week);
            return false;
        }
        System.out.println("-------WE HAVE A WEEKLY GOAL!!!!!!-------");
        System.out.println(weeklyGoal);
        return true;
    }

    private void createWeeklyGoal(String week)
    {
        Toast.makeText(this, "Set your goals for the week first!", Toast.LENGTH_SHORT).show();
        intent = new Intent(this, WeeklyGoalsActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("week", week);
        startActivity(intent);
    }

    //TODO improve - keep only dg
    private void goToDay(DailyGoal dg, String date)
    {
        Log.e("DGA","Going to day " + date);
        if(dg == null)
        {
            currentDG = null;
            clearAll();
            etDate.setText(date);
            updateCal(date);
            btnDelete.setVisibility(View.INVISIBLE);
        }
        else
        {
            currentDG = dg;
            fillFields(dg);
            updateCal(dg.getDate());
        }

        if(isUpToDate(date))
        {
            btnNext.setVisibility(View.INVISIBLE);
            if (date.equals(today))
            {
                etDate.setText("(Today) " + date);
                updateCal(date);
            }
        }

        etWeeksLeft.setText(calculateWeeksLeft());
    }

    private void updateCal(String date)
    {
        myCal.setTime(DateFormatter.parse(date));
        System.out.println("Date updated to: "+date);
    }

    private String calculateWeeksLeft()
    {
        Date eventDate = DateFormatter.parse(event.getEventDate());

        System.out.println("Date :" +myCal.getTime());
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

        return String.valueOf(weeks);
    }

    private boolean isUpToDate(String date)
    {
        return(date.equals(event.getEndDate()) || date.equals(today));
    }

    private DailyGoal getDG(String day)
    {
        for (DailyGoal dg : dglist)
        {
            if (dg.getDate().equals(day))
            {
                System.out.println(dg);                                             //TODO delete
                return dg;
            }
        }
        return null;
    }

    private String getDay(String date, int beforeOrAfter)
    {
        Date d = DateFormatter.parse(date);
        myCal.setTime(d);
        myCal.add(myCal.DATE, beforeOrAfter);
        return DateFormatter.format(myCal.getTime());
    }

    private void fillFields(DailyGoal dg)
    {
        System.out.println("filling fields with "+currentDG);
        btnDelete.setVisibility(View.VISIBLE);

        String date = dg.getDate();
        etDate.setText(date);

        etLocation.setText(dg.getLocation());

        float tempF = dg.getTemp();
        etTempF.setText(String.valueOf(tempF));
        convertFtoC(tempF);

        etTime.setText(String.format("%02d:%02d", dg.getHrs(), dg.getMin()));
        etWeight.setText(String.valueOf(dg.getWeight()));

        float miles = dg.getMiles();
        etMiles.setText(String.valueOf(miles));
        convertMtoY(miles);

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

        switch (id)
        {
            case R.id.action_done:

                String date = String.valueOf(etDate.getText());
                if (date.contains("Today"))
                {
                    date = date.substring(8);
                }

                String location = String.valueOf(etLocation.getText());
                float temp = Float.parseFloat(String.valueOf(etTempF.getText()));
                String time = String.valueOf(etTime.getText());
                int hrs = Integer.parseInt(time.substring(0, time.indexOf(":")));
                int min = Integer.parseInt(time.substring(time.indexOf(":") + 1, time.length()));

                float weight = Float.parseFloat(String.valueOf(etWeight.getText()));
                float miles = Float.parseFloat(String.valueOf(etMiles.getText()));
                float honest = Float.parseFloat(String.valueOf(etHonest.getText()));
                String notes = String.valueOf(etNotes.getText());

                //UPDATE
                if (currentDG != null)
                {
                    DailyGoal updatedDG = new DailyGoal(currentDG.getId(), date, location, temp, hrs, min, weight, miles, honest, notes, currentDG.getWeekly_id(), event.getId());
                    currentDG = updatedDG;
                    System.out.println("DGA: List: "+dglist);
                    int i = dglist.indexOf(currentDG);
                    dglist.set(i, updatedDG);
                    dbHelper.updateDailyGoal(currentDG, updatedDG);
                    updateTabs();

                    Toast.makeText(this, "Daily goal has been updated", Toast.LENGTH_SHORT).show();
                    scrollView.fullScroll(ScrollView.FOCUS_UP);

                    finish();
                    return true;
                }
                //CREATE NEW
                else
                {
                    //TODO the last weekly goal should be the only one in place?
                    DailyGoal newDG = new DailyGoal(date, location, temp, hrs, min, weight, miles, honest, notes, weeklyGoal.getId(), event.getId());
                    int newRowId = dbHelper.addDailyGoal(newDG);
                    newDG.setId(newRowId);
                    currentDG = newDG;
                    dglist = dbHelper.getDailyGoalList(event.getId());
                    updateTabs();

                    Toast.makeText(this, "Daily goal was saved", Toast.LENGTH_SHORT).show();

                    //If you entered today or last day prior to event go back to main page
                    if (isUpToDate(date))
                    {
                        Toast.makeText(this, "Your are up-to-date!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else //go to next
                    {
                        String nextDay = getDay(date, 1);
                        DailyGoal nextDG = getDG(nextDay);
                        Log.e("DGA","DG: "+nextDay);
                        goToDay(nextDG, nextDay);
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                        return true;
                    }
                }

            case android.R.id.home:
                updateTabs();
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void updateTabs()
    {
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, intent);
        finish();
    }

    private void clearAll()
    {
        etLocation.setText("");
        clearF();
        clearC();
        etTime.setText("");
        etWeight.setText("");
        clearM();
        clearY();
        etHonest.setText("");
        etNotes.setText("");
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

    public void dgListToString()
    {
        int i = 0;
        for(DailyGoal dg : dglist)
        {
            System.out.println("List "+i+": "+dg);
            i++;
        }
    }
}
