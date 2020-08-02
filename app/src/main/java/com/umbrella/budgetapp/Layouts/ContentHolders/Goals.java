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

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.tabs.TabLayout;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Layouts.Fragments.GoalsActive;
import com.umbrella.budgetapp.Layouts.Fragments.GoalsPaused;
import com.umbrella.budgetapp.Layouts.Fragments.GoalsReached;
import com.umbrella.budgetapp.R;

import butterknife.BindView;

public class Goals extends BaseFragment {

    @BindView(R.id.goalsFAB) FloatingActionMenu menu;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TabLayout tabLayout = getObject(R.id.goals_TabLayout);
        bindTabLayout(new PagerAdapter(getChildFragmentManager(), tabLayout.getTabCount()), tabLayout, getObject(R.id.goals_ViewPager));

        SetToolbar(getString(R.string.title_Goals), ToolbarNavIcon.MENU);

        menu.setOnMenuButtonClickListener(listener -> {
            if (menu.isOpened()) {
                menu.close(true);
            } else {
                navigate(GoalsDirections.goalsToUpdateGoalSelect());
            }
        });

        Log.d("_Test", "[Goals] onViewCreated() ");
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
                return new GoalsPaused();
            } else if (position == 2) {
                return new GoalsReached();
            } else {
                return new GoalsActive();
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getResources().getString(R.string.goals_Tab2);
            } else if (position == 2) {
                return getResources().getString(R.string.goals_Tab3);
            } else {
                return getResources().getString(R.string.goals_Tab1);
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}

