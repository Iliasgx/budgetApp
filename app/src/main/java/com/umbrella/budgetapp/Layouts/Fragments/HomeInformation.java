package com.umbrella.budgetapp.Layouts.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Customs.DetailedProgressbar;
import com.umbrella.budgetapp.Database.Adapters.DaoPlannedPayments;
import com.umbrella.budgetapp.Database.Collections.Debt;
import com.umbrella.budgetapp.Database.Collections.PlannedPayment;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Extensions.Functions;
import com.umbrella.budgetapp.Layouts.ContentHolders.HomeDirections;
import com.umbrella.budgetapp.R;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

// TODO: fix something for ShoppingLists
public class HomeInformation extends BaseFragment {
    private DaoPlannedPayments adapterPlannedPayments;

    @BindView(R.id.home_Card_PlannedPayments_upcoming) RecyclerView upcomingPlannedPayments;
    @BindView(R.id.home_Card_Debts_Progressbar_Lent) DetailedProgressbar PgbLent;
    @BindView(R.id.home_Card_Debts_Progressbar_Borrowed) DetailedProgressbar PgbBorrowed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("_Test", "[HomeInformation] onViewCreated() ");
        setUpRecyclerView();
        firestore.collection(Debt.COLLECTION).whereEqualTo(Debt.USER, app.getUser()).addSnapshotListener((snapshot, e) -> updateDebtCard());
    }

    private void setUpRecyclerView() {
        // TODO: fix query by frequency
        Query query = firestore.collection(PlannedPayment.COLLLECTION).whereEqualTo(PlannedPayment.USER, app.getUser())
                .whereGreaterThan("frequency.null", new Timestamp(new Date(System.currentTimeMillis())))
                .orderBy("frequency.null", Query.Direction.ASCENDING)
                .limit(1);

        FirestoreRecyclerOptions<PlannedPayment> options = new FirestoreRecyclerOptions.Builder<PlannedPayment>().setQuery(query, PlannedPayment.class).build();
        adapterPlannedPayments = new DaoPlannedPayments(options);
        bindRecyclerView(upcomingPlannedPayments, adapterPlannedPayments);

        adapterPlannedPayments.setOnItemClickListener((document, position) ->
                navigate(HomeDirections.homeToUpdatePlannedPayment().setPlannedPaymentID(document.getId())));

        Log.d("_Test", "[HomeInformation] setUpRecyclerView() ");
    }

    private void updateDebtCard() {
        firestore.collection(Debt.COLLECTION).whereEqualTo(Debt.USER, app.getUser()).get().addOnSuccessListener(data -> {

            Log.d("_Test", "[HomeInformation] updateDebtCard() ");
            List<Debt> debts = data.toObjects(Debt.class);

            AtomicDouble borrowed   = new AtomicDouble(0.00);
            AtomicDouble lent       = new AtomicDouble(0.00);

            if (!debts.isEmpty()) {
                debts.stream().filter(item -> item.getDebtType() == Debt.DebtType.BORROWED.ordinal()).forEach(debt -> borrowed.getAndAdd(Double.valueOf(debt.getAmount())));
                debts.stream().filter(item -> item.getDebtType() == Debt.DebtType.LENT.ordinal()).forEach(debt -> lent.getAndAdd(Double.valueOf(debt.getAmount())));
            }

            PgbBorrowed.setValueText(NumberFormat.getCurrencyInstance().format(borrowed.doubleValue()));
            PgbLent.setValueText(NumberFormat.getCurrencyInstance().format( - lent.doubleValue()));
            PgbBorrowed.setProgressBar(Functions.calculatePercentage(borrowed.doubleValue(), borrowed.doubleValue() + lent.doubleValue()).intValue(), getResources().getColor(R.color.positiveColor, requireActivity().getTheme()));
            PgbLent.setProgressBar(Functions.calculatePercentage(lent.doubleValue(), borrowed.doubleValue() + lent.doubleValue()).intValue(), getResources().getColor(R.color.negativeColor, requireActivity().getTheme()));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterPlannedPayments.startListening();
        Log.d("_Test", "[HomeInformation] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterPlannedPayments.stopListening();
        Log.d("_Test", "[HomeInformation] onStop() ");
    }

    @OnClick(R.id.home_Card_Debts_More)
    protected void onMoreAddDebt() {
        Log.d("_Test", "[HomeInformation] onMoreAddDebt() ");
        // TODO: set type by giving dialog with two options. LENT / BORROWED
        int type = 0;
        navigate(HomeDirections.homeToUpdateDebt().setDebtType(type));
    }

    @OnClick(R.id.home_MenuScroll_Item1)
    protected void onItem1() {
        Log.d("_Test", "[HomeInformation] onItem1() ");
        navigate(HomeDirections.homeToRecords());
    }

    @OnClick(R.id.home_MenuScroll_Item2)
    protected void onItem2() {
        Log.d("_Test", "[HomeInformation] onItem2() ");
        navigate(HomeDirections.homeToShoppingLists());
    }

    @OnClick({R.id.home_MenuScroll_Item3, R.id.home_Card_PlannedPayments_More})
    protected void onItem3() {
        Log.d("_Test", "[HomeInformation] onItem3() ");
        navigate(HomeDirections.homeToPlannedPayment());
    }

    @OnClick(R.id.home_MenuScroll_Item4)
    protected void onItem4() {
        Log.d("_Test", "[HomeInformation] onItem4() ");
        navigate(HomeDirections.homeToDebts());
    }
}

