package com.umbrella.budgetapp.Layouts.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Database.Adapters.DaoRecords;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Date;
import java.util.Objects;

public class Records extends BaseFragment {
    private DaoRecords adapter;

    private String textFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_records, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetToolbar(getString(R.string.title_Records), getParentFragmentManager().getBackStackEntryCount() != 0 ?
                ToolbarNavIcon.BACK :
                ToolbarNavIcon.MENU,
                R.menu.search);
        setNavigationOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() != 0) navigateUp();
        });
        setOnMenuItemClickListener(v -> {
            SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView)v.getActionView();

            if (searchView != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
                searchView.setIconifiedByDefault(true);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        textFilter = newText;
                        updateAdapter();
                        v.collapseActionView();
                        return true;
                    }
                });
            }
            return true;
        });
        updateAdapter();
    }

    private void updateAdapter() {
        Query query = firestore.collection(Record.COLLECTION).whereEqualTo(Record.USER, app.getUser()).orderBy(Record.DATETIME, Query.Direction.DESCENDING);

        String[] filter = app.splitFilterString(app.getRecordsFilter());

        // TODO: create the filter with the items of {filter}.
        //query.where ...

        if (textFilter != null && !textFilter.isEmpty()) {
            query.whereGreaterThanOrEqualTo(Record.DESCRIPTION, textFilter);
        }

        FirestoreRecyclerOptions<Record> options = new FirestoreRecyclerOptions.Builder<Record>().setQuery(query, Record.class).build();
        adapter = new DaoRecords(options);
        bindRecyclerView(getObject(R.id.fragment_records_recyclerView), adapter);
        adapter.setOnItemClickListener((documentSnapshot, position) ->
                navigate(RecordsDirections.recordsToUpdateRecordDetails().setRecordID(documentSnapshot.getReference().getPath())));
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // TODO: fix filter in test
    private void onFilterSelect(int period, int option, int week, Date start, Date end) {
        app.setRecordsFilter(app.matchFilterString(period, option, week, start, end));
        updateAdapter();
    }
}