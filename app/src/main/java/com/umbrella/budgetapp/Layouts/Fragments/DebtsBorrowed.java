package com.umbrella.budgetapp.Layouts.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Database.Adapters.DaoDebts;
import com.umbrella.budgetapp.Database.Collections.Debt;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Layouts.ContentHolders.DebtsDirections;
import com.umbrella.budgetapp.R;

public class DebtsBorrowed extends BaseFragment {
    private DaoDebts adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("_Test", "[DebtsBorrowed] onViewCreated() ");
        initData();
    }

    private void initData() {
        Query query = firestore.collection(Debt.COLLECTION).whereEqualTo(Debt.USER, app.getUser())
                .whereEqualTo(Debt.DEBT_TYPE, Debt.DebtType.BORROWED.ordinal())
                .orderBy(Debt.DATE, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Debt> options = new FirestoreRecyclerOptions.Builder<Debt>().setQuery(query, Debt.class).build();
        adapter = new DaoDebts(options, Debt.DebtType.BORROWED);

        bindRecyclerView(getObject(R.id.fragmentRecyclerView), adapter);

        adapter.setOnItemClickListener((snapshot, e) -> navigate(DebtsDirections.debtsToUpdateDebt().setDebtID(snapshot.getReference().getPath())));
        adapter.onCreateRecordClickListener(((snapshot, position) -> {
            // TODO: maybe a dialog or navigate to new record?
        }));
        Log.d("_Test", "[DebtsBorrowed] initData() ");
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        Log.d("_Test", "[DebtsBorrowed] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d("_Test", "[DebtsBorrowed] onStop() ");
    }
}

