package inandout.pliend.store;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import inandout.pliend.fragment.AnalyzeFragment;
import inandout.pliend.fragment.PlantMainFragment;
import inandout.pliend.fragment.MachineMainFragment;
import inandout.pliend.fragment.QuestFragment;

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
                PlantMainFragment plantMainFragment = new PlantMainFragment();
                return plantMainFragment;

            case 1:
                MachineMainFragment machineMainFragment = new MachineMainFragment();
                return machineMainFragment;

            case 2:
                QuestFragment questFragment = new QuestFragment();
                return questFragment;

            case 3:
                AnalyzeFragment analyzeFragment = new AnalyzeFragment();
                return analyzeFragment;

            default:
                return null;
        }
    }
    private static int NUM_OF_VIEWS = 4;
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