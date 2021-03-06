package cobaltix.internal_projects.swimtrackerapp;

import java.io.Serializable;

/**
 * Created by lgadola on 11/5/17.
 */
public class DailyLog implements Serializable
{
    private int id;
    private String date;
    private String location;
    private float temp;
    private int hrs;
    private int min;
    private float weight;
    private float miles;
    private float honest;
    private String notes;
    private int weekly_id;
    private int event_id;

    //TODO Do I need to save weeks left?
    public DailyLog(String date, String location, float temp, int hrs, int min, float weight,
                    float miles, float honest, String notes, int weekly_id, int event_id)
    {
        this.date = date;
        this.location = location;
        this.temp = temp;
        this.hrs = hrs;
        this.min = min;
        this.weight = weight;
        this.miles = miles;
        this.honest = honest;
        this.notes = notes;
        this.weekly_id = weekly_id;
        this.event_id = event_id;
    }

    public DailyLog(int id, String date, String location, float temp, int hrs, int min, float weight,
                    float miles, float honest, String notes, int weekly_id, int event_id)
    {
        this(date, location, temp, hrs, min, weight, miles, honest, notes, weekly_id, event_id);
        this.id = id;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public float getTemp()
    {
        return temp;
    }

    public void setTemp(float temp)
    {
        this.temp = temp;
    }

    public int getHrs()
    {
        return hrs;
    }

    public void setHrs(int hrs)
    {
        this.hrs = hrs;
    }

    public int getMin()
    {
        return min;
    }

    public void setMin(int min)
    {
        this.min = min;
    }

    public float getWeight()
    {
        return weight;
    }

    public void setWeight(float weight)
    {
        this.weight = weight;
    }

    public float getMiles()
    {
        return miles;
    }

    public void setMiles(float miles)
    {
        this.miles = miles;
    }

    public float getHonest()
    {
        return honest;
    }

    public void setHonest(float honest)
    {
        this.honest = honest;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String description)
    {
        this.notes = description;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getWeekly_id()
    {
        return weekly_id;
    }

    public void setWeekly_id(int weekly_id)
    {
        this.weekly_id = weekly_id;
    }

    public int getEvent_id()
    {
        return event_id;
    }

    public void setEvent_id(int event_id)
    {
        this.event_id = event_id;
    }

    @Override
    public String toString()
    {
        return "DailyLog{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                ", temp=" + temp +
                ", hrs=" + hrs +
                ", min=" + min +
                ", weight=" + weight +
                ", miles=" + miles +
                ", honest=" + honest +
                ", notes='" + notes + '\'' +
                ", weekly_id=" + weekly_id +
                ", event_id=" + event_id +
                '}';
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;

        DailyLog otherDG = (DailyLog) obj;

        if (otherDG.id != this.id)
        {
            return false;
        }
        return true;
    }
}