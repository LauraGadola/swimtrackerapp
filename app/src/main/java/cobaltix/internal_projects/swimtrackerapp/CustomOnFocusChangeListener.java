package cobaltix.internal_projects.swimtrackerapp;

import android.app.Activity;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class CustomOnFocusChangeListener implements View.OnFocusChangeListener
{
    private DailyGoalsActivity activity;
    private TextWatcher watcher;
    private EditText et;

    public CustomOnFocusChangeListener(DailyGoalsActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        et = (EditText) v;
        if(hasFocus)
        {
            watcher = new CustomTextWatcher(activity, et);
            et.addTextChangedListener(watcher);
        }
        else
        {
            if(watcher != null)
            {
                et.removeTextChangedListener(watcher);
            }
        }
    }
}
