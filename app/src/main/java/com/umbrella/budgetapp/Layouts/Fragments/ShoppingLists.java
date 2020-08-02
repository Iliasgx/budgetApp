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
import com.umbrella.budgetapp.Database.Adapters.DaoShoppingLists;
import com.umbrella.budgetapp.Database.Collections.ShoppingList;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import butterknife.OnClick;

public class ShoppingLists extends BaseFragment {
    private DaoShoppingLists adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetToolbar(getString(R.string.title_ShoppingLists),
                getParentFragmentManager().getBackStackEntryCount() != 0 ?
                ToolbarNavIcon.BACK :
                ToolbarNavIcon.MENU);
        setNavigationOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() != 0) navigateUp();
        });

        getObject(R.id.fragmentFloatingActionButton).setVisibility(View.VISIBLE);

        Log.d("_Test", "[ShoppingLists] onViewCreated() ");

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = firestore.collection(ShoppingList.COLLECTION).whereEqualTo(ShoppingList.USER, app.getUser()).orderBy(ShoppingList.NAME, Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<ShoppingList> options = new FirestoreRecyclerOptions.Builder<ShoppingList>().setQuery(query, ShoppingList.class).build();
        query.get().addOnSuccessListener(ready -> {
            Log.d("_Test", "[ShoppingLists] setUpRecyclerView() ");

            adapter = new DaoShoppingLists(options, ready.getDocuments());
            bindRecyclerView(getObject(R.id.fragmentRecyclerView), adapter);

            adapter.setOnItemClickListener((documentSnapshot, position) ->
                    navigate(ShoppingListsDirections.shoppingListToShoppingListItems(documentSnapshot.getReference().getPath())));

            adapter.setOnAddItemClick((documentSnapshot, position) -> {
                // TODO: add items through shoppingListItems
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        Log.d("_Test", "[ShoppingLists] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d("_Test", "[ShoppingLists] onStop() ");
    }

    @OnClick(R.id.fragmentFloatingActionButton)
    protected void onClick() {
        Log.d("_Test", "[ShoppingLists] onClick() ");
        navigate(ShoppingListsDirections.shoppingListsToUpdateShoppingList());
    }

}

