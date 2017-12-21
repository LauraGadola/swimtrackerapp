package cobaltix.internal_projects.swimtrackerapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Date;
import java.util.List;

public class StatsTab extends MyFragment
{
    private Event event;
    private DatabaseHelper dbHelper;
    private List<DailyLog> dgList;

    private GraphView graph;
    private LinearLayout btnGroup;
    private Button btnMiles;
    private Button btnWeight;
    private Button btnFocus;
    private LinearLayout empty;

    private DataPoint[] milesList;
    private DataPoint[] weightList;

    private ProgressBar progressBarMiles;
    private ProgressBar progressBarLongest;
    private ProgressBar progressBarWeight;
    private TextView txtMilesPercent;
    private TextView txtLongestPercent;
    private TextView txtWeightPercent;

    private LinearLayout percentageLayout;

    private float longest;
    private float totDist;

    private WeeklyGoal weeklyGoal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        System.out.println("-------------- Tab 2 ------------------");
        View v = inflater.inflate(R.layout.content_fragment_charts, container, false);

        graph = (GraphView) v.findViewById(R.id.graph);
        btnGroup = (LinearLayout) v.findViewById(R.id.buttonsLine);
        btnMiles = (Button) v.findViewById(R.id.btnMiles);
        btnWeight = (Button) v.findViewById(R.id.btnWeight);
        empty = (LinearLayout) v.findViewById(R.id.empty);
        progressBarMiles = (ProgressBar) v.findViewById(R.id.progressBarMiles);
        progressBarLongest = (ProgressBar) v.findViewById(R.id.progressBarLongest);
        progressBarWeight = (ProgressBar) v.findViewById(R.id.progressBarWeight);
        txtMilesPercent = (TextView) v.findViewById(R.id.txtMilesPercent);
        txtLongestPercent = (TextView) v.findViewById(R.id.txtLongestPercent);
        txtWeightPercent = (TextView) v.findViewById(R.id.txtWeightPercent);
        percentageLayout = (LinearLayout) v.findViewById(R.id.percentageLayout);

        dbHelper = new DatabaseHelper(getContext());

        setElements();

        return v;
    }

    public void setElements()
    {
        event = getEvent();

        weeklyGoal = dbHelper.getWeeklyGoal(getWeek());
        System.out.println("tab2: WG: "+weeklyGoal);

        dgList = dbHelper.getDailyLogList(getWeek());
        System.out.println("List: "+dgList);
        if(!dgList.isEmpty())
        {
            empty.setVisibility(View.INVISIBLE);

            graph.setVisibility(View.VISIBLE);
            btnGroup.setVisibility(View.VISIBLE);
            percentageLayout.setVisibility(View.VISIBLE);



            //SET GRAPH
            milesList = new DataPoint[dgList.size()];
            weightList = new DataPoint[dgList.size()];
            int i = dgList.size()-1;
            for (DailyLog dg : dgList)
            {
                Date date = DateFormatter.parse(dg.getDate());
                DataPoint milesDP = new DataPoint(date, dg.getMiles());
                milesList[i] = milesDP;

                DataPoint weightDP = new DataPoint(date, dg.getWeight());
                weightList[i] = weightDP;

                i--;
            }

            btnMiles.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    setBtnFocus(btnMiles);
                    renderGraph("Miles");
                }
            });

            btnWeight.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    setBtnFocus(btnWeight);
                    graph.removeAllSeries();

                    renderGraph("Weight");
                }
            });

            //TODO change depending on button click

            //default at page start
            btnFocus = btnWeight;
            setBtnFocus(btnMiles);
            renderGraph("Miles");

            //SET PERCENTAGES
            WeeklyGoal weeklyGoal = dbHelper.getWeeklyGoal(getWeek());
            System.out.println("WG Miles: "+weeklyGoal.getMiles() + " / " + totDist);
            System.out.println("WG Longest: "+weeklyGoal.getLongest() + " / " + longest);
            System.out.println("WG Weight: "+weeklyGoal.getWeight() + " / " + dgList.get(0).getWeight());

            int percent;
            percent = (int) (totDist/weeklyGoal.getMiles()*100);
            progressBarMiles.setProgress(percent);
            txtMilesPercent.setText(String.valueOf(percent)+"%");

//            longest = (float) graph.getViewport().getMaxY(true);
            percent = (int) (longest/weeklyGoal.getLongest()*100);
            progressBarLongest.setProgress(percent);
            txtLongestPercent.setText(String.valueOf(percent)+"%");

            System.out.println("Last DG weight: "+dgList.get(0).getWeight());
            percent = (int) (dgList.get(0).getWeight() / weeklyGoal.getWeight()*100);
            progressBarWeight.setProgress(percent);
            txtWeightPercent.setText(String.valueOf(percent)+"%");

        }
        else {
            graph.setVisibility(View.INVISIBLE);
            btnGroup.setVisibility(View.INVISIBLE);
            percentageLayout.setVisibility(View.INVISIBLE);

            empty.setVisibility(View.VISIBLE);
        }
    }

    public void renderGraph(String title)
    {
        System.out.println("Rendering graph.........");
        float goal = 0;
        DataPoint[] list = null;
        int min = 0;
        switch(title)
        {
            case "Miles":
                list = milesList;
                goal = weeklyGoal.getMiles();
                min = 0;
                break;
            case "Weight":
                list = weightList;
                goal = weeklyGoal.getWeight();
                min = 150;
                break;
            default: break;
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(list);
        series.setTitle(title);

        graph.removeAllSeries();
        graph.addSeries(series);
        series.setColor(Color.rgb(21,157,231));
        series.setDrawBackground(true);
        series.setDrawDataPoints(true);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        if(list.length < 10)        //todo list should always be max 7
            graph.getGridLabelRenderer().setNumHorizontalLabels(list.length);
        else
            graph.getGridLabelRenderer().setNumHorizontalLabels(10);

        graph.getGridLabelRenderer().setNumVerticalLabels(10);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(120);
        graph.getGridLabelRenderer().setPadding(40);
        graph.getGridLabelRenderer().setLabelsSpace(15);

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(list[0].getX());
        graph.getViewport().setMaxX(list[list.length-1].getX());
        graph.getViewport().setMinY(min);
        graph.getViewport().setMaxY(goal);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
//        graph.getViewport().setScalable(true);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setBackgroundColor(Color.LTGRAY);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        // as we use dates as labels, the human rounding to nice readable number is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    public void setBtnFocus(Button b)
    {
        b.setBackground(getContext().getDrawable(R.drawable.button_focus));
        b.setTextColor(getContext().getColor(R.color.colorBackground));

        if(!btnFocus.equals(b))
        {
            //Put out of focus the other button
            btnFocus.setBackground(getContext().getDrawable(R.drawable.button_unfocus));
            btnFocus.setTextColor(getContext().getColor(R.color.colorAccent));
            btnFocus = b;
        }
    }

    public void setLongest(float longest)
    {
        this.longest = longest;
    }

    public void setTotDist(float totDist)
    {
        this.totDist = totDist;
    }
}
