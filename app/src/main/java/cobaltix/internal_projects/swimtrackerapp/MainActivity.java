package cobaltix.internal_projects.swimtrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
{
    //Visual objects in the layout file
    private ListView lv;
    private FloatingActionButton fab;
    //HANDLE THE DATA OF THE LISTVIEW
    private ArrayList<Event> eventList;
    private static EventListAdapter adapter;

    private SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy");
    private Calendar myCal = Calendar.getInstance();
    private String today = sdf.format(myCal.getTime());

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("----------- Main Activity ------------");
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(this);

        //Import database if needed - todo to delete eventually
        if(! dbHelper.isDatabaseExist(this))
        {
            Log.e("MainActivity", "db does not exist");
            dbHelper.importDatabase();
        }
        else
            Log.e("MainActivity", "db exists!");


        eventList = dbHelper.getEventList();
        Log.e("MainActivity","EventList: "+eventList);

        //todo TO TEST!!
        if(!eventList.isEmpty())
        {
            Event recentEvent = eventList.get(0);
            Log.e("MainActivity", "Event: " + recentEvent);

            ArrayList<DailyGoal> dgList = dbHelper.getDailyGoalList(recentEvent.getId());
            DailyGoal lastDG = null;
            String date = "";
            if (!dgList.isEmpty())
            {
                lastDG = dgList.get(dgList.size() - 1);
                date = lastDG.getDate();
            }

            //Prompt daily goal activity to enter logs if needed
            if (dgList.isEmpty() || (!date.equals(today) && !date.equals(recentEvent.getEndDate())))  // No log yet || last log is not today nor event end date
            {
                Intent intent = new Intent(this, DailyGoalsActivity.class);
                intent.putExtra("event", recentEvent);
                startActivity(intent);

                //todo should it be a dialog?
                Toast.makeText(this, "You have not entered all your logs!", Toast.LENGTH_SHORT).show();
            }

        }
        ////End of prompt

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                startActivity(intent);
            }
        });


        markDoneEvents(eventList);
        adapter = new EventListAdapter(this, eventList);
        lv = (ListView) findViewById(R.id.eventList);
        lv.setAdapter(adapter);
        lv.setEmptyView(findViewById(R.id.empty));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                                    //TODO get to daily goals until all entries for the week are entered
                Event e = (Event) lv.getItemAtPosition(position);
                Intent i = new Intent(getApplicationContext(), OverviewActivity.class);
                i.putExtra("Event", e);
                startActivity(i);
            }
        });

    }

    private void markDoneEvents(ArrayList<Event> eventList)
    {

        for(Event e : eventList)
        {
            ArrayList<DailyGoal> dgList = dbHelper.getDailyGoalList(e.getId());
            if(!dgList.isEmpty())
            {
                DailyGoal dgLast = dgList.get(dgList.size() - 1);
                if (dgLast.getDate().equals(e.getEndDate()))
                {
                                                                        //todo If today is past end date
                    e.setTitle(e.getTitle() + " (Done)");
                }
            }
        }

    }

    public static void updateListView(Event event) {
        if(event != null)
        {
            adapter.add(event);
            //Notifies the attached observers that the underlying data has been changed and any View reflecting the data set should refresh itself.
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }

        else if(id == R.id.backup)
        {
            System.out.println("Backup Clicked--------");
            dbHelper.exportToCVS();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
