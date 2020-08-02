package com.umbrella.budgetapp.Database.Adapters;

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
import com.umbrella.budgetapp.Database.Collections.PlannedPayment;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

import java.util.Objects;

public class DaoPlannedPayments extends CloudRecyclerAdapter<PlannedPayment, DaoPlannedPayments.ModelHolder> {
    private OnItemClickListener listener;

    public DaoPlannedPayments(@NonNull FirestoreRecyclerOptions<PlannedPayment> options) {
        super(options);
        Log.d("_Test", "[DaoPlannedPayments] DaoPlannedPayments() ");
    }

    @NonNull
    @Override
    public DaoPlannedPayments.ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_planned_payments, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull PlannedPayment plannedPayment) {
        holder.Title.setText(plannedPayment.getName());
        holder.Information.setText(plannedPayment.getNote());
        holder.Amount.setText(String.valueOf(plannedPayment.getAmount()));
        holder.Date.setText(String.valueOf(plannedPayment.getStartDate().toDate()));
        holder.Stamp.setText(plannedPayment.getFrequency().get("Period"));
        plannedPayment.getAccountID().get().addOnSuccessListener(data -> holder.Account.setText(Objects.requireNonNull(data.toObject(Account.class)).getName()));
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ModelHolder extends RecyclerView.ViewHolder {
        TextView Title;
        TextView Account;
        TextView Information;
        TextView Amount;
        TextView Date;
        TextView Stamp;
        ImageView Icon;

        ModelHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.list_PlannedPayments_Title);
            Account = itemView.findViewById(R.id.list_PlannedPayments_Account);
            Information = itemView.findViewById(R.id.list_PlannedPayments_Information);
            Amount = itemView.findViewById(R.id.list_PlannedPayments_Amount);
            Date = itemView.findViewById(R.id.list_PlannedPayments_Date);
            Stamp = itemView.findViewById(R.id.list_PlannedPayments_Stamp);
            Icon = itemView.findViewById(R.id.list_PlannedPayments_Img);

            itemView.setOnClickListener(v -> {
                int Position = getAdapterPosition();
                if (Position != RecyclerView.NO_POSITION && listener != null){
                    listener.onItemClick(getSnapshots().getSnapshot(Position), Position);
                }
            });
        }
    }
}
