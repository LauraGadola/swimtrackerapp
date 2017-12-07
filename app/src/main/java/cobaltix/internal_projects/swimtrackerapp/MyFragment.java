package cobaltix.internal_projects.swimtrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public abstract class MyFragment extends Fragment
{
    private String week;
    private Bundle bundle;
    public Event getEvent()
    {
        bundle = this.getArguments();
        Event event = null;
        if (bundle != null)
            event = (Event) bundle.getSerializable("Event");
        return event;
    }

    public String getWeek()
    {
        //todo to test
        if(week == null)
        {
            bundle = this.getArguments();
            if (bundle != null)
                week = bundle.getString("week");
        }
        return week;
    }

    public String setWeek(String week)
    {
        this.week = week;
        return week;
    }

}
