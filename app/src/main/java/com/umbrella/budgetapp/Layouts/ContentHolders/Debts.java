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

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.tabs.TabLayout;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Layouts.Fragments.DebtsBorrowed;
import com.umbrella.budgetapp.Layouts.Fragments.DebtsLent;
import com.umbrella.budgetapp.R;

import butterknife.BindView;
import butterknife.OnClick;

public class Debts extends BaseFragment {

    @BindView(R.id.debtsFAB) FloatingActionMenu menu;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_debts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TabLayout tabLayout = getObject(R.id.debts_TabLayout);
        bindTabLayout(new PagerAdapter(getChildFragmentManager(), tabLayout.getTabCount()), tabLayout, getObject(R.id.debts_ViewPager));
    
        SetToolbar(getString(R.string.title_Debts), ToolbarNavIcon.MENU);

        menu.setOnMenuButtonClickListener(listener -> {
            if (menu.isOpened()) menu.close(true);
        });

        Log.d("_Test", "[Debts] onViewCreated() ");
    }

    @OnClick({R.id.debtsFAB_lent, R.id.debtsFAB_borrowed})
    protected void onNewDebt(FloatingActionButton button) {
        menu.close(true);
        navigate(DebtsDirections.debtsToUpdateDebt().setDebtType(Integer.valueOf(button.getTag().toString())));
        Log.d("_Test", "[Debts] onNewDebt() ");
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
                return new DebtsBorrowed();
            } else {
                return new DebtsLent();
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getResources().getString(R.string.debts_Tab2);
            } else {
                return getResources().getString(R.string.debts_Tab1);
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}

