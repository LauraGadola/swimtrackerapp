package cobaltix.internal_projects.swimtrackerapp;


import java.io.Serializable;

public class Event implements Serializable
{
    private int id;
    private String title;
    private String date;

    public Event (int id, String t, String d)
    {
        this.id = id;
        this.title = t;
        this.date = d;
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
}
