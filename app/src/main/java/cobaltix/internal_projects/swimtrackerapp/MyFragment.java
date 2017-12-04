package cobaltix.internal_projects.swimtrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class MyFragment extends Fragment
{
    Event event = null;

    public Event getEvent()
    {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            event = (Event) bundle.getSerializable("Event");
        }
        return event;
    };

}
