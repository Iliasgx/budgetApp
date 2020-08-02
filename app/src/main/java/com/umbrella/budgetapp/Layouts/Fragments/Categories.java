package com.umbrella.budgetapp.Layouts.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Database.Adapters.DaoCategories;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Objects;

public class Categories extends BaseFragment implements DaoCategories.OnItemClickListener {
    private DaoCategories adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetToolbar(getString(R.string.title_Categories), ToolbarNavIcon.BACK, R.menu.search);
        setOnMenuItemClickListener(item -> {
            SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView)item.getActionView();

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
                        updateAdapter(newText);
                        item.collapseActionView();
                        return true;
                    }
                });
            }
            return true;
        });
        setNavigationOnClickListener(nav -> {
            ((InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), 0);
            navigateUp();
        });

        Log.d("_Test", "[Categories] onViewCreated() ");
    }

    private void updateAdapter(@Nullable String filter) {
        Query query = app.getUserDocument().collection(Category.COLLECTION);
        if (filter != null && !filter.isEmpty()) {
            query.whereGreaterThanOrEqualTo(Category.NAME, filter);
        }

        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>().setQuery(query, Category.class).build();
        adapter = new DaoCategories(options);
        bindRecyclerView(getObject(R.id.fragmentRecyclerView), adapter);
        adapter.setOnItemClickListener(this);

        Log.d("_Test", "[Categories] updateAdapter() ");
    }

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
        navigate(CategoriesDirections.categoriesToUpdateCategory().setCategoryID(documentSnapshot.getReference().getPath()));
        Log.d("_Test", "[Categories] onItemClick() ");
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        Log.d("_Test", "[Categories] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d("_Test", "[Categories] onStop() ");
    }
}

