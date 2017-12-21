package cobaltix.internal_projects.swimtrackerapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter
{
    private int numOfTabs;
    private Event event;
    private String week;

    private OverviewTab tab1;
    private StatsTab tab2;

    public SectionsPagerAdapter(FragmentManager fm, int numOfTabs, Event e, String week)
    {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.event = e;
        this.week = week;
    }

    @Override
    public Fragment getItem(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Event", event);
        bundle.putString("week", week);
        switch (position)
        {
            case 0:
                MyFragment tab1 = new OverviewTab();
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                MyFragment tab2 = new StatsTab();
                tab2.setArguments(bundle);
                return tab2;
            default:
                return null;
        }
        // getItem is called to instantiate the fragment for the given page.
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // save the appropriate reference depending on position
        switch (position) {
            case 0:
                tab1 = (OverviewTab) createdFragment;
                break;
            case 1:
                tab2 = (StatsTab) createdFragment;
                break;
        }
        return createdFragment;
    }

    public Fragment getTab(int position)
    {
        switch (position)
        {
            case 0:
                return tab1;
            case 1:
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return numOfTabs;
    }
}
