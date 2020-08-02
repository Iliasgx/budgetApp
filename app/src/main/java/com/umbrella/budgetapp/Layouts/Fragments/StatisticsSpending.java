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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Customs.DetailedProgressbar;
import com.umbrella.budgetapp.Database.Adapters.DaoRecords;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Extensions.Functions;
import com.umbrella.budgetapp.Layouts.ContentHolders.Statistics;
import com.umbrella.budgetapp.Layouts.ContentHolders.StatisticsDirections;
import com.umbrella.budgetapp.R;

import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StatisticsSpending extends BaseFragment {
    private DaoRecords adapterRecords;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics_spending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("_Test", "[StatisticsSpending] onViewCreated() ");

        Query query = Statistics.QueryFilter.setFilter().orderBy(Record.AMOUNT, Query.Direction.DESCENDING).limit(5);
        FirestoreRecyclerOptions<Record> options = new FirestoreRecyclerOptions.Builder<Record>().setQuery(query, Record.class).build();
        adapterRecords = new DaoRecords(options);
        bindRecyclerView(getObject(R.id.statistics_Card_Expenses_RecyclerView), adapterRecords);

        adapterRecords.setOnItemClickListener((documentSnapshot, position) ->
                navigate(StatisticsDirections.statisticsToUpdateRecordDetails().setRecordID(documentSnapshot.getReference().getPath())));

        Statistics.QueryFilter.setFilter().get().addOnSuccessListener(data -> {
            app.getUserDocument().collection(Category.COLLECTION).get().addOnSuccessListener(cats -> {
                Map<DocumentReference, DocumentSnapshot> categoryMap = new LinkedHashMap<>();
                Map<DocumentSnapshot, Integer> usages = new LinkedHashMap<>();
                Map<DocumentSnapshot, Double> totalValues = new LinkedHashMap<>();

                cats.forEach(ct -> categoryMap.put(ct.getReference(), ct));

                data.forEach(item -> {
                    Record record = Objects.requireNonNull(item.toObject(Record.class));
                    DocumentSnapshot categorySnap = Objects.requireNonNull(categoryMap.get(record.getCategoryID()));
                    if (!usages.containsKey(categorySnap)) {
                        usages.put(item, 1);
                        totalValues.put(categorySnap, Double.valueOf(record.getAmount()));
                    } else {
                        usages.replace(item, Objects.requireNonNull(usages.get(item)) + 1);
                        totalValues.replace(categorySnap, Objects.requireNonNull(totalValues.get(categorySnap)) + Double.valueOf(record.getAmount()));
                    }
                });

                Map<Category, Double> all = new LinkedHashMap<>();
                usages.entrySet().stream().sorted(Map.Entry.comparingByValue()).limit(5)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
                        .forEach((docs, dbl) ->
                                all.put(Objects.requireNonNull(docs.toObject(Category.class)), Objects.requireNonNull(totalValues.get(docs))));

                bindRecyclerView(getObject(R.id.statistics_Card_Categories_RecyclerView), new DaoBiggestCategories(all));
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterRecords.startListening();
        Log.d("_Test", "[StatisticsSpending] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterRecords.stopListening();
        Log.d("_Test", "[StatisticsSpending] onStop() ");
    }

    public class DaoBiggestCategories extends RecyclerView.Adapter<DaoBiggestCategories.ModelHolder> {
        private LinkedList<Category> categories = new LinkedList<>();
        private LinkedList<Double> values = new LinkedList<>();
        private AtomicDouble totalValue = new AtomicDouble(0.00);

        private List<Integer> colors;

        DaoBiggestCategories(Map<Category, Double> val) {
            val.forEach((k, v) -> {
                categories.add(k);
                values.add(v);
            });
            colors = getIntegerArrayList(R.array.colors);

            values.forEach(dbl -> totalValue.addAndGet(dbl));

            Log.d("_Test", "[DaoBiggestCategories] DaoBiggestCategories() ");
        }

        @NonNull
        @Override
        public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_stats_prgb, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ModelHolder holder, int position) {
            holder.ProgressView.setTitleText(categories.get(position).getName());
            holder.ProgressView.setProgressBar(Functions.calculatePercentage(values.get(position), totalValue.doubleValue()).intValue(), colors.get(categories.get(position).getColor()));
            holder.ProgressView.setValueText(NumberFormat.getCurrencyInstance().format(values.get(position)));
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        class ModelHolder extends RecyclerView.ViewHolder {
            DetailedProgressbar ProgressView;

            ModelHolder(@NonNull View itemView) {
                super(itemView);
                ProgressView = itemView.findViewById(R.id.list_stats_prgb_bar);
            }
        }
    }
}