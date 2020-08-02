package com.umbrella.budgetapp.Layouts.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Database.Adapters.DaoPlannedPayments;
import com.umbrella.budgetapp.Database.Collections.PlannedPayment;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class PlannedPayments extends BaseFragment {
    private DaoPlannedPayments adapter;

    @BindView(R.id.plannedPaymentsFAB) FloatingActionMenu menu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_planned_payments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetToolbar(getString(R.string.title_PlannedPayments), getParentFragmentManager().getBackStackEntryCount() != 0 ?
                ToolbarNavIcon.BACK :
                ToolbarNavIcon.MENU,
                R.menu.sort);
        setOnMenuItemClickListener(item -> {
            new SortingDialog().getInstance();
            return true;
        });
        setNavigationOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() != 0) navigateUp();
        });
        menu.setOnMenuButtonClickListener(listener -> {
            if (menu.isOpened()) menu.close(true);
        });

        Log.d("_Test", "[PlannedPayments] onViewCreated() ");
        setUpRecyclerView(0);
    }

    private void setUpRecyclerView(int filterOption) {
        Query query = firestore.collection(PlannedPayment.COLLLECTION).whereEqualTo(PlannedPayment.USER, app.getUser());

        // TODO: fix filterOptions with PlannedPayment.Date
        switch (filterOption) {
            case 0:
                query.orderBy(PlannedPayment.NAME, Query.Direction.ASCENDING);
                break;
            case 1:
                query.orderBy(PlannedPayment.NAME, Query.Direction.DESCENDING);
                break;
            case 2:
                //query.orderBy(null);
                break;
            case 3:
                //query.orderBy(null);
                break;
        }

        Log.d("_Test", "[PlannedPayments] setUpRecyclerView() ");

        FirestoreRecyclerOptions<PlannedPayment> options = new FirestoreRecyclerOptions.Builder<PlannedPayment>().setQuery(query, PlannedPayment.class).build();
        adapter = new DaoPlannedPayments(options);
        bindRecyclerView(getObject(R.id.planned_payments_recyclerView), adapter);

        adapter.setOnItemClickListener((documentSnapshot, position) ->
                navigate(PlannedPaymentsDirections.plannedPaymentsToUpdatePlannedPayment().setPlannedPaymentID(documentSnapshot.getReference().getPath())));
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        Log.d("_Test", "[PlannedPayments] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d("_Test", "[PlannedPayments] onStop() ");
    }

    @OnClick({R.id.plannedPaymentsFAB_expenses, R.id.plannedPaymentsFAB_income})
    protected void onClick(FloatingActionButton button) {
        Log.d("_Test", "[PlannedPayments] onClick() ");
        menu.close(true);
        navigate(PlannedPaymentsDirections.plannedPaymentsToUpdatePlannedPayment().setPlannedPaymentType((int)button.getTag()));
    }

    public class SortingDialog extends DialogFragment {

        public void getInstance() {
            SortingDialog dialog = new SortingDialog();
            dialog.setTargetFragment(this, 300);
            dialog.show(getParentFragmentManager(), "SORT");
            Log.d("_Test", "[SortingDialog] getInstance() ");
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog_sort, container);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getObject(R.id.dialog_Sort_Cancel).setOnClickListener(v -> dismiss());
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);

            switch (app.getPlannedPaymentsSorting()) {
                case 0:
                    ((RadioButton)getObject(R.id.dialog_Sort_Item1)).toggle();
                    break;
                case 1:
                    ((RadioButton)getObject(R.id.dialog_Sort_Item2)).toggle();
                    break;
                case 2:
                    ((RadioButton)getObject(R.id.dialog_Sort_Item3)).toggle();
                    break;
                case 3:
                    ((RadioButton)getObject(R.id.dialog_Sort_Item4)).toggle();
                    break;
            }
            return dialog;
        }

        @OnClick(R.id.dialog_Sort_Default)
        public void sendBackResult() {
            Log.d("_Test", "[SortingDialog] sendBackResult() set default");

            app.setPlannedPaymentsSorting(-1);
            setUpRecyclerView(-1);
            dismiss();
        }

        @OnClick({R.id.dialog_Sort_Item1, R.id.dialog_Sort_Item2, R.id.dialog_Sort_Item3, R.id.dialog_Sort_Item4})
        public void onItemSelected(RadioButton button) {
            Log.d("_Test", "[SortingDialog] onItemSelected() item: " + button.getTag().toString());
            app.setPlannedPaymentsSorting((Integer)button.getTag());
            setUpRecyclerView((int)button.getTag());
            dismiss();
        }

    }
}

