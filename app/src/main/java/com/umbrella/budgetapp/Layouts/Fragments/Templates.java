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
import com.umbrella.budgetapp.Database.Adapters.DaoTemplates;
import com.umbrella.budgetapp.Database.Collections.Template;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import butterknife.OnClick;

public class Templates extends BaseFragment {
    private DaoTemplates adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetToolbar(getString(R.string.title_Templates), ToolbarNavIcon.BACK);
        setNavigationOnClickListener(v -> navigateUp());

        getObject(R.id.fragmentFloatingActionButton).setVisibility(View.VISIBLE);

        Log.d("_Test", "[Templates] onViewCreated() ");
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = app.getUserDocument().collection(Template.COLLECTION).orderBy(Template.POSITION, Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Template> options = new FirestoreRecyclerOptions.Builder<Template>().setQuery(query, Template.class).build();
        adapter = new DaoTemplates(options);
        bindRecyclerView(getObject(R.id.fragmentRecyclerView), adapter);

        adapter.setOnItemClickListener((documentSnapshot, position) ->
                TemplatesDirections.templatesToUpdateTemplate().setTemplateID(documentSnapshot.getReference().getPath()));
        Log.d("_Test", "[Templates] setUpRecyclerView() ");
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        Log.d("_Test", "[Templates] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d("_Test", "[Templates] onStop() ");
    }

    @OnClick(R.id.fragmentFloatingActionButton)
    protected void onClick() {
        Log.d("_Test", "[Templates] onClick() ");
        TemplatesDirections.templatesToUpdateTemplate();
    }
}

