package com.umbrella.budgetapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umbrella.budgetapp.Extensions.BaseFragment;

public class Login extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout._login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.requireViewById(R.id.loginButon).setOnClickListener(v -> ((MainActivity)requireActivity()).signIn());
        Log.d("_Test", "[Login] onViewCreated() ");
    }
}
