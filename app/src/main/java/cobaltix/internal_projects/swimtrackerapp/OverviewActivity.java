package cobaltix.internal_projects.swimtrackerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class OverviewActivity extends AppCompatActivity implements TabFragment1.OnLongestCalculatedListener
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FloatingActionButton fab;
    private Event event;
    private TabLayout tabLayout;
    private Calendar myCal;
    private String currentWeek;

    private TabFragment1 tabOverview;
    private TabFragment2 tabStats;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("----------- Overview Activity ------------");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);

        event = (Event) getIntent().getSerializableExtra("event");
        currentWeek = getIntent().getStringExtra("week");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("Stats"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //SET WEEK IN PAGE TITLE
        myCal = Calendar.getInstance();
        if(currentWeek == null)
        {
            if (event.isDone())
            {
                myCal.setTime(DateFormatter.parse(event.getEndDate()));
            }
            currentWeek = HelperClass.getWeek(myCal);
        }
        setTitle(currentWeek);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), event, currentWeek);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), DailyGoalsActivity.class);
                intent.putExtra("event", event);
                intent.putExtra("week", currentWeek);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Overview","OnActivityResult/");
        if (requestCode == 1) {
            if(resultCode == RESULT_OK)
            {
                String week = data.getStringExtra("week");
                System.out.println("Overview: week received: "+week);
                if(week != null)
                {
                    currentWeek = week;
                }
                setTitle(currentWeek);
                LinkedList<DailyGoal> dailyGoals = dbHelper.getDailyGoalList(currentWeek);
                getTabOverview().setList(dailyGoals);
                if(dailyGoals.size() == 7)
                {
                    fab.setVisibility(View.INVISIBLE);
                }
                else
                    fab.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_calendar:
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        myCal.set(year, monthOfYear, dayOfMonth);
                        currentWeek = HelperClass.getWeek(myCal);
                        setTitle(currentWeek);

                        //Update list in tab
                        getTabOverview();
                        tabOverview.setWeek(currentWeek);
                        tabOverview.populateListView();
                        tabOverview.showResults();

                        getTabStats();
                        tabStats.setWeek(currentWeek);
                        tabStats.setElements();

                    }
                };
                DatePickerDialog pickerDialog = new DatePickerDialog(this, date, myCal.get(Calendar.YEAR), myCal.get(Calendar.MONTH),
                        myCal.get(Calendar.DAY_OF_MONTH));
                pickerDialog.getDatePicker().setMinDate(DateFormatter.parse(event.getStartDate()).getTime());
                pickerDialog.getDatePicker().setMaxDate(DateFormatter.parse(event.getEndDate()).getTime());
                pickerDialog.show();

                return true;

            case R.id.action_week_goal:
                Intent intent = new Intent(this, WeeklyGoalsActivity.class);
                intent.putExtra("event", event);
                intent.putExtra("week", currentWeek);
                startActivity(intent);
                return true;

            case android.R.id.home:
                onBackPressed();
//                LinkedList<DailyGoal> dailyGoals = dbHelper.getDailyGoalList(currentWeek);
//                getTabOverview().setList(dailyGoals);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("prompt", false);
        startActivity(intent);
        finish();
    }

    @Override
    public void sendLongest(float longest, float miles)
    {
        TabFragment2 tab2 = (TabFragment2) mSectionsPagerAdapter.getTab(1);
        tab2.setLongest(longest);
        tab2.setTotDist(miles);
    }

    public TabFragment1 getTabOverview()
    {
        return tabOverview = (TabFragment1) mSectionsPagerAdapter.getTab(0);
    }

    public TabFragment2 getTabStats()
    {
        return tabStats = (TabFragment2) mSectionsPagerAdapter.getTab(1);
    }
}
