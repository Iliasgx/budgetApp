package com.umbrella.budgetapp.Layouts.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umbrella.budgetapp.BuildConfig;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Calendar;

public class About extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetToolbar(getString(R.string.title_About), ToolbarNavIcon.BACK);

        ((TextView)getObject(R.id.about_app_version)).setText(BuildConfig.VERSION_NAME);
        ((TextView)getObject(R.id.about_app_information)).setText(getString(R.string.app_info));
        ((TextView)getObject(R.id.about_copyright)).setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));

        ((ListView)getObject(R.id.about_developers_list)).setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, getStringArrayList(R.array.developersNames)));
        ((ListView)getObject(R.id.about_names_list)).setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, getStringArrayList(R.array.basedOnNames)));

        setNavigationOnClickListener(listener -> navigateUp());

        Log.d("_Test", "[About] onViewCreated() ");
    }
}

