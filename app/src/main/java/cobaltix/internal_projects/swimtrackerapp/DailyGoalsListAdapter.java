package cobaltix.internal_projects.swimtrackerapp;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class DailyGoalsListAdapter extends ArrayAdapter<DailyGoal>
{
    private Activity context;
    private DailyGoal dg;

    public DailyGoalsListAdapter(Activity context, ArrayList<DailyGoal> dailyGoals) {
        super(context, 0, dailyGoals);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        dg = getItem(position);

        if(convertView == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.daily_goal_list_single, parent, false);
        }

        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView weekDay = (TextView) convertView.findViewById(R.id.weekDay);
        TextView miles = (TextView) convertView.findViewById(R.id.miles);
        TextView honest = (TextView) convertView.findViewById(R.id.honest);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView weight = (TextView) convertView.findViewById(R.id.weight);
        TextView temp = (TextView) convertView.findViewById(R.id.temp);
        final TextView notes = (TextView) convertView.findViewById(R.id.notes);
        ImageButton dots = (ImageButton) convertView.findViewById(R.id.dots);
        notes.setVisibility(View.GONE);

        String s = String.valueOf(dg.getDate());
        String d = s.substring(s.indexOf(",")+1);
        String w = s.substring(0, s.indexOf(","));

        date.setText(d);
        weekDay.setText(w);
        miles.setText(String.valueOf(dg.getMiles()));
        honest.setText(String.valueOf(dg.getHonest()));
        time.setText(dg.getHrs() + ":" + dg.getMin());
        weight.setText(String.valueOf(dg.getWeight()));
        temp.setText(String.valueOf(dg.getTemp()));
        notes.setText(String.valueOf(dg.getNotes()));

        dots.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (notes.getVisibility() == View.GONE)
                {
                    notes.setVisibility(View.VISIBLE);

                } else
                {
                    notes.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }


}