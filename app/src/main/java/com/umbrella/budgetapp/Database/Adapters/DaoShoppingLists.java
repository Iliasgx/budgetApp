package com.umbrella.budgetapp.Database.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.firebase.firestore.DocumentSnapshot;
import com.umbrella.budgetapp.Database.Collections.ShoppingItem;
import com.umbrella.budgetapp.Database.Collections.ShoppingList;
import com.umbrella.budgetapp.Database.Collections.ShoppingListItem;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DaoShoppingLists extends CloudRecyclerAdapter<ShoppingList, DaoShoppingLists.ModelHolder> {
    private OnItemClickListener listener;
    private onAddItemListener listenerAddItem;
    private List<DocumentSnapshot> references;

    public DaoShoppingLists(@NonNull FirestoreRecyclerOptions<ShoppingList> options, List<DocumentSnapshot> referList) {
        super(options);
        this.references = referList;
        Log.d("_Test", "[DaoShoppingLists] DaoShoppingLists() and listSize is: " + referList.size());
    }

    @NonNull
    @Override
    public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_shopping_lists, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull ShoppingList shoppingList) {
        holder.Name.setText(shoppingList.getName());

        if (references == null || references.size() == 0) return;
        references.stream()
                .filter(reference -> Objects.equals(reference.toObject(ShoppingList.class), shoppingList))
                .collect(Collectors.toList()).get(0).getReference()
                .get().addOnSuccessListener(SpList -> {
                    SpList.getReference().collection(ShoppingListItem.COLLECTION).get().addOnSuccessListener(items -> {
                        AtomicDouble totalValue = new AtomicDouble(0.00);
                        holder.NbItems.setText(items.size());

                        items.forEach(item -> {
                            ShoppingListItem shoppingListItem = Objects.requireNonNull(item.toObject(ShoppingListItem.class));
                            shoppingListItem.getShoppingItemID().get().addOnSuccessListener(data -> {
                                ShoppingItem shoppingItem = Objects.requireNonNull(data.toObject(ShoppingItem.class));

                                holder.Amount.setText(String.valueOf(totalValue.addAndGet(Double.valueOf(shoppingItem.getDefAmount()) * shoppingListItem.getNumber())));
                            });
                        });
                    });
        });
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ModelHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Amount;
        TextView NbItems;
        TextView ItemButton;

        ModelHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.list_ShoppingList_Name);
            Amount = itemView.findViewById(R.id.list_ShoppingList_Amount);
            NbItems = itemView.findViewById(R.id.list_ShoppingList_NbItems);
            ItemButton = itemView.findViewById(R.id.list_ShoppingList_Add);

            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                    if (v != ItemButton){
                        listener.onItemClick(getSnapshots().getSnapshot(getAdapterPosition()), getAdapterPosition());
                    } else {
                        listenerAddItem.onAdd(getSnapshots().getSnapshot(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });
        }
    }

    public void setOnAddItemClick(onAddItemListener listener) {
        listenerAddItem = listener;
    }

    public interface onAddItemListener {
        void onAdd(DocumentSnapshot snapshot, int position);
    }
}
