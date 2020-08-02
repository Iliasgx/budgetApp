package com.umbrella.budgetapp.Database.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.umbrella.budgetapp.Database.Collections.Country;
import com.umbrella.budgetapp.Database.Collections.Currency;
import com.umbrella.budgetapp.Database.Defaults.DefaultCountries;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.MainActivity;
import com.umbrella.budgetapp.R;

import java.util.List;
import java.util.stream.Collectors;

public class DaoCurrencies extends CloudRecyclerAdapter<Currency, ReorderableViewHolder> {
    private OnItemClickListener listener;

    private List<Integer> colors;

    public DaoCurrencies(@NonNull FirestoreRecyclerOptions<Currency> options) {
        super(options);

        colors = getIntegerArrayList(R.array.colors);
        Log.d("_Test", "[DaoCurrencies] DaoCurrencies() ");
    }

    @NonNull
    @Override
    public ReorderableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReorderableViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reorderable_view, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ReorderableViewHolder holder, int position, @NonNull Currency currency) {
        Country country = new DefaultCountries().getDefaultCountries(new MainActivity()).stream().filter(item -> item.getCode().equals(currency.getCountryID())).collect(Collectors.toList()).get(0);

        holder.Icon.setBackgroundColor(colors.get(holder.getAdapterPosition()));
        holder.Name.setText(country.getName());
        holder.Description.setText(country.getCode());

        holder.itemView.setOnClickListener(v -> {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(getSnapshots().getSnapshot(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
