package cobaltix.internal_projects.swimtrackerapp;

public class WeeklyGoal
{
    private long id;
    private String week;
    private float miles;
    private float longest;
    private float weight;
    private String description;
    private long event_id;

    public WeeklyGoal(long id, String week, float miles, float longest, float weight, String description, long event_id)
    {
        this.id = id;
        this.week = week;
        this.miles = miles;
        this.longest = longest;
        this.weight = weight;
        this.description = description;
        this.event_id = event_id;
    }

    public String getWeek()
    {
        return week;
    }

    public void setWeek(String week)
    {
        this.week = week;
    }

    public float getMiles()
    {
        return miles;
    }

    public void setMiles(float miles)
    {
        this.miles = miles;
    }

    public float getLongest()
    {
        return longest;
    }

    public void setLongest(float longest)
    {
        this.longest = longest;
    }

    public float getWeight()
    {
        return weight;
    }

    public void setWeight(float weight)
    {
        this.weight = weight;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getEvent_id()
    {
        return event_id;
    }

    public void setEvent_id(long event_id)
    {
        this.event_id = event_id;
    }

    public boolean exist()
    {
        if (this == null)
            return false;
        return true;
    }
}
