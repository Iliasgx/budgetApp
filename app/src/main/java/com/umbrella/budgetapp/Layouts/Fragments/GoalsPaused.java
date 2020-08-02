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
import com.umbrella.budgetapp.Database.Adapters.DaoGoals;
import com.umbrella.budgetapp.Database.Collections.Goal;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Layouts.ContentHolders.GoalsDirections;
import com.umbrella.budgetapp.R;

public class GoalsPaused extends BaseFragment {
    private DaoGoals adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("_Test", "[GoalsPaused] onViewCreated() ");
        initData();
    }

    private void initData() {
        Log.d("_Test", "[GoalsPaused] initData() ");
        Query query = firestore.collection(Goal.COLLECTION).whereEqualTo(Goal.USER, app.getUser())
                .whereEqualTo(Goal.STATUS, Goal.StatusItem.PAUSED.ordinal())
                .orderBy(Goal.DESIRED_DATE, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Goal> options = new FirestoreRecyclerOptions.Builder<Goal>().setQuery(query, Goal.class).build();
        adapter = new DaoGoals(options, Goal.StatusItem.PAUSED);

        bindRecyclerView(getObject(R.id.fragmentRecyclerView), adapter);

        adapter.setOnItemClickListener((snapshot, e) -> navigate(GoalsDirections.goalsToUpdateGoalDetails().setGoalID(snapshot.getReference().getPath())));
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        Log.d("_Test", "[GoalsPaused] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d("_Test", "[GoalsPaused] onStop() ");
    }
}

