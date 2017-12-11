package cobaltix.internal_projects.swimtrackerapp;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Event implements Serializable
{
    private int id;
    private String title;
    private String date;
    private boolean done;

    public Event (int id, String t, String d)
    {
        this.id = id;
        this.title = t;
        this.date = d;
    }

    public String getEndDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy");
        Calendar myCal = Calendar.getInstance();
        try
        {
            Date d = sdf.parse(date);
            myCal.setTime(d);
            myCal.add(Calendar.DATE, -1);   //Day prior to the event
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        String endDate = sdf.format(myCal.getTime());
        return endDate;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDate()
    {
        return date;
    }

    public void setTitle(String t)
    {
        title = t;
    }

    public void setDate(String d)
    {
        date = d;
    }

    public boolean isDone()
    {
        return done;
    }

    public void setDone(boolean done)
    {
        this.done = done;
    }

    @Override
    public String toString()
    {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;

        Event otherEvent = (Event) obj;

        if (otherEvent.id != this.id)
        {
            System.out.println("id false");
            return false;
        }
        System.out.println("id true");
        return true;
    }
}
