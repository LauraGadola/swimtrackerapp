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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyGoalsActivity extends AppCompatActivity
{
    private ScrollView scrollView;

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

    private List<DailyGoal> dglist;
    private DailyGoal currentDG;
    private DailyGoal lastSavedDG;

    private String myFormat = "EEE, MM/dd/yy";
    private SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
    private Calendar myCal = Calendar.getInstance();
    private String today = sdf.format(myCal.getTime());
//    private DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve DailyGoal obj sent from main Activity
        event = (Event) getIntent().getSerializableExtra("event");

        dbHelper = new DatabaseHelper(this);

        dglist = dbHelper.getDailyGoalList(event.getId());
        System.out.println(dglist);                                                     //TODO delete

//        pbMiles = (ProgressBar) findViewById(R.id.progressBarMiles);
//        pbWeight = (ProgressBar) findViewById(R.id.progressBarWeight);
//        pbLongest = (ProgressBar) findViewById(R.id.progressBarLongest);

        //TODO below are default to test it
//        pbMiles.setProgress(25);
//        pbWeight.setProgress(75);
//        pbLongest.setProgress(40);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etLongest = (EditText) findViewById(R.id.etLongest);
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
        btnPrevious.setVisibility(View.INVISIBLE);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnNext.setVisibility(View.INVISIBLE);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setVisibility(View.INVISIBLE);

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
//                etDate.setText(sdf.format(myCal.getTime()));
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

                String time = etTime.getText().toString();
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

        lastSavedDG = dbHelper.getLastDailyGoal(event.getId());
        System.out.println("/db: "+lastSavedDG);                                        //TODO delete
        if(!dglist.isEmpty())
            lastSavedDG = dglist.get(dglist.size()-1);
        System.out.println("/Arraylist: "+lastSavedDG);

        if(lastSavedDG != null)
        {
            String date = lastSavedDG.getDate();
            //if we reach today or the the day prior to the event- no more entries allowed
            if(isUpToDate(date))
            {
                fillFields(lastSavedDG);
                if(dglist.size() > 1)
                {
                    Toast.makeText(this, "old event", Toast.LENGTH_SHORT).show();
                    btnPrevious.setVisibility(View.VISIBLE);
                }
//                    btnNext.setText("Exit");
            }
            else //move to next day
            {
                etDate.setText(dayAfter(date));
                btnPrevious.setVisibility(View.VISIBLE);
            }
        }
        else //today is the first entry for this event
        {
            System.out.println("/dga/--------- Today is the first entry for this event");
            etDate.setText(today);
//            btnNext.setText("Exit");
        }

        Date eventDate = null;
        try
        {
            eventDate = sdf.parse(event.getDate());
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        System.out.println(myCal.getTime().toString());                                 //TODO delete
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

        btnPrevious.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DailyGoal previousDG = getPreviousTo(currentDG);
                fillFields(previousDG);
                btnNext.setVisibility(View.VISIBLE);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DailyGoal nextDG = getNextTO(currentDG);
                if(nextDG == null)
                {
                    etDate.setText(dayAfter(lastSavedDG.getDate()));
                }
                else
                {
                    fillFields(nextDG);
                }
                btnPrevious.setVisibility(View.VISIBLE);

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dbHelper.removeDailyGoal(currentDG);
                dglist.remove(currentDG);
                Toast.makeText(DailyGoalsActivity.this, "Daily goal has been deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isUpToDate(String date)
    {
        return(date.equals(today) || date.equals(getEndDate()));
    }

    public DailyGoal getPreviousTo(DailyGoal dg)
    {
        btnPrevious.setVisibility(View.INVISIBLE);
        DailyGoal previousDG;
        System.out.println(dg);

        if(dg != null)
        {
            int i = dglist.indexOf(dg);
            System.out.println(i);
            i--;
            previousDG = dglist.get(i);
            if (i != 0)      //we will get the last element
            {
                btnPrevious.setVisibility(View.VISIBLE);
            }
        }
        else    //Current is not in the list, the previous goal will be the last one saved
        {
            previousDG = dglist.get(dglist.size()-1);
            if(dglist.size() > 1)
            {
                btnPrevious.setVisibility(View.VISIBLE);
            }
        }
        return previousDG;
    }

    private DailyGoal getNextTO(DailyGoal dg)
    {
        System.out.println(dg);      //TODO delete
        System.out.println(dglist);
        int i = dglist.indexOf(dg);
        DailyGoal nextDG;
        System.out.println(i);

        if(lastSavedDG.equals(getEndDate()))
        {
            if(dglist.get(i+1).equals(lastSavedDG))
            {
                btnNext.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            if (dglist.get(i).equals(lastSavedDG))
            {
                btnNext.setVisibility(View.INVISIBLE);
                return null;
            }
        }
        i++;
        nextDG = dglist.get(i);
        return nextDG;
    }

    public String dayAfter(String date)
    {
        currentDG = null;
        clearAll();

        try
        {
            Date d = sdf.parse(date);
            myCal.setTime(d);
            myCal.add(myCal.DATE, 1);
            return sdf.format(myCal.getTime());
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return "NO DATE";
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
        currentDG = dg;
        btnDelete.setVisibility(View.VISIBLE);

        etDate.setText(dg.getDate());
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
            String date = String.valueOf(etDate.getText());                             //TODO TO TEST if it equals to today
            String location = etLocation.getText().toString();
            float temp = Float.parseFloat(etTempF.getText().toString());


            String time = String.valueOf(etTime.getText());
            int hrs = Integer.parseInt(time.substring(0, time.indexOf(":")));
            int min = Integer.parseInt(time.substring(time.indexOf(":")+1, time.length()));

            float weight = Float.parseFloat(etWeight.getText().toString());
            float miles = Float.parseFloat(etMiles.getText().toString());
            float longest = Float.parseFloat(etLongest.getText().toString());
            float honest = Float.parseFloat(etHonest.getText().toString());
            String notes = etNotes.getText().toString();

            WeeklyGoal wg = dbHelper.getLastWeeklyGoal(event.getId());
            int weekly_id = wg.getId();

            if(currentDG != null) //update
            {
                DailyGoal newDG = new DailyGoal(currentDG.getId(), date, location, temp, hrs, min, weight, miles, longest, honest, notes, weekly_id, event.getId());
                int i = dglist.indexOf(currentDG);
                dglist.set(i, newDG);
                dbHelper.updateDailyGoal(currentDG, newDG);
                Toast.makeText(this, "Daily goal has been updated", Toast.LENGTH_SHORT).show();

            }
            else //create new entry
            {
                //TODO the last weekly goal should be the only one in place?
                DailyGoal newDG = new DailyGoal(date, location, temp, hrs, min, weight, miles, longest, honest, notes, weekly_id, event.getId());
                int newRowId = dbHelper.addDailyGoal(newDG);
                newDG.setId(newRowId);
                dglist.add(newDG);
                Toast.makeText(this, "Daily goal was saved", Toast.LENGTH_SHORT).show();

                //If you entered today or last day prior to event go back to main page
                if(isUpToDate(date))
                {
                    Toast.makeText(this, "Your log is up-to-date!", Toast.LENGTH_SHORT).show();
                    this.finish();                                             //TODO return to main page
                }
                else //go to next
                {
                    etDate.setText(dayAfter(date));
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            }
        }

        return super.onOptionsItemSelected(item);
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
        etLongest.setText("");
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

}
