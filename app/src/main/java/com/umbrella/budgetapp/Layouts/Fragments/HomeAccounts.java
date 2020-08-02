package com.umbrella.budgetapp.Layouts.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.umbrella.budgetapp.Customs.DetailedProgressbar;
import com.umbrella.budgetapp.Database.Adapters.DaoRecords;
import com.umbrella.budgetapp.Database.Collections.Account;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Extensions.Functions;
import com.umbrella.budgetapp.Layouts.ContentHolders.HomeDirections;
import com.umbrella.budgetapp.R;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import butterknife.BindView;
import butterknife.OnClick;

public class HomeAccounts extends BaseFragment {
    private DaoRecords adapter;

    @BindView(R.id.home_account1) Button homeAccount1;
    @BindView(R.id.home_account2) Button homeAccount2;
    @BindView(R.id.home_account3) Button homeAccount3;
    @BindView(R.id.home_account4) Button homeAccount4;

    @BindView(R.id.home_Card_Balance_Amount) TextView balanceAmount;
    @BindView(R.id.home_Card_Balance_PrevAmount) TextView balancePreviousPercentage;

    @BindView(R.id.home_Card_Cashflow_Period) TextView cashFlowPeriod;
    @BindView(R.id.home_Card_Cashflow_Amount) TextView cashFlowAmount;
    @BindView(R.id.home_Card_Cashflow_PrevAmount) TextView cashFlowPreviousPercentage;
    @BindView(R.id.home_Card_Cashflow_Progressbar_Income) DetailedProgressbar cashFlowIncomePrgB;
    @BindView(R.id.home_Card_Cashflow_Progressbar_Expenses) DetailedProgressbar cashFlowExpensesPrgB;

    @BindView(R.id.home_Card_Records_Period) TextView recordsPeriod;
    @BindView(R.id.home_Card_RecordsList) RecyclerView recyclerViewRecords;
    @BindView(R.id.home_Card_Records) CardView cardViewRecords;

    private final Query basicQuery = firestore.collection(Record.COLLECTION).whereEqualTo(Record.USER, app.getUser()).orderBy(Record.DATETIME, Query.Direction.DESCENDING);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_accounts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateAll();
    }

    private void updateAll() {
        app.getUserDocument().collection(Account.COLLECTION)
                .orderBy(Account.POSITION, Query.Direction.ASCENDING)
                .limit(4)
                .get(app.getSource())
                .addOnSuccessListener(data -> {
                    Button[] buttons = {homeAccount1, homeAccount2, homeAccount3, homeAccount4};

                    data.forEach(accountSnapshot -> {
                        Account account = accountSnapshot.toObject(Account.class);
                        String text = account.getName() + "\n" + account.getCurrentValue();

                        Button btn = buttons[account.getPosition()];
                        btn.setText(text);
                        btn.setTag(accountSnapshot.getReference());
                        btn.setContentDescription(account.getName());

                        btn.setBackgroundColor(app.getSelectedAccounts().contains(accountSnapshot.getReference()) ?
                                getIntegerArrayList(R.array.colors).get(account.getColor()) :
                                getResources().getColor(R.color.accountBackgroundColor, requireActivity().getTheme()));
                    });
                });

        updateBalance();
        updateCashflow();
        updateRecords();
    }

    /*
     * Balance today: value of account
     * Balance last period: value of account - (income + expenses of each record back in time till the current day minus 1 month.)
     */
    private void updateBalance() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH) - 1, c.get(Calendar.DAY_OF_MONTH), 0,0,0);
        Timestamp tp = new Timestamp(c.getTime());

        Query query = basicQuery.whereGreaterThan(Record.DATETIME, tp);

        updateQueries(query).addOnSuccessListener(result -> {
            List<Record> items = new LinkedList<>();
            result.forEach(qr -> items.addAll(qr.toObjects(Record.class)));

            AtomicDouble today = new AtomicDouble(0.00);
            AtomicDouble lastPeriod = new AtomicDouble(0.00);

            //Total of selected accounts.
            Button[] buttons = {homeAccount1, homeAccount2, homeAccount3, homeAccount4};
            for (int i = 0; i < buttons.length - 1; i++) {
                Button btn = buttons[i];
                DocumentReference ref = (DocumentReference)btn.getTag();
                if (app.getSelectedAccounts().contains(ref)) today.addAndGet(Double.valueOf(btn.getText().toString().replace(btn.getContentDescription() + "\n", "")));
            }
            lastPeriod.addAndGet(today.doubleValue());

            //Add each item back to the value
            items.forEach(item -> {
                //Income: Subtract to get old value
                //Expenses: Add to get old value
                Double value = Double.valueOf(item.getAmount());
                lastPeriod.addAndGet(item.getType() == 0 ? - value : value);
            });

            try {
                Double total = Functions.calculatePercentage(lastPeriod, today);
                balanceAmount.setText(NumberFormat.getCurrencyInstance(Locale.FRANCE).format(today.doubleValue()));
                balancePreviousPercentage.setText(getString(R.string.percentage, String.valueOf(total)));
                balancePreviousPercentage.setTextColor(getResources().getColor(total < 0 ? R.color.negativeColor : R.color.positiveColor, null));
            } catch (NullPointerException e) {
                Log.d("_Test", "[HomeAccounts] updateBalance() catch", e.fillInStackTrace());
            }
        });
    }

    private void updateCashflow() {
        cashFlowPeriod.setText(getStringArrayList(R.array.filterPeriods).get(app.getHomeCashFlowFilter()));

        Query[] queries = getPeriod(app.getHomeCashFlowFilter());

        updateQueries(queries[0]).addOnSuccessListener(resultNow ->
            updateQueries(queries[1]).addOnSuccessListener(resultPrev -> {
                List<Record> itemsNow = new LinkedList<>();
                List<Record> itemsPrev = new LinkedList<>();
                resultNow.forEach(qr -> itemsNow.addAll(qr.toObjects(Record.class)));
                resultPrev.forEach(qr -> itemsPrev.addAll(qr.toObjects(Record.class)));

                AtomicDouble previous = new AtomicDouble(0.00);
                AtomicDouble income   = new AtomicDouble(0.00);
                AtomicDouble expense  = new AtomicDouble(0.00);

                if (!itemsNow.isEmpty()) {
                    itemsNow.forEach(record -> {
                        if (record.getType() == 0) {
                            income.getAndAdd(Double.valueOf(record.getAmount()));
                        } else {
                            expense.getAndAdd(Double.valueOf(record.getAmount()));
                        }
                    });
                }

                if (!itemsPrev.isEmpty()) itemsPrev.forEach(record -> previous.getAndAdd(Double.valueOf(record.getAmount())));

                try {
                    Double total = Functions.calculatePercentage(previous.doubleValue(), income.doubleValue() + expense.doubleValue());
                    cashFlowAmount.setText(NumberFormat.getCurrencyInstance(Locale.FRANCE).format(income.doubleValue() - expense.doubleValue()));
                    cashFlowPreviousPercentage.setText(getString(R.string.percentage, String.valueOf(total)));
                    cashFlowPreviousPercentage.setTextColor(getResources().getColor(total < 0 ? R.color.negativeColor : R.color.positiveColor, null));

                    int perIncome = Functions.calculatePercentageOfTotal(income, new AtomicDouble(income.doubleValue() + expense.doubleValue())).intValue();

                    cashFlowIncomePrgB.setValueText(String.valueOf(NumberFormat.getCurrencyInstance(Locale.FRANCE).format(income.doubleValue())));
                    cashFlowExpensesPrgB.setValueText(String.valueOf(NumberFormat.getCurrencyInstance(Locale.FRANCE).format(expense.doubleValue())));
                    cashFlowIncomePrgB.setProgressBar(perIncome, getResources().getColor(R.color.positiveColor, null));
                    cashFlowExpensesPrgB.setProgressBar(100 - perIncome, getResources().getColor(R.color.negativeColor, null));
                } catch (NullPointerException e) {
                    Log.d("_Test", "[HomeAccounts] updateCashflow() catch", e.fillInStackTrace());
                }
            }));
    }

    private void updateRecords() {
        if (app.getSelectedAccounts().size() == 1) {
            cardViewRecords.setVisibility(View.VISIBLE);

            recordsPeriod.setText(getStringArrayList(R.array.filterPeriods).get(app.getHomeRecordsFilter()));

            Query c = getPeriod(app.getHomeRecordsFilter())[0].limit(5);
            FirestoreRecyclerOptions<Record> options = new FirestoreRecyclerOptions.Builder<Record>().setQuery(c, Record.class).build();

            adapter = new DaoRecords(options);
            bindRecyclerView(recyclerViewRecords, adapter);

            adapter.setOnItemClickListener((documentSnapshot, position) ->
                    navigate(HomeDirections.homeToUpdateRecordDetails().setRecordID(documentSnapshot.getReference().getPath())));

            adapter.startListening();
        } else {
            adapter = null;
            cardViewRecords.setVisibility(View.GONE);
        }
    }

    private Task<List<QuerySnapshot>> updateQueries(Query basicQuery) {
        Collection<Task<QuerySnapshot>> queries = new LinkedList<>();
        app.getSelectedAccounts().forEach(acc -> queries.add(basicQuery.whereEqualTo(Record.ACCOUNT, acc).get(app.getSource())));
        return Tasks.whenAllSuccess(queries);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    @OnClick({R.id.home_account1, R.id.home_account2, R.id.home_account3, R.id.home_account4})
    protected void onAccountClicked(Button button) {
        DocumentReference currentReference = firestore.document(String.valueOf(button.getTag()));

        if (app.getSelectedAccounts().contains(currentReference)) {
            app.getSelectedAccounts().remove(currentReference);
        } else {
            app.getSelectedAccounts().add(currentReference);
        }

        updateAll();
    }

    private Query[] getPeriod(int filter) {
        Calendar periodNow = Calendar.getInstance();
        Calendar periodPrev = Calendar.getInstance();

        periodNow.set(periodNow.get(Calendar.YEAR), periodNow.get(Calendar.MONTH), periodNow.get(Calendar.DAY_OF_MONTH), 0,0,0);
        periodPrev.setTimeInMillis(periodNow.getTimeInMillis());

        switch (filter) {
            case 0: //Today
                //periodNow is correct
                periodPrev.set(Calendar.DAY_OF_YEAR, -1);
                break;
            case 1: //This week
                periodNow.set(Calendar.DAY_OF_YEAR, -(Calendar.MONDAY - periodNow.get(Calendar.DAY_OF_WEEK)));
                periodPrev.setTimeInMillis(periodNow.getTimeInMillis());
                periodPrev.add(Calendar.DAY_OF_YEAR, -(Calendar.MONDAY - periodNow.get(Calendar.DAY_OF_WEEK)));
                break;
            case 2: //This month
                periodNow.set(Calendar.DAY_OF_MONTH, 1);
                periodPrev.setTimeInMillis(periodNow.getTimeInMillis());
                periodPrev.add(Calendar.MONTH, -1);
                break;
            case 3: //This year
                periodNow.set(periodNow.get(Calendar.YEAR), Calendar.JANUARY, 1);
                periodPrev.setTimeInMillis(periodNow.getTimeInMillis());
                periodPrev.add(Calendar.YEAR, -1);
                break;
            case 4: //7 days
                periodNow.add(Calendar.DAY_OF_YEAR, -6);
                periodPrev.setTimeInMillis(periodNow.getTimeInMillis());
                periodPrev.add(Calendar.DAY_OF_YEAR, -6);
                break;
            case 5: //30 days (till same 1 one month ago)
                periodNow.add(Calendar.MONTH, -1);
                periodPrev.setTimeInMillis(periodNow.getTimeInMillis());
                periodPrev.add(Calendar.MONTH, -1);
                break;
            case 6: //6 months
                periodNow.add(Calendar.MONTH, -6);
                periodPrev.setTimeInMillis(periodNow.getTimeInMillis());
                periodPrev.add(Calendar.MONTH, -6);
                break;
            case 7: //1 year
                periodNow.add(Calendar.YEAR, -1);
                periodPrev.setTimeInMillis(periodNow.getTimeInMillis());
                periodPrev.add(Calendar.YEAR, -1);
                break;
            case 8: //anytime
                //periodNow is correct
                //periodPrev is correct
                break;
        }
        Query filteredNow = basicQuery.whereGreaterThanOrEqualTo(Record.DATETIME, new Timestamp(periodNow.getTime()));
        Query filteredPrev = basicQuery.whereGreaterThanOrEqualTo(Record.DATETIME, new Timestamp(periodPrev.getTime())).whereLessThan(Record.DATETIME, new Timestamp(periodNow.getTime()));

        return new Query[]{filteredNow, filteredPrev};
    }

    @OnClick(R.id.home_RecordsButton)
    protected void showRecords() {
        Log.d("_Test", "[HomeAccounts] showRecords() ");
        navigate(HomeDirections.homeToRecords());
    }

    @OnClick(R.id.home_Card_Accounts_All)
    protected void showAllAccounts() {
        Log.d("_Test", "[HomeAccounts] showAllAccounts() ");
        navigate(HomeDirections.homeToAccounts());
    }

    @OnClick(R.id.home_Card_Balance_More)
    protected void onBalanceMore() {
        Log.d("_Test", "[HomeAccounts] onBalanceMore() ");
        navigate(HomeDirections.homeToStatistics().setPage(0));
    }

    @OnClick(R.id.home_Card_Cashflow_More)
    protected void onCashFlowMore() {
        Log.d("_Test", "[HomeAccounts] onCashFlowMore() ");
        navigate(HomeDirections.homeToStatistics().setPage(1));
    }

    @OnClick(R.id.home_Card_Records_More)
    protected void onRecordsMore() {
        Log.d("_Test", "[HomeAccounts] onRecordsMore() ");
        navigate(HomeDirections.homeToRecords());
    }

    @OnClick(R.id.home_Card_Cashflow_Filter)
    protected void onCashflowFilter() {
        Log.d("_Test", "[HomeAccounts] onCashflowFilter() ");
        new FilterDialog().getInstance(this, FilterDialog.CASHFLOW);
    }

    @OnClick(R.id.home_Card_Records_Filter)
    protected void onRecordsFilter() {
        Log.d("_Test", "[HomeAccounts] onRecordsFilter() ");
        new FilterDialog().getInstance(this, FilterDialog.RECORDS);
    }

    public class FilterDialog extends DialogFragment {
        private FilterDialog dlg = new FilterDialog();
        private static final int CASHFLOW = 0;
        private static final int RECORDS = 1;

        private int item = CASHFLOW;

        @BindView(R.id.dialog_Filter_SelectPeriod) Spinner period;

        public void getInstance(@NonNull Fragment fragment, int ID) {
            FilterDialog dialog = dlg;
            dialog.setTargetFragment(fragment, 300);
            dialog.show(fragment.getParentFragmentManager(), "FILTER");
            item = ID;
            Log.d("_Test", "[FilterDialog] getInstance() ");
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog_filter, container);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getObject(R.id.dialog_Frequency_Cancel).setOnClickListener(v -> dismiss());
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);

            Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);

            bindSimpleSpinner(period, getStringArrayList(R.array.filterPeriods));
            return dialog;
        }

        @OnClick(R.id.dialog_Filter_Ok)
        public void sendBackResult() {
           if (item == CASHFLOW) {
               Log.d("_Test", "[FilterDialog] sendBackResult() is CASHFLOW");
               app.setHomeCashFlowFilter(period.getSelectedItemPosition());
               updateCashflow();
           } else if (item == RECORDS) {
               Log.d("_Test", "[FilterDialog] sendBackResult() is RECORDS");
               app.setHomeRecordsFilter(period.getSelectedItemPosition());
               updateRecords();
           }
            dismiss();
        }
    }
}

