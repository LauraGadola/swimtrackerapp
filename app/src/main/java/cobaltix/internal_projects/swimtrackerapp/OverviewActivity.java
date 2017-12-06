package cobaltix.internal_projects.swimtrackerapp;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
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
import android.widget.RelativeLayout;

import java.util.Calendar;

public class OverviewActivity extends AppCompatActivity
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
    private Event event;
    private TabLayout tabLayout;
    Calendar myCal;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("----------- Overview Activity ------------");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("Stats"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        event = (Event) getIntent().getSerializableExtra("Event");

        myCal = Calendar.getInstance();
        String week = HelperClass.getWeek(myCal);
        setTitle(week);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), event, week);

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

    }

//
//    //todo check if needed
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("Overview","OnActivityResult");
//        System.out.println("code: "+requestCode);
//        System.out.println("result code: "+resultCode);
//        if (requestCode == 1) {
//            if(resultCode == RESULT_OK) {
//                event = (Event) data.getSerializableExtra("Event");
//                System.out.println("Event from result method: "+event);
//            }
//        }
//    }

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action_calendar:
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        myCal.set(year, monthOfYear, dayOfMonth);
                        String week = HelperClass.getWeek(myCal);
                        setTitle(week);

                        //Update list in tab
                        TabFragment1 tabList = (TabFragment1) mSectionsPagerAdapter.getTab(0);
                        View v = tabList.getView();
                        tabList.setList(v, week);

                        TabFragment2 tabStats = (TabFragment2) mSectionsPagerAdapter.getTab(1);
                        tabStats.setElements(week);

                    }
                };
                System.out.println(myCal.getTime());
                new DatePickerDialog(this, date, myCal.get(Calendar.YEAR), myCal.get(Calendar.MONTH),
                        myCal.get(Calendar.DAY_OF_MONTH)).show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
