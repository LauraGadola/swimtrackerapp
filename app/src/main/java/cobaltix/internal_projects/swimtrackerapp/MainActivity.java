package cobaltix.internal_projects.swimtrackerapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity
{
    //Visual objects in the layout file
    private ListView lv;
    private FloatingActionButton fab;
    //HANDLE THE DATA OF THE LISTVIEW
    private ArrayList<Event> eventList;
    private EventListAdapter adapter;

    private Calendar myCal = Calendar.getInstance();
    private Date today = myCal.getTime();

    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("Class: "+this);
        System.out.println("----------- Main Activity ------------");
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(this);

        ////Import database if needed - todo to delete eventually
        if(! dbHelper.isDatabaseExist(this))
        {
            Log.e("MainActivity", "db does not exist");
            dbHelper.importDatabase();
        }
        else
            Log.e("MainActivity", "db exists!");


        eventList = dbHelper.getEventList();
        System.out.println("Main: List: "+eventList);
        Log.e("MainActivity","EventList: "+eventList);

        ////prompt to enter logs at app start - todo TO TEST!!
        if(!eventList.isEmpty())
        {
            Event recentEvent = eventList.get(0);
            Log.e("MainActivity", "Event: " + recentEvent);

            LinkedList<DailyGoal> dgList = dbHelper.getDailyGoalList(recentEvent.getId());
            DailyGoal lastDG;
            String date = "";
            if (!dgList.isEmpty())
            {
                lastDG = dgList.get(dgList.size() - 1);
                date = lastDG.getDate();
            }

            //Prompt daily goal activity to enter logs if needed
            if (dgList.isEmpty() || (!date.equals(DateFormatter.format(today)) && !date.equals(recentEvent.getEndDate())))  // No log yet || last log is not today nor event end date
            {
                Intent intent = new Intent(this, DailyGoalsActivity.class);
                intent.putExtra("event", recentEvent);
                startActivity(intent);

                //todo should it be a dialog?
                Toast toast= Toast.makeText(this, "You have not entered all your logs!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
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

                Event e = (Event) lv.getItemAtPosition(position);
                Intent i = new Intent(getApplicationContext(), OverviewActivity.class);
                i.putExtra("event", e);
                startActivity(i);
            }
        });
//        lv.setOnLongClickListener(new View.OnLongClickListener()
//        {
//            @Override
//            public boolean onLongClick(View v)
//            {
//
//            }
//        });
        registerForContextMenu(lv);

        //here for testing purposes (need to export db) - todo move to export cvs clicked
        PermissionsHandler permissionsHandler = new PermissionsHandler(this);
        permissionsHandler.requestWriteExtStoragePermissions();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.eventList) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_event_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(this, CreateEventActivity.class);
                Event e = (Event) lv.getItemAtPosition(info.position);
                System.out.println("Main: Event: "+e);
                intent.putExtra("event", e);
                startActivityForResult(intent, 1);
                return true;
            case R.id.action_delete:
                // edit stuff here
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Main","OnActivityResult/");
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                ArrayList<Event> list = dbHelper.getEventList();
                eventList.clear();
                eventList.addAll(list);
//                Event event = (Event) data.getSerializableExtra("update");
//                if (event != null)
//                {
//                    int index = eventList.indexOf(event);
//                    eventList.set(index, event);
//                }
            }
        }
    }

    private void markDoneEvents(ArrayList<Event> eventList)
    {
        for(Event e : eventList)
        {
            if (DateFormatter.parse(e.getEndDate()).before(today))
            {
                e.setTitle(e.getTitle() + " (Done)");
                e.setDone(true);
            }
            else
                e.setDone(false);
        }

    }

    public void updateListView(Event event) {
        System.out.println("Class: "+this);
        System.out.println("Main: (add) List: "+eventList);
        System.out.println("Main: adapter: "+adapter);
        if(event != null)
        {
            adapter.add(event);
            //Notifies the attached observers that the underlying data has been changed and any View reflecting the data set should refresh itself.
            adapter.notifyDataSetChanged();
        }
    }

    public void replaceInListView(Event oldEvent, Event updatedEvent)
    {
        System.out.println("Main: List: "+ eventList);

        int index = eventList.indexOf(oldEvent);
        eventList.set(index, updatedEvent);
        adapter.notifyDataSetChanged();
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
            dbHelper.exportToCVS();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
