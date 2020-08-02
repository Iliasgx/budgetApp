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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Database.Collections.Template;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Layouts.Fragments.HomeAccounts;
import com.umbrella.budgetapp.Layouts.Fragments.HomeInformation;
import com.umbrella.budgetapp.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class Home extends BaseFragment implements FloatingActionButton.OnClickListener {

    @BindView(R.id.homeFAB) FloatingActionMenu menu;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TabLayout tabLayout = getObject(R.id.home_TabLayout);
        bindTabLayout(new PagerAdapter(getChildFragmentManager(), tabLayout.getTabCount()), tabLayout, getObject(R.id.home_ViewPager));

        SetToolbar(getString(R.string.title_Home), ToolbarNavIcon.MENU);

        updateMenu();
    }

    private void updateMenu() {
        menu.removeAllMenuButtons();
        app.getUserDocument().collection(Template.COLLECTION)
                .orderBy(Template.POSITION, Query.Direction.ASCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(tps -> {
                    Log.d("_Test", "[Home] updateMenu() onSuccessListener");
                    tps.forEach(template -> addFab(menu, this, template.toObject(Template.class).getName(), R.drawable.template_icon, template.getReference()));
                    addFab(menu, this, getString(R.string.home_newRecordFAB), R.drawable.edit, "record");
                });
    }

    @Override
    public void onClick(View v) {
        menu.close(true);
        if (v.getTag().equals("record")) {
            Log.d("_Test", "[Home] onClick() record click");
            navigate(HomeDirections.homeToNewRecord());
        } else {
            Log.d("_Test", "[Home] onClick() other click");
            ((DocumentReference)v.getTag()).get().addOnSuccessListener(rc -> {
                Template template = Objects.requireNonNull(rc.toObject(Template.class));
                Record record = new Record();

                record.setAmount(template.getAmount());
                record.setPaymentType(template.getPaymentType());
                record.setType(template.getType());
                record.setCurrencyID(template.getCurrencyID());
                record.setStoreID(template.getStoreID());
                record.setAccountID(template.getAccountID());
                record.setCategoryID(template.getCategoryID());
                record.setPayee(template.getPayee());
                record.setDescription(template.getName() + "\n" + template.getNote());

                Log.d("_Test", "[Home] onClick() " + record.toString());

                navigate(HomeDirections.homeToNewRecord().setRecord(record));
            });
        }
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
               return new HomeInformation();
           } else {
               return new HomeAccounts();
           }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getResources().getString(R.string.home_Tab2);
            } else {
                return getResources().getString(R.string.home_Tab1);
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
