package cobaltix.internal_projects.swimtrackerapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TabFragment2 extends MyFragment
{
    private Event event;
    private DatabaseHelper dbHelper;
    private List<DailyGoal> dgList;
    private String myFormat = "EEEE, MMM dd, yyyy";
    private SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

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
        dgList = dbHelper.getDailyGoalList(getWeek());
        System.out.println("List: "+dgList);
        if(!dgList.isEmpty())
        {
            empty.setVisibility(View.INVISIBLE);

            graph.setVisibility(View.VISIBLE);
            btnGroup.setVisibility(View.VISIBLE);
            percentageLayout.setVisibility(View.VISIBLE);

            //SET PERCENTAGES
            WeeklyGoal weeklyGoal = dbHelper.getWeeklyGoal(getWeek());
            System.out.println("WG Miles: "+weeklyGoal.getMiles() + " / " + totDist);
            System.out.println("WG Longest: "+weeklyGoal.getLongest() + " / " + longest);
            System.out.println("WG Weight: "+weeklyGoal.getWeight() + " / " + dgList.get(0).getWeight());

            int percent;
            percent = (int) (totDist/weeklyGoal.getMiles()*100);
            progressBarMiles.setProgress(percent);
            txtMilesPercent.setText(String.valueOf(percent)+"%");

            percent = (int) (longest/weeklyGoal.getLongest()*100);
            progressBarLongest.setProgress(percent);
            txtLongestPercent.setText(String.valueOf(percent)+"%");

            System.out.println("Last DG weight: "+dgList.get(0).getWeight());
            percent = (int) (dgList.get(0).getWeight() / weeklyGoal.getWeight()*100);
            progressBarWeight.setProgress(percent);
            txtWeightPercent.setText(String.valueOf(percent)+"%");

            //SET GRAPH
            milesList = new DataPoint[dgList.size()];
            weightList = new DataPoint[dgList.size()];
            int i = 0;
            for (DailyGoal dg : dgList)
            {
                Date date = null;
                try
                {
                    date = sdf.parse(dg.getDate());
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }

                DataPoint milesDP = new DataPoint(date, dg.getMiles());
                milesList[i] = milesDP;

                DataPoint weightDP = new DataPoint(date, dg.getWeight());
                weightList[i] = weightDP;

                i++;
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
                    renderGraph("Weight");
                }
            });

            //TODO change depending on button click

            btnFocus = btnMiles;
            setBtnFocus(btnMiles);
            renderGraph("Miles");
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
        DataPoint[] list = null;
        switch(title)
        {
            case "Miles":
                list = milesList;
                break;
            case "Weight":
                list = weightList;
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
        if(list.length < 10)
            graph.getGridLabelRenderer().setNumHorizontalLabels(list.length);
        else
            graph.getGridLabelRenderer().setNumHorizontalLabels(10);

        graph.getGridLabelRenderer().setNumVerticalLabels(10);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(135);
        graph.getGridLabelRenderer().setPadding(64);

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(list[0].getX());
        graph.getViewport().setMinY(0);
//        graph.getViewport().setMaxY();                                           //TODO set to the weekly goal
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setScalable(true);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setBackgroundColor(Color.LTGRAY);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        // as we use dates as labels, the human rounding to nice readable number is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    //TODO improve
    public  void setBtnFocus(Button b)
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
