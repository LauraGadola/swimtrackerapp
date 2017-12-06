package cobaltix.internal_projects.swimtrackerapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class TabFragment1 extends MyFragment
{
    private DatabaseHelper dbHelper;
    private ListView lv;
    private ArrayList<DailyGoal> dgList;
    private DailyGoalsListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        System.out.println("----------- Tab 1 ------------");

        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        lv = (ListView) view.findViewById(R.id.dailyGoalList);

        //Set List
        Log.e("tab1","week: "+getWeek());
        setList(view, getWeek());

        return view;
    }

    public void setList(View v, String week)
    {
        dbHelper = new DatabaseHelper(getActivity());
        dgList = dbHelper.getDailyGoalList(week, getEvent().getId());
        adapter = new DailyGoalsListAdapter(getActivity(), dgList);
        lv.setAdapter(adapter);
        lv.setEmptyView(v.findViewById(R.id.empty));
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
    }

}
