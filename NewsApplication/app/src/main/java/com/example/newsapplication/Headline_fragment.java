package com.example.newsapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class Headline_fragment extends Fragment {

    private TabLayout headlineTablayout;
    private ViewPager headlineViewPager;
    private TabItem worldTab, businessTab, politicsTab, sportsTab, technologyTab, scienceTab;
    public HeadlinePageAdapter headlinePagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        View view=inflater.inflate(R.layout.headline_fragment,container,false);

        headlineTablayout= (TabLayout) view.findViewById(R.id.headline_tab_layout);
        worldTab=(TabItem) view.findViewById(R.id.world_tab);
        businessTab=(TabItem) view.findViewById(R.id.business_tab);
        politicsTab=(TabItem) view.findViewById(R.id.politics_tab);
        sportsTab=(TabItem) view.findViewById(R.id.sports_tab);
        technologyTab=(TabItem) view.findViewById(R.id.technology_tab);
        scienceTab=(TabItem) view.findViewById(R.id.science_tab);

        headlineViewPager=(ViewPager) view.findViewById(R.id.headlines_viewpager);

        headlinePagerAdapter= new HeadlinePageAdapter(getChildFragmentManager(),headlineTablayout.getTabCount());
        headlineViewPager.setAdapter(headlinePagerAdapter);

        headlineTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                headlineViewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0)
                {
                    headlinePagerAdapter.notifyDataSetChanged();
                }
                if(tab.getPosition()==1)
                {
                    headlinePagerAdapter.notifyDataSetChanged();
                }
                if(tab.getPosition()==2)
                {
                    headlinePagerAdapter.notifyDataSetChanged();
                }
                if(tab.getPosition()==3)
                {
                    headlinePagerAdapter.notifyDataSetChanged();
                }
                if(tab.getPosition()==4)
                {
                    headlinePagerAdapter.notifyDataSetChanged();
                }
                if(tab.getPosition()==5)
                {
                    headlinePagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        headlineViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(headlineTablayout));
        return view;
    }
}
