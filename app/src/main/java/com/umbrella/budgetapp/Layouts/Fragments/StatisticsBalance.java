package com.umbrella.budgetapp.Layouts.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Customs.DetailedProgressbar;
import com.umbrella.budgetapp.Database.Collections.Account;
import com.umbrella.budgetapp.Database.Collections.Currency;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.Extensions.Functions;
import com.umbrella.budgetapp.Layouts.ContentHolders.Statistics;
import com.umbrella.budgetapp.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class StatisticsBalance extends BaseFragment {
    private AtomicDouble totalAccountValue = new AtomicDouble(0.00);

    @BindView(R.id.statistics_Card_Balance_Amount) TextView amount;
    @BindView(R.id.statistics_Card_Balance_RecyclerView) RecyclerView balanceRecyclerView;
    @BindView(R.id.statistics_Card_BalanceCurrencies_Progressbar) ProgressBar currencyProgressbar;
    @BindView(R.id.statistics_Card_BalanceCurrencies_RecyclerView) RecyclerView currencyRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics_balance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onUpdateData();
    }

    private void onUpdateData() {
        Log.d("_Test", "[StatisticsBalance] onUpdateData() ");

        AtomicDouble totalCurrencyValue = new AtomicDouble(0.00);
        AtomicDouble totalCurrencyForeign = new AtomicDouble(0.00);

        Query queryAccounts = app.getUserDocument().collection(Account.COLLECTION).orderBy(Account.POSITION, Query.Direction.ASCENDING);
        queryAccounts.get().addOnSuccessListener(data -> data.toObjects(Account.class).forEach(account ->
                amount.setText(String.valueOf(totalAccountValue.addAndGet(Double.valueOf(account.getCurrentValue()))))));

        FirestoreRecyclerOptions<Account> optionsAccounts = new FirestoreRecyclerOptions.Builder<Account>().setQuery(queryAccounts, Account.class).build();
        bindRecyclerView(balanceRecyclerView, new DaoBalanceAccounts(optionsAccounts));

        Statistics.QueryFilter.setFilter().get().addOnSuccessListener(data ->
                app.getUserDocument().collection(Currency.COLLECTION).whereEqualTo(Currency.POSITION, 0).get().addOnSuccessListener(curr ->
                    data.forEach(record -> {
                        Record rec = Objects.requireNonNull(record.toObject(Record.class));

                        if (!record.getId().equals(curr.getDocuments().get(0).getId())) totalCurrencyForeign.getAndAdd(Double.valueOf(rec.getAmount()));
                        totalCurrencyValue.getAndAdd(Double.valueOf(rec.getAmount()));

                        currencyProgressbar.setProgress((int)(totalCurrencyForeign.doubleValue() / (totalCurrencyForeign.doubleValue() + totalCurrencyValue.doubleValue())));
                    })));

        Query currencyQuery = app.getUserDocument().collection(Currency.COLLECTION).whereGreaterThan(Currency.POSITION, 0);
        FirestoreRecyclerOptions<Currency> optionsCurrency = new FirestoreRecyclerOptions.Builder<Currency>().setQuery(currencyQuery, Currency.class).build();
        bindRecyclerView(currencyRecyclerView, new DaoBalanceCurrency(optionsCurrency, Statistics.QueryFilter.setFilter()));

        currencyRecyclerView.setVisibility(optionsCurrency.getSnapshots().size() == 0 ? View.GONE : View.VISIBLE);
    }

    private class DaoBalanceAccounts extends CloudRecyclerAdapter<Account, DaoBalanceAccounts.ModelHolder> {
        private List<Integer> colors;

        DaoBalanceAccounts(@NonNull FirestoreRecyclerOptions<Account> options) {
            super(options);
            colors = getIntegerArrayList(R.array.colors);
            Log.d("_Test", "[DaoBalanceAccounts] DaoBalanceAccounts() ");
        }

        @NonNull
        @Override
        public DaoBalanceAccounts.ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_stats_prgb, parent, false));
        }

        @Override
        protected void onBindViewHolder(@NonNull DaoBalanceAccounts.ModelHolder holder, int position, @NonNull Account account) {
            holder.ProgressView.setTitleText(account.getName());
            holder.ProgressView.setProgressBar(Functions.calculatePercentage(Double.valueOf(account.getCurrentValue()), totalAccountValue.doubleValue()).intValue(), colors.get(account.getColor()));
            holder.ProgressView.setValueText(NumberFormat.getCurrencyInstance().format(Double.valueOf(account.getCurrentValue())));
        }

        @Override
        public void setOnItemClickListener(OnItemClickListener listener) {

        }

        class ModelHolder extends RecyclerView.ViewHolder {
            DetailedProgressbar ProgressView;

            ModelHolder(@NonNull View itemView) {
                super(itemView);
                ProgressView = itemView.findViewById(R.id.list_stats_prgb_bar);
            }
        }
    }

    private class DaoBalanceCurrency extends CloudRecyclerAdapter<Currency, DaoBalanceCurrency.ModelHolder> {
        private List<Integer> colors;
        private Query query;

        DaoBalanceCurrency(@NonNull FirestoreRecyclerOptions<Currency> options, Query query) {
            super(options);
            colors = getIntegerArrayList(R.array.colors);
            this.query = query;
            Log.d("_Test", "[DaoBalanceCurrency] DaoBalanceCurrency() ");
        }

        @NonNull
        @Override
        public DaoBalanceCurrency.ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_balance_currencies, parent, false));
        }

        @Override
        protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull Currency currency) {
            holder.Color.setBackgroundColor(colors.get(position));
            holder.Currency.setText(currency.getCountryID());

            query.whereEqualTo(Record.CURRENCY, getSnapshots().getSnapshot(position).getId()).get().addOnSuccessListener(data -> {
                AtomicDouble value = new AtomicDouble(0.00);
                data.forEach(recordData ->
                        holder.Amount.setText(String.valueOf(value.addAndGet(Double.valueOf(recordData.toObject(Record.class).getAmount())))));
            });
        }

        @Override
        public void setOnItemClickListener(OnItemClickListener listener) {

        }

        class ModelHolder extends RecyclerView.ViewHolder {
            View Color;
            TextView Currency;
            TextView Amount;

            ModelHolder(@NonNull View itemView) {
                super(itemView);
                Color = itemView.findViewById(R.id.list_BalanceCurrencies_Color);
                Currency = itemView.findViewById(R.id.list_BalanceCurrencies_Currency);
                Amount = itemView.findViewById(R.id.list_BalanceCurrencies_Amount);
            }
        }
    }
}

