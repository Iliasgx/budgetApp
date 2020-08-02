package com.umbrella.budgetapp.Database.Adapters;

import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.umbrella.budgetapp.Database.Collections.Account;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

import java.util.List;

public class DaoAccounts extends CloudRecyclerAdapter<Account, ReorderableViewHolder> {
    private OnItemClickListener listener;

    private List<String> accountTypes;
    private List<Integer> colors;
    private TypedArray icons;

    public DaoAccounts(@NonNull FirestoreRecyclerOptions<Account> options) {
        super(options);

        colors = getIntegerArrayList(R.array.colors);
        icons = getTypedArray(R.array.icons);
        accountTypes = getStringArrayList(R.array.accountType);
        Log.d("_Test", "[DaoAccounts] DaoAccounts() ");
    }

    @NonNull
    @Override
    public ReorderableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReorderableViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reorderable_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReorderableViewHolder holder, int position, @NonNull Account account) {
        holder.Name.setText(account.getName());
        holder.Icon.setImageResource(icons.getResourceId(account.getType(), 0));
        holder.Icon.setBackgroundColor(colors.get(account.getColor()));
        holder.Description.setText(accountTypes.get(account.getType()));

        holder.itemView.setOnClickListener(v -> {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(getSnapshots().getSnapshot(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

