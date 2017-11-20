package cobaltix.internal_projects.swimtrackerapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter
{
    int numOfTabs;
    Event event;

    public SectionsPagerAdapter(FragmentManager fm, int numOfTabs, Event e)
    {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.event = e;
    }

    @Override
    public Fragment getItem(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Event", event);
        switch (position)
        {
            case 0:
                MyFragment tab1 = new TabFragment1();
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                MyFragment tab2 = new TabFragment2();
                tab2.setArguments(bundle);
                return tab2;
            default:
                return null;
        }
        // getItem is called to instantiate the fragment for the given page.
    }

    @Override
    public int getCount()
    {
        return numOfTabs;
    }
}
