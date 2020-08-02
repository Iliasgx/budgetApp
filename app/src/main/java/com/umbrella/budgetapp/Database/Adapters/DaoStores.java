package com.umbrella.budgetapp.Database.Adapters;

import android.app.Activity;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Country;
import com.umbrella.budgetapp.Database.Collections.Currency;
import com.umbrella.budgetapp.Database.Collections.Store;
import com.umbrella.budgetapp.Database.Defaults.DefaultCountries;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DaoStores extends CloudRecyclerAdapter<Store, DaoStores.ModelHolder> {
    private OnItemClickListener listener;

    private List<Integer> colors;
    private TypedArray icons;

    public DaoStores(@NonNull FirestoreRecyclerOptions<Store> options) {
        super(options);

        colors = getIntegerArrayList(R.array.colors);
        icons = getTypedArray(R.array.icons);
        Log.d("_Test", "[DaoStores] DaoStores() ");
    }

    @NonNull
    @Override
    public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_stores, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull Store store) {
        holder.Name.setText(store.getName());
        store.getCategoryID().get().addOnSuccessListener(data -> {
            Category category = Objects.requireNonNull(data.toObject(Category.class));
            holder.Category.setText(category.getName());
            holder.Icon.setBackgroundColor(colors.get(category.getColor()));
            holder.Icon.setImageResource(icons.getResourceId(category.getIcon(), 0));
        });
        store.getCurrencyID().get().addOnSuccessListener(data -> {
            Country country = new DefaultCountries().getDefaultCountries(new Activity()).stream().filter(item -> item.getCode().equals(Objects.requireNonNull(data.toObject(Currency.class)).getCountryID())).collect(Collectors.toList()).get(0);
            holder.Currency.setText(country.getCode());
            holder.Country.setText(country.getName());
        });
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ModelHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Category;
        TextView Currency;
        TextView Country;
        ImageView Icon;

        ModelHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.list_Stores_Name);
            Category = itemView.findViewById(R.id.list_Stores_Category);
            Currency = itemView.findViewById(R.id.list_Stores_Currency);
            Country = itemView.findViewById(R.id.list_Stores_Country);
            Icon = itemView.findViewById(R.id.list_Stores_Img);

            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null){
                    listener.onItemClick(getSnapshots().getSnapshot(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }
}
