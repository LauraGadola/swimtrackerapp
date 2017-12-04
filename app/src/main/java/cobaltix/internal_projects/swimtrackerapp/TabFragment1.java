package cobaltix.internal_projects.swimtrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class TabFragment1 extends MyFragment
{
    private DatabaseHelper dbHelper;
    private ListView lv;
    private ArrayList<DailyGoal> dgList;
    private DailyGoalsListAdapter adapter;
    private Calendar myCal;
    private WeeklyGoal weeklyGoal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        System.out.println("----------- Tab 1 ------------");

        myCal = Calendar.getInstance();

        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        lv = (ListView) view.findViewById(R.id.dailyGoalList);
        final EditText etWeek = (EditText) view.findViewById(R.id.etWeek);
        ImageButton btnPrevious = (ImageButton) view.findViewById(R.id.btnPrevious);
        ImageButton btnNext = (ImageButton) view.findViewById(R.id.btnNext);

        dbHelper = new DatabaseHelper(view.getContext());

        //Set List
        dgList = dbHelper.getDailyGoalList(getEvent().getId());
        adapter = new DailyGoalsListAdapter(getActivity(), dgList);
        lv.setAdapter(adapter);
        lv.setEmptyView(view.findViewById(R.id.empty));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(view.getContext(), "Clicked!", Toast.LENGTH_SHORT).show();
                DailyGoal dg = (DailyGoal) lv.getItemAtPosition(position);
                Intent i = new Intent(getContext(), DailyGoalsActivity.class);
                i.putExtra("event", getEvent());
                i.putExtra("daily_goal", dg);
                getActivity().startActivityForResult(i,1);
            }
        });

        //Set week
        String week = HelperClass.getWeek(myCal);
        etWeek.setText(week);

        weeklyGoal = dbHelper.getLastWeeklyGoal(getEvent().getId());

        //Set buttons
        btnPrevious.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myCal.add(myCal.DATE, -7);
                etWeek.setText(HelperClass.getWeek(myCal));
                updateList();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myCal.add(myCal.DATE, 7);
                etWeek.setText(HelperClass.getWeek(myCal));
                updateList();
            }
        });

        return view;
    }

    private void updateList()
    {

    }

}
