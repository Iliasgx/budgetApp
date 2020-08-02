package com.umbrella.budgetapp.Database.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.umbrella.budgetapp.Database.Collections.ShoppingItem;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

public class DaoShoppingItems extends CloudRecyclerAdapter<ShoppingItem, DaoShoppingItems.ModelHolder> {
    private OnItemClickListener listener;

    public DaoShoppingItems(@NonNull FirestoreRecyclerOptions<ShoppingItem> options) {
        super(options);
        Log.d("_Test", "[DaoShoppingItems] DaoShoppingItems() ");
    }

    @NonNull
    @Override
    public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_shopping_items, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull ShoppingItem shoppingItem) {
        holder.Item.setText(shoppingItem.getName());
        holder.Amount.setText(String.valueOf(shoppingItem.getDefAmount()));
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ModelHolder extends RecyclerView.ViewHolder {
        CheckBox Item;
        TextView Amount;
        ImageView Delete;

        ModelHolder(@NonNull View itemView) {
            super(itemView);
            Item = itemView.findViewById(R.id.list_Shopping_Item);
            Amount = itemView.findViewById(R.id.list_Shopping_Amount);
            Delete = itemView.findViewById(R.id.list_Shopping_Delete);

            itemView.setOnClickListener(v -> {
                int Position = getAdapterPosition();
                if (v != Delete){
                    listener.onItemClick(getSnapshots().getSnapshot(Position), Position);
                }
                deleteItem(Position);
            });
        }
    }
}
