package com.umbrella.budgetapp.Layouts.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umbrella.budgetapp.Database.Collections.Account;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.List;

import butterknife.OnClick;

/*
This class needs to be sort out how to import and export data.
 */

public class Imports extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_imports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetToolbar(getString(R.string.title_Imports), ToolbarNavIcon.MENU);

        app.getUserDocument().collection(Account.COLLECTION).limit(4).get().addOnSuccessListener(data -> {
            List<Account> items = data.toObjects(Account.class);

            ((TextView)getObject(R.id.imports_Card_Account1_Name)).setText(items.get(0).getName());
            ((TextView)getObject(R.id.imports_Card_Account2_Name)).setText(items.get(1).getName());
            ((TextView)getObject(R.id.imports_Card_Account3_Name)).setText(items.get(2).getName());
            ((TextView)getObject(R.id.imports_Card_Account4_Name)).setText(items.get(3).getName());
        });

        Log.d("_Test", "[Imports] onViewCreated() ");
    }

    @OnClick({R.id.imports_Card_App_Import,
              R.id.imports_Card_Account1_Import,
              R.id.imports_Card_Account2_Import,
              R.id.imports_Card_Account3_Import,
              R.id.imports_Card_Account4_Import,
              R.id.imports_Card_New_Create})
    protected void onClick() {
        Log.d("_Test", "[Imports] onClick() ");
        String message = "This function is not available yet.";
        showSnackbar(message);
    }
}

