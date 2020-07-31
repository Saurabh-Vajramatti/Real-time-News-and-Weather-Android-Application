package com.example.newsapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HeadlinePageAdapter extends FragmentPagerAdapter {

    private int headlineNumberOfTabs;

    public HeadlinePageAdapter(FragmentManager fm, int headlineNumberOfTabs) {
        super(fm);
        this.headlineNumberOfTabs= headlineNumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new worldTab();
            case 1:
                return new businessTab();
            case 2:
                return new politicsTab();
            case 3:
                return new sportsTab();
            case 4:
                return new technologyTab();
            case 5:
                return new scienceTab();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return headlineNumberOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
