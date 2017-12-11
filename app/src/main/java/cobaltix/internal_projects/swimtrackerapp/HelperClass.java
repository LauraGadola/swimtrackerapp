package cobaltix.internal_projects.swimtrackerapp;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HelperClass
{
    private static String sunFormat = "MMM dd";
    private static String satFormat = "MMM dd, yyyy";
    private static SimpleDateFormat sdfSun = new SimpleDateFormat(sunFormat);
    private static SimpleDateFormat sdfSat = new SimpleDateFormat(satFormat);

    //Constructor
    private HelperClass(){}

    public static String getWeek(Calendar myCal)  {
        int dayOfWeek = myCal.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek != Calendar.SUNDAY)
        {
            //Set the date to the Sunday of that week
            myCal.add(Calendar.DATE, -(dayOfWeek-1));
        }

        String sunday = sdfSun.format(myCal.getTime());

        //Move the calendar to the last day of the week (Sat)
        myCal.add(myCal.DATE, 6);
        String sat = sdfSat.format(myCal.getTime());

        return sunday+" - "+sat;
    }

    public static String getFirstDay(String week)
    {
        String day = week.substring(0, week.indexOf("-")-1);
        String year = week.substring(week.indexOf(",", week.indexOf(",")));
        String sunday = "Sunday, "+ day + year;
        System.out.println("First day of week: "+sunday);
        return sunday;
    }
}
