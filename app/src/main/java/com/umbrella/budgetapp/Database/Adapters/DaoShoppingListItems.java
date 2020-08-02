package com.umbrella.budgetapp.Database.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.umbrella.budgetapp.Database.Collections.ShoppingItem;
import com.umbrella.budgetapp.Database.Collections.ShoppingListItem;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

import java.util.Objects;

public class DaoShoppingListItems extends CloudRecyclerAdapter<ShoppingListItem, DaoShoppingListItems.ModelHolder> {
    private OnItemClickListener listener;

    public DaoShoppingListItems(@NonNull FirestoreRecyclerOptions<ShoppingListItem> options) {
        super(options);
        Log.d("_Test", "[DaoShoppingListItems] DaoShoppingListItems() ");
    }

    @NonNull
    @Override
    public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_shoppinglist_items, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull ShoppingListItem shoppingListItem) {
        shoppingListItem.getShoppingItemID().get().addOnSuccessListener(data ->
                Objects.requireNonNull(data.toObject(ShoppingListItem.class))
                        .getShoppingItemID().get().addOnSuccessListener(data2 -> {
                            holder.Item.setText(Objects.requireNonNull(data2.toObject(ShoppingItem.class)).getName());
                            holder.Amount.setText(Objects.requireNonNull(data.toObject(ShoppingListItem.class)).getNumber());
                }));
        holder.Number.setText(String.valueOf(shoppingListItem.getNumber()));
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ModelHolder extends RecyclerView.ViewHolder {
        CheckBox Item;
        EditText Number;
        TextView Amount;

        ModelHolder(@NonNull View itemView) {
            super(itemView);
            Item = itemView.findViewById(R.id.list_ShoppingListItems_Item);
            Number = itemView.findViewById(R.id.list_ShoppingListItems_Number);
            Amount = itemView.findViewById(R.id.list_ShoppingListItems_Amount);

            Item.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }
}
