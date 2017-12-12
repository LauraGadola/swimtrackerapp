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
    private String eventDate;
    private String startDate;
    private boolean done;
    private boolean upToDate;

    public Event (int id, String title, String startDate, String eventDate)
    {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.eventDate = eventDate;
    }

    public String getEndDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy");
        Calendar myCal = Calendar.getInstance();
        try
        {
            Date d = sdf.parse(eventDate);
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

    public String getEventDate()
    {
        return eventDate;
    }

    public void setTitle(String t)
    {
        title = t;
    }

    public void setEventDate(String d)
    {
        eventDate = d;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public boolean isDone()
    {
        return done;
    }

    public void setDone(boolean done)
    {
        this.done = done;
    }

    public boolean isUpToDate()
    {
        return upToDate;
    }

    public void setUpToDate(boolean upToDate)
    {
        this.upToDate = upToDate;
    }

    @Override
    public String toString()
    {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", eventDate='" + eventDate + '\'' +
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
