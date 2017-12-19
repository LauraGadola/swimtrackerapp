package cobaltix.internal_projects.swimtrackerapp;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by lgadola on 10/20/17.
 */
public class CustomTextWatcher implements TextWatcher
{
    DailyLogsActivity activity;
    EditText et;

    public CustomTextWatcher(DailyLogsActivity activity, EditText et)
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
        String str = s.toString();
        float f;
        switch (et.getId())
        {
            case R.id.etTempF:
                if(!str.matches(""))
                {
                    f = Float.parseFloat(str);
                    activity.convertFtoC(f);
                }
                else
                    activity.clearC();
                break;

            case R.id.etTempC:
                if(!str.matches(""))
                {
                    f = Float.parseFloat(str);
                    activity.convertCtoF(f);
                }
                else
                    activity.clearF();
                break;

            case R.id.etWeekLongest:
                if(!str.matches(""))
                {
                    f = Float.parseFloat(str);
                    activity.convertMtoY(f);
                }
                else
                    activity.clearY();
                break;

            case R.id.etYards:
                if(!str.matches(""))
                {
                    f = Float.parseFloat(str);
                    activity.convertYtoM(f);
                }
                else
                    activity.clearM();
                break;
        }
    }
}
