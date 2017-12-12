package cobaltix.internal_projects.swimtrackerapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.view.ViewGroup;
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

    private Event eventToRemove;

    private boolean prompt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        System.out.println("----------- Main Activity ------------");

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

        prompt = getIntent().getBooleanExtra("prompt", true);
        //prompt to enter logs at app start
        if(prompt && !eventList.isEmpty())
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
            if (DateFormatter.parse(recentEvent.getStartDate()).before(today) ||dgList.isEmpty() || (!date.equals(DateFormatter.format(today)) && !date.equals(recentEvent.getEndDate())))  // No log yet || last log is not today nor event end date
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

        markDoneEvents();

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
        Event e = (Event) lv.getItemAtPosition(info.position);

        switch(item.getItemId()) {

            case R.id.action_edit:
                Intent intent = new Intent(this, CreateEventActivity.class);
                intent.putExtra("event", e);
                startActivityForResult(intent, 1);
                return true;

            case R.id.action_delete:
                eventToRemove = e;
                createAlertDialog();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void createAlertDialog()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dbHelper.removeEvent(eventToRemove);
                        refreshList();
                        Toast.makeText(MainActivity.this, "The event has been deleted", Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete it?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
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
                refreshList();
            }
        }
    }

    private void refreshList()
    {
        ArrayList<Event> list = dbHelper.getEventList();
        //eventList.clear();
        adapter.clear();
        eventList.addAll(list);
        adapter.notifyDataSetChanged();

        markDoneEvents();
    }

    private void markDoneEvents()
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
