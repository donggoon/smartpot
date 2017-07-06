package inandout.pliend.store;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import inandout.pliend.fragment.PlantMainFragment;
import inandout.pliend.fragment.MachineMainFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    // Count number of tabs
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        // Returning the current tabs
        switch (position) {
            case 0:
                PlantMainFragment mainFragment = new PlantMainFragment();
                return mainFragment;

            case 1:
                MachineMainFragment registerFragment = new MachineMainFragment();
                return registerFragment;
            default:
                return null;
        }
    }
    private static int NUM_OF_VIEWS = 2;
    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

}