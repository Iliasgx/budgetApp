package com.umbrella.budgetapp.Database.Adapters;

import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.umbrella.budgetapp.Database.Collections.Goal;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

import java.util.List;

public class DaoGoals extends CloudRecyclerAdapter<Goal, DaoGoals.ModelHolder> {
    private OnItemClickListener listener;
    private Goal.StatusItem type;

    private List<Integer> colors;
    private TypedArray icons;

    public DaoGoals(@NonNull FirestoreRecyclerOptions<Goal> options) {
        super(options);
    }

    public DaoGoals(@NonNull FirestoreRecyclerOptions<Goal> options, Goal.StatusItem type) {
        super(options);
        this.type = type;

        colors = getIntegerArrayList(R.array.colors);
        icons = getTypedArray(R.array.icons);
        Log.d("_Test", "[DaoGoals] DaoGoals() and statusItem is: " + type.name());
    }

    @NonNull
    @Override
    public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int Layout = -1;
        if (type == Goal.StatusItem.ACTIVE || type == Goal.StatusItem.PAUSED) {
            Layout = R.layout.list_goals_active_paused;
        } else if (type == Goal.StatusItem.REACHED){
            Layout = R.layout.list_goals_reached;
        }
        return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(Layout, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull Goal goal) {
        holder.Name.setText(goal.getName());
        holder.Date.setText(String.valueOf(goal.getDesiredDate().toDate()));
        holder.Saved.setText(String.valueOf(goal.getSavedAmount()));
        holder.Icon.setBackgroundColor(colors.get(goal.getColor()));
        holder.Icon.setImageResource(icons.getResourceId(goal.getIcon(), 0));

        if (type != Goal.StatusItem.REACHED){
            holder.GoalItem.setText(String.valueOf(goal.getTargetAmount()));
            holder.Progress.setProgress((int)(100 * (Double.valueOf(goal.getSavedAmount()) / Double.valueOf(goal.getTargetAmount()))));
        }
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ModelHolder extends RecyclerView.ViewHolder {
    //Active-Paused-Reached
        TextView Name;
        TextView Date;
        TextView Saved;
        ImageView Icon;
    //Active-Paused
        TextView GoalItem;
        ProgressBar Progress;

        ModelHolder(@NonNull View itemView) {
            super(itemView);
            if (type == Goal.StatusItem.ACTIVE || type == Goal.StatusItem.PAUSED){
                Name = itemView.findViewById(R.id.list_Goals_ActivePaused_Name);
                Date = itemView.findViewById(R.id.list_Goals_ActivePaused_Date);
                Saved = itemView.findViewById(R.id.list_Goals_ActivePaused_Saved);
                Icon = itemView.findViewById(R.id.list_Goals_ActivePaused_Img);
                GoalItem = itemView.findViewById(R.id.list_Goals_ActivePaused_Goal);
                Progress = itemView.findViewById(R.id.list_Goals_ActivePaused_Progressbar);
            } else if (type == Goal.StatusItem.REACHED){
                Name = itemView.findViewById(R.id.list_Goals_Reached_Name);
                Date = itemView.findViewById(R.id.list_Goals_Reached_Date);
                Saved = itemView.findViewById(R.id.list_Goals_Reached_Saved);
                Icon = itemView.findViewById(R.id.list_Goals_Reached_Img);
            }

            itemView.setOnClickListener(v -> {
                int Position = getAdapterPosition();
                if (Position != RecyclerView.NO_POSITION && listener != null){
                    listener.onItemClick(getSnapshots().getSnapshot(Position), Position);
                }
            });
        }
    }

}
