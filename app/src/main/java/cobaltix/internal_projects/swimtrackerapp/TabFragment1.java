package cobaltix.internal_projects.swimtrackerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class TabFragment1 extends MyFragment
{
    private static DatabaseHelper dbHelper;

    private View view;
    private ListView lv;
    private TextView totDist;
    private TextView longest;
    private LinearLayout totDistLayout;
    private LinearLayout longestLayout;

    private static LinkedList<DailyGoal> dgList;
    private static DailyGoalsListAdapter adapter;

    private OnLongestCalculatedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        System.out.println("----------- Tab 1 ------------");

        view = inflater.inflate(R.layout.fragment_overview, container, false);
        lv = (ListView) view.findViewById(R.id.dailyGoalList);
        totDist = (TextView) view.findViewById(R.id.etTotDist);
        longest = (TextView) view.findViewById(R.id.etLongest);
        totDistLayout = (LinearLayout) view.findViewById(R.id.totalDistLayout);
        longestLayout = (LinearLayout) view.findViewById(R.id.longestLayout);

        setList();

        showResults();

        return view;
    }

    public void setList()
    {
        dbHelper = new DatabaseHelper(getActivity());
        dgList = dbHelper.getDailyGoalList(getWeek(), getEvent().getId());
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
        if(dgList == null)
        {
            totDistLayout.setVisibility(View.INVISIBLE);
            longestLayout.setVisibility(View.INVISIBLE);
        }
        else
        {
            totDistLayout.setVisibility(View.VISIBLE);
            longestLayout.setVisibility(View.VISIBLE);
        }
    }

    public void showResults()
    {
        if(dgList != null)
        {
            float totMiles = 0;
            float longMile = 0;
            for (DailyGoal dg : dgList)
            {
                float miles = dg.getMiles();
                totMiles += miles;
                if (miles > longMile)
                    longMile = miles;
            }

            totDist.setText(String.valueOf(totMiles));
            longest.setText(String.valueOf(longMile));
            listener.sendLongest(longMile, totMiles);
        }
    }

    public interface OnLongestCalculatedListener
    {
        void sendLongest(float longest, float miles);
    }

    //the system calls it when adding the fragment to the activity
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try {
            listener = (OnLongestCalculatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLongestCalculatedListener");
        }
    }

    public void updateListView(DailyGoal dg)
    {
        if(dg != null)
        {
            int i = dgList.indexOf(dg);
            dgList.set(i, dg);
        }
        adapter.notifyDataSetChanged();
        showResults();
    }

    public void addToListView(DailyGoal dg)
    {
        if(dg != null)
        {
            dgList.addFirst(dg);
        }
        adapter.notifyDataSetChanged();
        showResults();
    }

    public void removeFromListView(DailyGoal dg)
    {
        if(dg != null)
        {
            dgList.remove(dg);
        }
        adapter.notifyDataSetChanged();
        showResults();
    }
}
