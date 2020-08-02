package com.umbrella.budgetapp.Layouts.Fragments;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Extensions.Functions;
import com.umbrella.budgetapp.Layouts.ContentHolders.Statistics;
import com.umbrella.budgetapp.R;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StatisticsReports extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Statistics.QueryFilter.setFilter().orderBy(Record.DATETIME).orderBy(Record.CATEGORY, Query.Direction.ASCENDING).get().addOnSuccessListener(data ->
                app.getUserDocument().collection(Category.COLLECTION).get().addOnSuccessListener(cats -> {
                Map<DocumentReference, DocumentSnapshot> categoryMap = new LinkedHashMap<>();
                Map<DocumentSnapshot, Double> CategoryRecordValueMap = new LinkedHashMap<>();
                AtomicDouble IC = new AtomicDouble(0.00);
                AtomicDouble EX = new AtomicDouble(0.00);

                Log.d("_Test", "[StatisticsReports] onViewCreated() ");

                cats.forEach(ct -> categoryMap.put(ct.getReference(), ct));

                data.forEach(item -> {
                    Record record = Objects.requireNonNull(item.toObject(Record.class));
                    DocumentSnapshot categorySnap = Objects.requireNonNull(categoryMap.get(record.getCategoryID()));
                    if (!CategoryRecordValueMap.containsKey(categorySnap)) {
                        CategoryRecordValueMap.put(categorySnap, Double.valueOf(record.getAmount()));
                    } else {
                        CategoryRecordValueMap.replace(categorySnap, Objects.requireNonNull(CategoryRecordValueMap.get(categorySnap)) + Double.valueOf(record.getAmount()));
                    }

                    if (record.getType() == 0) {
                        ((TextView)getObject(R.id.statistics_Card_Saldi_IncomeAmount)).setText(String.valueOf(IC.addAndGet(Double.valueOf(record.getAmount()))));
                    } else if (record.getType() == 1) {
                        ((TextView)getObject(R.id.statistics_Card_Saldi_ExpensesAmount)).setText(String.valueOf(EX.addAndGet(Double.valueOf(record.getAmount()))));
                    }
                    ((TextView)getObject(R.id.statistics_Card_Saldi_Amount)).setText(String.valueOf(IC.doubleValue() + EX.doubleValue()));
                });

                getPreviousQuery().orderBy(Record.DATETIME).orderBy(Record.CATEGORY, Query.Direction.ASCENDING).get().addOnSuccessListener(dataOld -> {
                    Map<DocumentSnapshot, Double> PreviousRecords = new LinkedHashMap<>();
                    AtomicDouble IC_old = new AtomicDouble(0.00);
                    AtomicDouble EX_old = new AtomicDouble(0.00);

                    dataOld.forEach(item -> {
                        Record record = Objects.requireNonNull(item.toObject(Record.class));
                        DocumentSnapshot oldCategorySnap = Objects.requireNonNull(categoryMap.get(record.getCategoryID()));
                        if (!PreviousRecords.containsKey(oldCategorySnap)) {
                            PreviousRecords.put(oldCategorySnap, Double.valueOf(record.getAmount()));
                        } else {
                            PreviousRecords.replace(oldCategorySnap, Objects.requireNonNull(PreviousRecords.get(oldCategorySnap)) + Double.valueOf(record.getAmount()));
                        }

                        if (record.getType() == 0) {
                            IC_old.addAndGet(Double.valueOf(record.getAmount()));
                        } else if (record.getType() == 1) {
                            EX_old.addAndGet(Double.valueOf(record.getAmount()));
                        }
                    });
                    ((TextView)getObject(R.id.statistics_Card_Saldi_PrevAmount)).setText(getString(R.string.percentage, Functions.calculatePercentage(IC_old.doubleValue() + EX_old.doubleValue(), IC.doubleValue() + EX.doubleValue()).toString()));
                    ((TextView)getObject(R.id.statistics_Card_Saldi_PrevIncomeAmount)).setText(getString(R.string.percentage, Functions.calculatePercentage(IC_old, IC).toString()));
                    ((TextView)getObject(R.id.statistics_Card_Saldi_PrevExpensesAmount)).setText(getString(R.string.percentage, Functions.calculatePercentage(EX_old, EX).toString()));

                    bindRecyclerView(getObject(R.id.statistics_Card_Saldi_RecyclerView_Income), new DaoReport(CategoryRecordValueMap, PreviousRecords, 0));
                    bindRecyclerView(getObject(R.id.statistics_Card_Saldi_ListView_Expenses), new DaoReport(CategoryRecordValueMap, PreviousRecords, 1));
                });
        }));
    }

    private Query getPreviousQuery() {
        Log.d("_Test", "[StatisticsReports] getPreviousQuery() ");
        String[] filter = app.splitFilterString(app.getStatisticsFilter());
        String periodText = "";

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_YEAR), 0,0,0);

        switch (filter[0]) {
            case "1":
                switch (filter[1]) {
                    case "1":
                        start.add(Calendar.DAY_OF_YEAR, -14);
                        end.add(Calendar.DAY_OF_YEAR, -7);
                        periodText = "Last week";
                        break;
                    case "2":
                        start.add(Calendar.MONTH, -2);
                        end.add(Calendar.MONTH, -1);
                        periodText = "Last month";
                        break;
                    case "3":
                        start.add(Calendar.YEAR, -1);
                        end.add(Calendar.MONTH, -6);
                        periodText = "Last 6 months";
                        break;
                    case "4":
                        periodText = "Last year";
                        start.add(Calendar.YEAR, -2);
                        end.add(Calendar.YEAR, -1);
                        break;
                }
                break;
            case "2":
                start.set(Calendar.WEEK_OF_YEAR, Integer.valueOf(filter[2]) - 1);
                end.setTimeInMillis(start.getTimeInMillis());
                end.add(Calendar.DAY_OF_YEAR, 6);
                periodText = "Week " + (start.get(Calendar.WEEK_OF_YEAR) + 1);
                break;
            case "3":
                start.set(Integer.valueOf(filter[5]), Integer.valueOf(filter[4]), Integer.valueOf(filter[3]));
                end.set(Integer.valueOf(filter[8]), Integer.valueOf(filter[7]), Integer.valueOf(filter[6]));
                long diff = TimeUnit.MILLISECONDS.convert(end.getTimeInMillis() - start.getTimeInMillis(), TimeUnit.MILLISECONDS);
                end.setTimeInMillis(start.getTimeInMillis());
                start.setTimeInMillis(start.getTimeInMillis() - diff);
                periodText = filter[3] + "/" + filter[4] + "/" + filter[5].subSequence(2,3) + " - " + filter[6] + "/" + filter[7] + "/" + filter[8].substring(2,3);
                break;
        }

        ((TextView)getObject(R.id.statistics_Card_Saldi_Period)).setText(periodText);

        return firestore.collection(Record.COLLECTION)
                .whereEqualTo(Record.USER, app.getUser())
                .whereGreaterThan(Record.DATETIME, new Timestamp(start.getTime()))
                .whereLessThanOrEqualTo(Record.DATETIME, new Timestamp(end.getTime()));
    }

    public class DaoReport extends RecyclerView.Adapter<DaoReport.ModelHolder> {
        private List<Integer> colors;
        private TypedArray arrayIcons;

        private LinkedHashMap<DocumentSnapshot, Double> N;
        private LinkedHashMap<DocumentSnapshot, Double> O;

        DaoReport(@NonNull Map<DocumentSnapshot, Double> newValues, @NonNull Map<DocumentSnapshot, Double> oldValues, int type) {
            colors = getIntegerArrayList(R.array.colors);
            arrayIcons = getResources().obtainTypedArray(R.array.icons);

            N = newValues.entrySet().stream().filter(item -> Objects.requireNonNull(item.getKey().toObject(Record.class)).getType() == type).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            O = oldValues.entrySet().stream().filter(item -> Objects.requireNonNull(item.getKey().toObject(Record.class)).getType() == type).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            Log.d("_Test", "[DaoReport] DaoReport() ");
        }

        @NonNull
        @Override
        public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reports, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ModelHolder holder, int position) {
            DocumentSnapshot snap = N.entrySet().iterator().next().getKey();
            Category category = Objects.requireNonNull(snap.toObject(Category.class));

            holder.Img.setBackgroundColor(colors.get(category.getColor()));
            holder.Img.setImageResource(arrayIcons.getResourceId(category.getIcon(), 0));
            holder.Name.setText(category.getName());
            holder.Amount.setText(String.valueOf(N.get(snap)));
            holder.PreviousPer.setText(getString(R.string.percentage, Functions.calculatePercentage(O.entrySet().iterator().next().getValue(), Objects.requireNonNull(N.get(snap))).toString()));
        }

        @Override
        public int getItemCount() {
            return N.size();
        }

        public class ModelHolder extends RecyclerView.ViewHolder {
            ImageView Img;
            TextView Name;
            TextView Amount;
            TextView PreviousPer;

            public ModelHolder(@NonNull View itemView) {
                super(itemView);
                Img = itemView.findViewById(R.id.list_Reports_Img);
                Name = itemView.findViewById(R.id.list_Reports_Category);
                Amount = itemView.findViewById(R.id.list_Reports_Amount);
                PreviousPer = itemView.findViewById(R.id.reports_previous_period);
            }
        }

    }
}









