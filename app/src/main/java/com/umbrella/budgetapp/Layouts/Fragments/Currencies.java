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
import com.umbrella.budgetapp.Database.Adapters.DaoCurrencies;
import com.umbrella.budgetapp.Database.Collections.Currency;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import butterknife.OnClick;

public class Currencies extends BaseFragment {
    private DaoCurrencies adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetToolbar(getString(R.string.title_Currencies), ToolbarNavIcon.BACK);
        setNavigationOnClickListener(v -> navigateUp());

        Log.d("_Test", "[Currencies] onViewCreated() ");

        setUpRecyclerView();

        getObject(R.id.fragmentFloatingActionButton).setVisibility(View.VISIBLE);

    }

    private void setUpRecyclerView() {
        Query query = app.getUserDocument().collection(Currency.COLLECTION).orderBy(Currency.POSITION, Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Currency> options = new FirestoreRecyclerOptions.Builder<Currency>().setQuery(query, Currency.class).build();
        adapter = new DaoCurrencies(options);
        bindRecyclerView(getObject(R.id.fragmentRecyclerView), adapter);
        adapter.setOnItemClickListener(((documentSnapshot, position) ->
                CurrenciesDirections.currenciesToUpdateCurrency().setCurrencyID(documentSnapshot.getReference().getPath())));

        Log.d("_Test", "[Currencies] setUpRecyclerView() ");
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        Log.d("_Test", "[Currencies] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d("_Test", "[Currencies] onStop() ");
    }

    @OnClick(R.id.fragmentFloatingActionButton)
    protected void onAddClicked() {
        Log.d("_Test", "[Currencies] onAddClicked() ");
        navigate(CurrenciesDirections.currenciesToUpdateCurrency());
    }
}

