package cobaltix.internal_projects.swimtrackerapp;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter
{
    private static SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy");

    private DateFormatter(){}

    public static String format(Date d)
    {
        String date = sdf.format(d);
        return date;
    }

    public static Date parse(String date)
    {
        Date d = null;
        try
        {
            d = sdf.parse(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return d;
    }
}
