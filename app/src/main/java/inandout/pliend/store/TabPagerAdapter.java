package inandout.pliend.store;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import inandout.pliend.app.AppController;
import inandout.pliend.fragment.AnalyzeFragment;
import inandout.pliend.fragment.DisplayFragment;
import inandout.pliend.fragment.PlantFragment;
import inandout.pliend.fragment.MachineFragment;
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
        final Fragment result;
        switch (position) {
            case 0:
                // PlantFragment plantMainFragment = new PlantFragment();
                // return plantMainFragment;
                result = new PlantFragment();
                break;
            case 1:
                /*MachineFragment machineMainFragment = new MachineFragment();
                return machineMainFragment;*/
                result = new MachineFragment();
                break;
            case 2:
                /*QuestFragment questFragment = new QuestFragment();
                return questFragment;*/
                result = new QuestFragment();
                break;
            case 3:
                /*AnalyzeFragment analyzeFragment = new AnalyzeFragment();
                return analyzeFragment;*/
                result = new DisplayFragment();
                break;
            default:
                // return null;
                result = null;
                break;
        }
        return result;
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
        // return POSITION_UNCHANGED;
    }
}