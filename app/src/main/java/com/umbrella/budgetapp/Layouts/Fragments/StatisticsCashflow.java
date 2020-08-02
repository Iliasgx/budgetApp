package com.umbrella.budgetapp.Layouts.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.util.concurrent.AtomicDouble;
import com.umbrella.budgetapp.Customs.DetailedProgressbar;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Extensions.Functions;
import com.umbrella.budgetapp.Layouts.ContentHolders.Statistics;
import com.umbrella.budgetapp.R;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsCashflow extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics_cashflow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onUpdate();
    }

    private void onUpdate() {
        Log.d("_Test", "[StatisticsCashflow] onUpdate() ");
        Statistics.QueryFilter.setFilter().get().addOnSuccessListener(data -> {
            AtomicDouble totalIncome    = new AtomicDouble(0.00);
            AtomicDouble totalExpenses  = new AtomicDouble(0.00);
            AtomicInteger NrOfIncome    = new AtomicInteger(0);
            AtomicInteger NrOfExpenses  = new AtomicInteger(0);

            data.toObjects(Record.class).forEach(record -> {
                if (record.getType() == 0) {
                    totalIncome.addAndGet(Double.valueOf(record.getAmount()));
                    NrOfIncome.incrementAndGet();
                } else if (record.getType() == 1) {
                    totalExpenses.addAndGet(Double.valueOf(record.getAmount()));
                    NrOfExpenses.incrementAndGet();
                }
            });

            Double totalValue = totalIncome.doubleValue() + totalExpenses.doubleValue();
            Calendar[] periods = Statistics.QueryFilter.getPeriods();
            int days = (int)TimeUnit.DAYS.convert(periods[1].getTimeInMillis() - periods[0].getTimeInMillis(), TimeUnit.MILLISECONDS);

            DetailedProgressbar IC = getObject(R.id.statistics_Card_Cashflow_Progressbar_Income);
            DetailedProgressbar EX = getObject(R.id.statistics_Card_Cashflow_Progressbar_Expenses);
            IC.setValueText(NumberFormat.getCurrencyInstance().format(totalIncome.doubleValue()));
            EX.setValueText(NumberFormat.getCurrencyInstance().format(totalExpenses.doubleValue()));
            IC.setProgressBar(Functions.calculatePercentage(totalIncome.doubleValue(), totalValue).intValue(), getResources().getColor(R.color.positiveColor, requireActivity().getTheme()));
            EX.setProgressBar(Functions.calculatePercentage(totalExpenses.doubleValue(), totalValue).intValue(), getResources().getColor(R.color.negativeColor, requireActivity().getTheme()));
            ((TextView)getObject(R.id.statistics_Card_Cashflow_Amount)).setText(NumberFormat.getCurrencyInstance().format(totalValue));
            ((TextView)getObject(R.id.statistics_Card_CashflowTable_I1)).setText(NumberFormat.getCurrencyInstance(Locale.FRANCE).format(NrOfIncome.doubleValue()));
            ((TextView)getObject(R.id.statistics_Card_CashflowTable_E1)).setText(NumberFormat.getCurrencyInstance(Locale.FRANCE).format(NrOfExpenses.doubleValue()));
            ((TextView)getObject(R.id.statistics_Card_CashflowTable_I2)).setText(String.valueOf(totalIncome.doubleValue() / days));
            ((TextView)getObject(R.id.statistics_Card_CashflowTable_E2)).setText(String.valueOf(totalExpenses.doubleValue() / days));
            ((TextView)getObject(R.id.statistics_Card_CashflowTable_I3)).setText(String.valueOf((int)totalIncome.doubleValue() / (NrOfIncome.intValue() == 0 ? 1 : NrOfIncome.intValue())));
            ((TextView)getObject(R.id.statistics_Card_CashflowTable_E3)).setText(String.valueOf((int)totalExpenses.doubleValue() / (NrOfExpenses.intValue() == 0 ? 1 : NrOfExpenses.intValue())));
            ((TextView)getObject(R.id.statistics_Card_CashflowTable_I4)).setText(String.valueOf(totalIncome.doubleValue()));
            ((TextView)getObject(R.id.statistics_Card_CashflowTable_E4)).setText(String.valueOf(totalExpenses.doubleValue()));
        });
    }
}
