package com.umbrella.budgetapp.Database.Adapters;

import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

import java.util.List;

public class DaoCategories extends CloudRecyclerAdapter<Category, ReorderableViewHolder> {
    private OnItemClickListener listener;

    private List<Integer> colors;
    private TypedArray arrayIcons;

    public DaoCategories(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);

        colors = getIntegerArrayList(R.array.colors);
        arrayIcons = getTypedArray(R.array.icons);
        Log.d("_Test", "[DaoCategories] DaoCategories() ");
    }

    @NonNull
    @Override
    public ReorderableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReorderableViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reorderable_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReorderableViewHolder holder, int position, @NonNull Category category) {
        holder.Name.setText(category.getName());
        holder.Description.setVisibility(View.GONE);
        holder.OrderButton.setVisibility(View.GONE);
        holder.Icon.setImageResource(arrayIcons.getResourceId(category.getIcon(), 0));
        holder.Icon.setColorFilter(colors.get(category.getColor()));

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