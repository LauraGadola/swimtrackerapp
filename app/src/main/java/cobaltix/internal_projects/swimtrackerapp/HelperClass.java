package cobaltix.internal_projects.swimtrackerapp;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HelperClass
{
    private HelperClass(){}

    public static String getWeek(Calendar myCal)  {
        Log.e("HelperClass","date: "+ myCal.getTime().toString());
        int dayOfWeek = myCal.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek != Calendar.SUNDAY)
        {
            //Set the date to the Sunday of that week
            myCal.add(Calendar.DATE, -(dayOfWeek-1));
        }

        String sunFormat = "MMM dd";
        String satFormat = "MMM dd, yyyy";
        SimpleDateFormat sdfSun = new SimpleDateFormat(sunFormat);
        SimpleDateFormat sdfSat = new SimpleDateFormat(satFormat);

        String sunday = sdfSun.format(myCal.getTime());

        //Move the calendar to the last day of the week (Sat)
        myCal.add(myCal.DATE, 6);
        String sat = sdfSat.format(myCal.getTime());

        return sunday+" - "+sat;
    }

}
