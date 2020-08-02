package com.umbrella.budgetapp.Layouts.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Database.Adapters.DaoAccounts;
import com.umbrella.budgetapp.Database.Collections.Account;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import butterknife.OnClick;

public class Accounts extends BaseFragment {
    private DaoAccounts adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetToolbar(getString(R.string.title_Accounts), ToolbarNavIcon.BACK);
        setNavigationOnClickListener(v -> navigateUp());

        Log.d("_Test", "[Accounts] onViewCreated() ");
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = app.getUserDocument().collection(Account.COLLECTION).orderBy(Account.POSITION, Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Account> options = new FirestoreRecyclerOptions.Builder<Account>().setQuery(query, Account.class).build();
        adapter = new DaoAccounts(options);

        RecyclerView recyclerView = getObject(R.id.fragmentRecyclerView);
        bindRecyclerView(recyclerView, adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN ,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { }
        }).attachToRecyclerView(recyclerView);

        Log.d("_Test", "[Accounts] setUpRecyclerView() ");
        adapter.setOnItemClickListener((which, pos) -> navigate(AccountsDirections.accountsToUpdateAccount().setAccountID(which.getReference().getPath())));
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        Log.d("_Test", "[Accounts] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d("_Test", "[Accounts] onStop() ");
    }

    @OnClick(R.id.fragmentFloatingActionButton)
    protected void onActionButtonClicked() {
        Log.d("_Test", "[Accounts] onActionButtonClicked() ");
        navigate(AccountsDirections.accountsToUpdateAccount());
    }
}