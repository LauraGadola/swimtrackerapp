package cobaltix.internal_projects.swimtrackerapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity
{
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

    private boolean activeEvent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        System.out.println("----------- Main Activity ------------");

        dbHelper = new DatabaseHelper(this);

        ////Import database if needed - todo to delete (only for testing)
        if(! dbHelper.isDatabaseExist(this))
        {
            Log.e("MainActivity", "db does not exist");
            dbHelper.importDatabase();
        }
        else
            Log.e("MainActivity", "db exists!");
        //Finish import


        eventList = dbHelper.getEventList();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(activeEvent) //We have one event open - don't allow creation of new event
                {
                    Toast toast = Toast.makeText(MainActivity.this, "You already have an active event.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                    startActivity(intent);
                }
            }
        });

        markDoneEvents();

        prompt = getIntent().getBooleanExtra("prompt", true);

        if(prompt && activeEvent)
        {
            Event recentEvent = eventList.get(0);
            {
                LinkedList<DailyLog> dgList = dbHelper.getDailyLogList(recentEvent.getId());
                DailyLog lastDG;
                String date = "";
                if (!dgList.isEmpty())
                {
                    lastDG = dgList.get(dgList.size() - 1);
                    date = lastDG.getDate();
                }

                //Prompt daily log activity to enter logs if needed
                if (DateFormatter.parse(recentEvent.getStartDate()).before(today)
                        && (dgList.isEmpty() || (!date.equals(DateFormatter.format(today)))))  // No log yet || last log is not today
                {
                    Intent intent = new Intent(this, DailyLogsActivity.class);
                    intent.putExtra("event", recentEvent);
                    startActivity(intent);

                    Toast toast = Toast.makeText(this, "You have not entered all your logs!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
        }
        ////End of prompt

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

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.menu_event_list, menu);

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

            case R.id.action_export:
                PermissionsHandler permissionsHandler = new PermissionsHandler(this);
                permissionsHandler.requestWriteExtStoragePermissions();

                dbHelper.exportToCSV(e);
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
                        if(eventList.isEmpty() || !eventToRemove.isDone())
                        {
                            activeEvent = false;
                            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent,null)));
                        }
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
                activeEvent = false;
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent,null)));
                refreshList();
            }
        }
    }

    private void refreshList()
    {
        ArrayList<Event> list = dbHelper.getEventList();
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
            {
                e.setDone(false);
                {
                    activeEvent = true;
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                }
            }
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
            Toast toast = Toast.makeText(this, "Nothing here yet!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
