package com.umbrella.budgetapp.Database.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.umbrella.budgetapp.R;

public class ReorderableViewHolder extends RecyclerView.ViewHolder {
    ImageView Icon;
    ImageView OrderButton;
    TextView Name;
    TextView Description;

    public ReorderableViewHolder(final View itemView){
        super(itemView);
        Icon = itemView.findViewById(R.id.list_ReorderableView_Img);
        OrderButton = itemView.findViewById(R.id.list_ReorderableView_Orderable);
        Name = itemView.findViewById(R.id.list_ReorderableView_Name);
        Description = itemView.findViewById(R.id.list_ReorderableView_Info);
    }
}
