package cobaltix.internal_projects.swimtrackerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class TabFragment1 extends MyFragment
{
    private DatabaseHelper dbHelper;

    private View view;
    private ListView lv;
    private TextView totDist;
    private TextView longest;
    private LinearLayout totDistLayout;
    private LinearLayout longestLayout;

    private LinkedList<DailyLog> dgList;
    private DailyLogsListAdapter adapter;

    private OnLongestCalculatedListener listener;

    //todo - duplicate
    private Calendar myCal = Calendar.getInstance();
    private Date today = myCal.getTime();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        System.out.println("----------- Tab 1 ------------");

        view = inflater.inflate(R.layout.fragment_overview, container, false);
        lv = (ListView) view.findViewById(R.id.dailyLogsList);
        totDist = (TextView) view.findViewById(R.id.etTotDist);
        longest = (TextView) view.findViewById(R.id.etLongest);
        totDistLayout = (LinearLayout) view.findViewById(R.id.totalDistLayout);
        longestLayout = (LinearLayout) view.findViewById(R.id.longestLayout);

        populateListView();

        showResults();

        return view;
    }

    public void populateListView()
    {
        dbHelper = new DatabaseHelper(getActivity());
        dgList = dbHelper.getDailyLogList(getWeek());

        adapter = new DailyLogsListAdapter(getActivity(), dgList);
        lv.setAdapter(adapter);
        lv.setEmptyView(view.findViewById(R.id.empty));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                DailyLog dg = (DailyLog) lv.getItemAtPosition(position);
                Intent i = new Intent(getContext(), DailyLogsActivity.class);
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
            checkForButton();
        }
    }

    private void checkForButton()
    {
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        Date startDate = DateFormatter.parse(getEvent().getStartDate());
        Date today = Calendar.getInstance().getTime();

        if(startDate.after(today) || dgList.size() == 7)
        {
            fab.setVisibility(View.INVISIBLE);
        }
        else
        {
            String day = HelperClass.getFirstDay(getWeek());
            while (!isStartDateOrAfter(day) || getLog(day) != null)
            {
                day = getDay(day, 1);
            }
            Date d = DateFormatter.parse(day);
            if (d.after(today) || d.after(DateFormatter.parse(getEvent().getEndDate())))
            {
                fab.setVisibility(View.INVISIBLE);
            } else
                fab.setVisibility(View.VISIBLE);
        }

    }

    //todo change - duplicate from DGA
    private boolean isStartDateOrAfter(String day)
    {
        boolean isStartDate = day.equals(getEvent().getStartDate());
        boolean afterStartDate = DateFormatter.parse(day).after(DateFormatter.parse(getEvent().getStartDate()));
        return  isStartDate || afterStartDate;
    }

    //todo change - duplicate from DGA
    private String getDay(String date, int beforeOrAfter)
    {
        Date d = DateFormatter.parse(date);
        myCal.setTime(d);
        myCal.add(myCal.DATE, beforeOrAfter);
        return DateFormatter.format(myCal.getTime());
    }

    //todo change - duplicate from DGA
    private DailyLog getLog(String day)
    {
        for (DailyLog dg : dgList)
        {
            if (dg.getDate().equals(day))
            {
                return dg;
            }
        }
        return null;
    }



    public void showResults()
    {
        if(dgList != null)
        {
            float totMiles = 0;
            float longMile = 0;
            for (DailyLog dg : dgList)
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

//
//    public void updateListView(DailyLog dg)
//    {
//        if(dg != null)
//        {
//            int i = dgList.indexOf(dg);
//            dgList.set(i, dg);
//        }
//        adapter.notifyDataSetChanged();
//        showResults();
//    }
//
//    public void addToListView(DailyLog dg)
//    {
//        if(dg != null)
//        {
//            dgList.addFirst(dg);
//        }
//        adapter.notifyDataSetChanged();
//        showResults();
//    }
//
//    public void removeFromListView(DailyLog dg)
//    {
//        if(dg != null)
//        {
//            dgList.remove(dg);
//        }
//        adapter.notifyDataSetChanged();
//        showResults();
//    }

    public LinkedList<DailyLog> getList()
    {
        return dgList;
    }

    public void setList(LinkedList<DailyLog> list)
    {
        System.out.println("tab1: List: "+list);
        dgList.clear();
        dgList.addAll(list);
        adapter.notifyDataSetChanged();
        showResults();
    }

}
