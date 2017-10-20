package cobaltix.internal_projects.swimtrackerapp;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by lgadola on 10/20/17.
 */
public class CustomTextWatcher implements TextWatcher
{
    DailyGoalsActivity activity;
    EditText et;

    public CustomTextWatcher(DailyGoalsActivity activity, EditText et)
    {
        this.activity = activity;
        this.et = et;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s)
    {
        switch (et.getId())
        {
            case R.id.etTempF:
                if(!s.toString().matches(""))
                {
                    float tempF = Float.parseFloat(s.toString());
                    activity.covertFtoC(tempF);

                }
                else {
                    activity.clearC();
                }
                break;

            case R.id.etTempC:
                if(!s.toString().matches(""))
                {
                    float tempC = Float.parseFloat(s.toString());
                    activity.covertCtoF(tempC);

                }
                else
                {
                    activity.clearF();
                }
                break;
        }
    }
}
