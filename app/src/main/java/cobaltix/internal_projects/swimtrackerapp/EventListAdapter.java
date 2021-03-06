package cobaltix.internal_projects.swimtrackerapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class EventListAdapter extends ArrayAdapter<Event>
{
    private Activity context;
    private Event event;
    private int selectedItem;


    public EventListAdapter(Activity context, ArrayList<Event> events) {
        super(context, 0, events);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        event = getItem(position);

        if(convertView == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.event_list_single, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
        TextView tvDate = (TextView) convertView.findViewById(R.id.date);

        tvTitle.setText(event.getTitle());
        tvDate.setText(event.getEventDate());

        return convertView;
    }
}

