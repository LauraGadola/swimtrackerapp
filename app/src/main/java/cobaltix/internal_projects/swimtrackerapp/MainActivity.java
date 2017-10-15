package cobaltix.internal_projects.swimtrackerapp;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    //Visual objects in the layout file
    private ListView lv;
    private FloatingActionButton fab;
    //HANDLE THE DATA OF THE LISTVIEW
    ArrayList<Event> eventList;
    private static CustomList adapter;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

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

        eventList = new ArrayList<>();
        adapter = new CustomList(this, eventList);
        lv = (ListView) findViewById(R.id.eventList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                //TODO get to daily goals until all entries for the week are entered
                //Event e = (Event) lv.getItemAtPosition(position);
                Intent i = new Intent(getApplicationContext(), DailyGoalsActivity.class);
                //i.putExtra("event", e);
                startActivity(i);
            }
        });

        dbHelper = new DatabaseHelper(this);
        retrieveDatabase();

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

    public void retrieveDatabase()
    {
        db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DatabaseContract.Events._ID,
                DatabaseContract.Events.COLUMN_NAME_TITLE,
                DatabaseContract.Events.COLUMN_NAME_DATE
        };

        // Filter results WHERE "title" = 'My Title'
//        String selection = DatabaseContract.Events.COLUMN_NAME_TITLE + " = ?";
//        String[] selectionArgs = { "Hello" };

        // How you want the results sorted in the resulting Cursor
        //TODO: IT'S ORDERING BASED ON THE DAY OF THE WEEK
        String sortOrder =
                DatabaseContract.Events.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = db.query(
                DatabaseContract.Events.TABLE_NAME,       // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while(cursor.moveToNext()) {
            long id = cursor.getInt(0);
            String title = cursor.getString(1);
            String date = cursor.getString(2);
            Event event = new Event(id, title, date);
            adapter.add(event);
        }
        cursor.close();
    }

}
