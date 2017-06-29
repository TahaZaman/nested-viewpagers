package com.example.project;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements Switcher{


    @Override
    public void switchTabs(int tab) {
        viewPager.setCurrentItem(tab);
    }



    TabLayout tabLayout;
    ViewPager viewPager;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) v.findViewById(R.id.view_pager);


        FragementsAdapter fragementsAdapter = new FragementsAdapter(getChildFragmentManager());


        viewPager.setAdapter(fragementsAdapter);


        tabLayout.setupWithViewPager(viewPager);


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        MainActivity mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {
            int tab = mainActivity.homeTab;


            viewPager.setCurrentItem(tab);
        }
    }

    private class FragementsAdapter extends FragmentPagerAdapter {

        public final int PAGE_COUNT = 3;

        private final String[] mTabsTitle = {"Chats", "Active", "Groups"};

        public FragementsAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    return new ChatFragment();

                case 1:
                    return new ActiveFragment();
                case 2:
                    return new GroupsFragment();

                default:
                    return new ChatFragment();

            }


        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position]; // check here for active user by checking on position==1 (2nd tab index)
        }
    }

}
