package cobaltix.internal_projects.swimtrackerapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String>
{
    private final Activity context;
    private final String[] titles;
    private final String[] dates;
    public CustomList(Activity context,
                      String[] titles, String[] dates) {
        super(context, R.layout.list_single, titles);
        this.context = context;
        this.titles = titles;
        this.dates = dates;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
//        TextView txtTitle = (TextView) rowView.findViewById(R.id.);
//
//        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
//        txtTitle.setText(web[position]);
//
//        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}

