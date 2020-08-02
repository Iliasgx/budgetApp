package com.umbrella.budgetapp.Layouts.ContentHolders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Layouts.Fragments.StatisticsBalance;
import com.umbrella.budgetapp.Layouts.Fragments.StatisticsCashflow;
import com.umbrella.budgetapp.Layouts.Fragments.StatisticsReports;
import com.umbrella.budgetapp.Layouts.Fragments.StatisticsSpending;
import com.umbrella.budgetapp.R;

import java.util.Calendar;

public class Statistics extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TabLayout tabLayout = getObject(R.id.statistics_TabLayout);
        final ViewPager viewPager = getObject(R.id.statistics_ViewPager);
        bindTabLayout(new PagerAdapter(getChildFragmentManager(), tabLayout.getTabCount()), tabLayout, viewPager);

        viewPager.setCurrentItem(StatisticsArgs.fromBundle(requireArguments()).getPage());

        SetToolbar(getString(R.string.title_Statistics), getParentFragmentManager().getBackStackEntryCount() != 0 ?
                ToolbarNavIcon.BACK :
                ToolbarNavIcon.MENU);
        setNavigationOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() != 0) navigateUp();
        });

        Log.d("_Test", "[Statistics] onViewCreated() ");
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        private PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.mNumOfTabs = NumOfTabs;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                return new StatisticsCashflow();
            } else if (position == 2) {
                return new StatisticsReports();
            } else if (position == 3) {
                return new StatisticsSpending();
            } else {
                return new StatisticsBalance();
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getResources().getString(R.string.statistics_Tab2);
            } else if (position == 2) {
                return getResources().getString(R.string.statistics_Tab3);
            } else if (position == 3) {
                return getResources().getString(R.string.statistics_Tab4);
            } else {
                return getResources().getString(R.string.statistics_Tab1);
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    public static class QueryFilter {

        public static Query setFilter() {
            Log.d("_Test", "[QueryFilter] setFilter() ");
            Calendar[] cls = getPeriods();
            return firestore.collection(Record.COLLECTION)
                    .whereEqualTo(Record.USER, app.getUser())
                    .whereGreaterThan(Record.DATETIME, new Timestamp(cls[0].getTime()))
                    .whereLessThanOrEqualTo(Record.DATETIME, new Timestamp(cls[1].getTime()));
        }

        public static Calendar[] getPeriods() {
            String[] filter = app.splitFilterString(app.getStatisticsFilter());

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();

            start.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_YEAR), 0,0,0);

            switch (filter[0]) {
                case "1":
                    switch (filter[1]) {
                        case "1":
                            start.add(Calendar.DAY_OF_YEAR, -7);
                            break;
                        case "2":
                            start.add(Calendar.MONTH, -1);
                            break;
                        case "3":
                            start.add(Calendar.MONTH, -6);
                            break;
                        case "4":
                            start.add(Calendar.YEAR, -1);
                            break;
                    }
                    break;
                case "2":
                    start.set(Calendar.WEEK_OF_YEAR, Integer.valueOf(filter[2]));
                    end.setTimeInMillis(start.getTimeInMillis());
                    end.add(Calendar.DAY_OF_YEAR, 6);
                    break;
                case "3":
                    start.set(Integer.valueOf(filter[5]), Integer.valueOf(filter[4]), Integer.valueOf(filter[3]));
                    end.set(Integer.valueOf(filter[8]), Integer.valueOf(filter[7]), Integer.valueOf(filter[6]));
                    break;
            }

            Log.d("_Test", "[QueryFilter] getPeriods() ");
            return new Calendar[]{start, end};
        }
    }
}
