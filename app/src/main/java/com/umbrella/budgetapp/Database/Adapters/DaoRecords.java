package com.umbrella.budgetapp.Database.Adapters;

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
import com.umbrella.budgetapp.Database.Collections.Account;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Extensions.App;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DaoRecords extends CloudRecyclerAdapter<Record, DaoRecords.ModelHolder> {
    private OnItemClickListener listener;

    private List<Integer> colors;
    private TypedArray icons;

    public DaoRecords(@NonNull FirestoreRecyclerOptions<Record> options) {
        super(options);

        colors = getIntegerArrayList(R.array.colors);
        icons = getTypedArray(R.array.icons);
    }

    @NonNull
    @Override
    public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_records, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull Record record) {
        holder.Information.setText(record.getDescription().isEmpty() ? null : "\"" + record.getDescription() + "\"");
        holder.Amount.setText(NumberFormat.getCurrencyInstance(Locale.FRANCE).format(Double.valueOf(record.getAmount())));
        holder.Date.setText(toSimpleDateFormat(record.getDateTime().toDate(), BaseFragment.DateFormat.DATE));

        record.getAccountID().get().addOnSuccessListener(data -> holder.Account.setText(Objects.requireNonNull(data.toObject(Account.class)).getName()));
        record.getCategoryID().get().addOnSuccessListener(data -> {
           Category category = Objects.requireNonNull(data.toObject(Category.class));

           holder.Category.setText(category.getName());
           holder.Icon.setBackgroundColor(colors.get(category.getColor()));
           holder.Icon.setImageResource(icons.getResourceId(category.getIcon(), 0));
        });
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ModelHolder extends RecyclerView.ViewHolder {
        TextView Category;
        TextView Account;
        TextView Information;
        TextView Amount;
        TextView Date;
        ImageView Icon;

        ModelHolder(@NonNull View itemView) {
            super(itemView);
            Category = itemView.findViewById(R.id.list_Records_Category);
            Account = itemView.findViewById(R.id.list_Records_Account);
            Information = itemView.findViewById(R.id.list_Records_Information);
            Amount = itemView.findViewById(R.id.list_Records_Amount);
            Date = itemView.findViewById(R.id.list_Records_Date);
            Icon = itemView.findViewById(R.id.list_Records_Img);

            itemView.setOnClickListener(v -> {
                int Position = getAdapterPosition();
                listener.onItemClick(getSnapshots().getSnapshot(Position), Position);
            });
        }
    }
}

