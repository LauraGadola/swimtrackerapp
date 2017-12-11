package cobaltix.internal_projects.swimtrackerapp;

import java.io.Serializable;

public class WeeklyGoal implements Serializable
{
    private int id;
    private String weekStart;
    private float miles;
    private float longest;
    private float weight;
    private String description;

    public WeeklyGoal(String weekStart, float miles, float longest, float weight, String description)
    {
        this.weekStart = weekStart;
        this.miles = miles;
        this.longest = longest;
        this.weight = weight;
        this.description = description;
    }

    //TODO needed or can I just set the id?
    public WeeklyGoal(int id, String weekStart, float miles, float longest, float weight, String description)
    {
        this(weekStart, miles, longest, weight, description);
        this.id = id;
    }

    public String getWeekStart()
    {
        return weekStart;
    }

    public void setWeekStart(String weekStart)
    {
        this.weekStart = weekStart;
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

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "WeeklyGoal{" +
                "id=" + id +
                ", weekStart='" + weekStart + '\'' +
                ", miles=" + miles +
                ", longest=" + longest +
                ", weight=" + weight +
                ", description='" + description + '\'' +
                '}';
    }
}
