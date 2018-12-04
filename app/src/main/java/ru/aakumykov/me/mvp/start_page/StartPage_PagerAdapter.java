package ru.aakumykov.me.mvp.start_page;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.ArrayMap;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;


public class StartPage_PagerAdapter extends FragmentPagerAdapter {

    private ArrayMap<Integer,Fragment> fragmentsMap = new ArrayMap<Integer,Fragment>();

    StartPage_PagerAdapter(FragmentManager fm, Map<Integer,Fragment> fragmentsMap) {
        super(fm);
        this.fragmentsMap.putAll(fragmentsMap);
    }

    @Override
    public Fragment getItem(int i) {
        int key = fragmentsMap.keyAt(i);
        Fragment f = fragmentsMap.valueAt(i);
        Fragment fByIndex = fragmentsMap.get(key);
        return f;
    }

    @Override
    public int getCount() {
        return fragmentsMap.size();
    }
}
