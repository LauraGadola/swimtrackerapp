package cobaltix.internal_projects.swimtrackerapp;

import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class CustomOnFocusChangeListener implements View.OnFocusChangeListener
{
    private DailyLogsActivity activity;
    private TextWatcher watcher;
    private EditText et;

    public CustomOnFocusChangeListener(DailyLogsActivity activity)
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
            //I need to remove it to prevent recursion since I change text in afterTextChanged which causes the method to be called for the other field
            if(watcher != null)
            {
                et.removeTextChangedListener(watcher);
            }
        }
    }
}
