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
import com.umbrella.budgetapp.Database.Adapters.DaoStores;
import com.umbrella.budgetapp.Database.Collections.Store;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import butterknife.OnClick;

public class Stores extends BaseFragment {
    private DaoStores adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetToolbar(getString(R.string.title_Stores),
                getParentFragmentManager().getBackStackEntryCount() == 0 ?
                ToolbarNavIcon.MENU :
                ToolbarNavIcon.BACK);

        getObject(R.id.fragmentFloatingActionButton).setVisibility(View.VISIBLE);

        Log.d("_Test", "[Stores] onViewCreated() ");
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = firestore.collection(Store.COLLECTION).whereEqualTo(Store.USER, app.getUser());
        FirestoreRecyclerOptions<Store> options = new FirestoreRecyclerOptions.Builder<Store>().setQuery(query, Store.class).build();
        adapter = new DaoStores(options);
        bindRecyclerView(getObject(R.id.fragmentRecyclerView), adapter);

        adapter.setOnItemClickListener((documentSnapshot, position) ->
                navigate(StoresDirections.storesToUpdateStore().setStoreID(documentSnapshot.getReference().getPath())));
        Log.d("_Test", "[Stores] setUpRecyclerView() ");
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        Log.d("_Test", "[Stores] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d("_Test", "[Stores] onStop() ");
    }

    @OnClick(R.id.fragmentFloatingActionButton)
    protected void onClick() {
        Log.d("_Test", "[Stores] onClick() ");
        navigate(StoresDirections.storesToUpdateStore());
    }
}

