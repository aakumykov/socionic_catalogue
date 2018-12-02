package ru.aakumykov.me.mvp.start_page;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;


public class StartPage_PagerAdapter extends FragmentPagerAdapter {

    private SparseArray<Fragment> fragmentsMap = new SparseArray<>();

    public StartPage_PagerAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);
        for (int i=0; i<fragments.length; i++) {
            Fragment f = fragments[i];
            fragmentsMap.append(i, f);
        }
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentsMap.get(i);
    }

    @Override
    public int getCount() {
        return fragmentsMap.size();
    }
}
