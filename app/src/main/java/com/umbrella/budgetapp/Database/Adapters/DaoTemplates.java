package com.umbrella.budgetapp.Database.Adapters;

import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Template;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

import java.util.List;
import java.util.Objects;

public class DaoTemplates extends CloudRecyclerAdapter<Template, ReorderableViewHolder> {
    private OnItemClickListener listener;
    private int Position;

    private List<Integer> colors;
    private TypedArray icons;

    public DaoTemplates(@NonNull FirestoreRecyclerOptions<Template> options) {
        super(options);

        colors = getIntegerArrayList(R.array.colors);
        icons = getTypedArray(R.array.icons);
        Log.d("_Test", "[DaoTemplates] DaoTemplates() ");
    }

    @NonNull
    @Override
    public ReorderableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReorderableViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reorderable_view, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ReorderableViewHolder holder, int position, @NonNull Template template) {
        holder.Name.setText(template.getName());
        template.getCategoryID().get().addOnSuccessListener(data -> {
            Category category = Objects.requireNonNull(data.toObject(Category.class));

            holder.Description.setText(category.getName());
            holder.Icon.setBackgroundColor(colors.get(category.getColor()));
            holder.Icon.setImageResource(icons.getResourceId(category.getIcon(), 0));
        });

        holder.itemView.setOnClickListener(v -> {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION && listener != null){
                listener.onItemClick(getSnapshots().getSnapshot(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
