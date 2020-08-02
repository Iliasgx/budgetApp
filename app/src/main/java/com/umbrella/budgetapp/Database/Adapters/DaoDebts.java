package com.umbrella.budgetapp.Database.Adapters;

import android.content.Context;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Debt;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

import java.util.List;
import java.util.Objects;

public class DaoDebts extends CloudRecyclerAdapter<Debt, DaoDebts.ModelHolder> {
    private OnItemClickListener itemListener;
    private OnRecordListener recordListener;
    private Context context;
    private Debt.DebtType debtType;

    private List<Integer> colors;
    private TypedArray icons;

    public DaoDebts(@NonNull FirestoreRecyclerOptions<Debt> options) {
        super(options);
    }

    public DaoDebts(@NonNull FirestoreRecyclerOptions<Debt> options, Debt.DebtType type) {
        super(options);

        this.debtType = type;
        colors = getIntegerArrayList(R.array.colors);
        icons = getTypedArray(R.array.icons);
        Log.d("_Test", "[DaoDebts] DaoDebts() - DebType is: " + type.name());
    }

    @NonNull
    @Override
    public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new  ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_debts, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull Debt debt) {
        String TITLE;
        if (debtType == Debt.DebtType.LENT){
            TITLE = context.getString(R.string.debts_list_Name_Lent, debt.getName()).toUpperCase();
        } else {
            TITLE = context.getString(R.string.debts_list_Name_Borrowed, debt.getName()).toUpperCase();
        }
        holder.Title.setText(TITLE);
        holder.Name.setText(debt.getName());
        holder.Information.setText(debt.getDescription());
        holder.Amount.setText(debt.getAmount());
        holder.Date.setText(toSimpleDateFormat(debt.getDate().toDate(), BaseFragment.DateFormat.DATE));

        debt.getCategoryID().get().addOnSuccessListener(data -> {
            Category category = Objects.requireNonNull(data.toObject(Category.class));

            holder.Icon.setBackgroundColor(colors.get(category.getColor()));
            holder.Icon.setImageResource(icons.getResourceId(category.getIcon(), 0));
        });

        holder.itemView.setOnClickListener(v -> {
            if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) return;
            if (v != holder.ButtonRecord) {
                if (itemListener != null) itemListener.onItemClick(getSnapshots().getSnapshot(holder.getAdapterPosition()), holder.getAdapterPosition());
            } else {
                if (recordListener != null) recordListener.createRecord(getSnapshots().getSnapshot(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemListener = listener;
    }

    public interface OnRecordListener {
        void createRecord(DocumentSnapshot snapshot, int position);
    }

    public void onCreateRecordClickListener(OnRecordListener listener) {
        this.recordListener = listener;
    }

    public class ModelHolder extends RecyclerView.ViewHolder {
        TextView Title;
        TextView Name;
        TextView Information;
        TextView Amount;
        TextView Date;
        TextView ButtonRecord;
        ImageView Icon;

        ModelHolder(final View itemView){
            super(itemView);
            Title = itemView.findViewById(R.id.list_Debts_Title);
            Name = itemView.findViewById(R.id.list_Debts_Name);
            Information = itemView.findViewById(R.id.list_Debts_Information);
            Amount = itemView.findViewById(R.id.list_Debts_Amount);
            Date = itemView.findViewById(R.id.list_Debts_Date);
            ButtonRecord = itemView.findViewById(R.id.list_debts_create_record);
            Icon = itemView.findViewById(R.id.list_Debts_Img);
        }
    }
}
