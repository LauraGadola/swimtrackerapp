package cobaltix.internal_projects.swimtrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    //Visual objects in the layout file
    private ListView lv;
    private FloatingActionButton fab;
    //HANDLE THE DATA OF THE LISTVIEW
    ArrayList<Event> eventList;
    private static CustomListAdapter adapter;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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

        dbHelper = new DatabaseHelper(this);
        eventList = dbHelper.getEventList();
        markDoneEvents(eventList);
        adapter = new CustomListAdapter(this, eventList);
        lv = (ListView) findViewById(R.id.eventList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                //TODO get to daily goals until all entries for the week are entered
                Event e = (Event) lv.getItemAtPosition(position);
                Intent i = new Intent(getApplicationContext(), DailyGoalsActivity.class);
                i.putExtra("event", e);
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

        return super.onOptionsItemSelected(item);
    }


}
