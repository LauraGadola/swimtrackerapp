package cobaltix.internal_projects.swimtrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TabFragment1 extends MyFragment
{
    private DatabaseHelper dbHelper;
    private ListView lv;
    private ArrayList dgList;
    private DailyGoalsListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        lv = (ListView) view.findViewById(R.id.dailyGoalList);

        dbHelper = new DatabaseHelper(view.getContext());
        dgList = dbHelper.getDailyGoalList(getEvent().getId());
//        if(!dgList.isEmpty())
//        {
            adapter = new DailyGoalsListAdapter(getActivity(), dgList);
            lv.setAdapter(adapter);
            System.out.println("***********we have a list**************");
//        }
//        else
//        {
//            //show 'list is empty'
//            Toast.makeText(getContext(), "Empty list", Toast.LENGTH_SHORT).show();
//        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                System.out.println(position);
                Toast.makeText(view.getContext(), "Clicked!", Toast.LENGTH_SHORT).show();
                DailyGoal dg = (DailyGoal) lv.getItemAtPosition(position);
                Intent i = new Intent(getContext(), DailyGoalsActivity.class);
                i.putExtra("event", getEvent());
                i.putExtra("daily_goal", dg);
                startActivity(i);

            }
        });

        return view;
    }

}
