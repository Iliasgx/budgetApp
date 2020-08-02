package com.umbrella.budgetapp.Layouts.Fragments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umbrella.budgetapp.Customs.InputValueDialog;
import com.umbrella.budgetapp.Database.Collections.Goal;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class GoalDetails extends BaseFragment implements InputValueDialog.InputValueDialogListener {
    private static final String TAG = "Goal details";
    private Goal.StatusItem status = Goal.StatusItem.ACTIVE;
    private Goal goal;
    private String ID;

    @BindView(R.id.goal_Details_Img) ImageView img;
    @BindView(R.id.goal_Details_Name) TextView name;
    @BindView(R.id.goal_Details_TargetDate) TextView targetDate;
    @BindView(R.id.goal_Details_Progress) ProgressBar progress;
    @BindView(R.id.goal_Details_Saved) TextView saved;
    @BindView(R.id.goal_Details_SavedAmount) TextView savedAmount;
    @BindView(R.id.goal_Details_Savable) TextView savable;
    @BindView(R.id.goal_Details_LastAdded_Amount) TextView lastAddedAmount;
    @BindView(R.id.goal_Details_EstimatedPeriod) TextView estimatedPeriod;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ID = Objects.requireNonNull(GoalDetailsArgs.fromBundle(requireArguments()).getGoalID());

        SetToolbar(getString(R.string.title_GoalsDetails), ToolbarNavIcon.BACK, R.menu.goal_more_options);
        setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuLayout_GoalMoreOptions_pause:
                case R.id.menuLayout_GoalMoreOptions_start: onStatusChange(); break;
                case R.id.menuLayout_GoalMoreOptions_Delete: onDeleteClicked(); break;
                case R.id.menuLayout_GoalMoreOptions_Edit: onEditClicked(); break;
            }
            return true;
        });
        setNavigationOnClickListener(v -> navigateUp());

        Log.d("_Test", "[GoalDetails] onViewCreated() ");
        setData();
    }

    private void setData() {
        firestore.document(ID).get().addOnSuccessListener(data -> {
            goal = Objects.requireNonNull(data.toObject(Goal.class));

            int progressValue = (int)(Double.valueOf(goal.getSavedAmount()) / Double.valueOf(goal.getTargetAmount()));
            int period = ((100 - progressValue) / progressValue) * (int)(System.currentTimeMillis() - goal.getStartDate().toDate().getTime()) / (24 * 60 * 60 * 1000);

            img.setImageResource(getIntegerArrayList(R.array.icons).get(goal.getIcon()));
            img.setColorFilter(goal.getColor());
            name.setText(goal.getName());
            targetDate.setText(toSimpleDateFormat(goal.getDesiredDate().toDate(), DateFormat.DATE));
            progress.setProgress(progressValue);
            saved.setText(getString(R.string.percentage, progressValue));
            savedAmount.setText(goal.getSavedAmount());
            savable.setText(getString(R.string.bar, goal.getTargetAmount()));
            lastAddedAmount.setText(goal.getLastAmount());
            estimatedPeriod.setText(getString(R.string.number_days, period));

            status = Goal.StatusItem.valueOf(String.valueOf(goal.getStatus()));
            setButtons();

            Log.d("_Test", "[GoalDetails] setData() ");
        });
    }

    private void setButtons() {
        getObject(R.id.goal_Details_AddAmount).setVisibility(status.equals(Goal.StatusItem.ACTIVE) ? View.VISIBLE : View.GONE);
        getObject(R.id.goal_Details_SetReached).setVisibility(status.equals(Goal.StatusItem.ACTIVE) ? View.VISIBLE : View.GONE);
        getObject(R.id.goal_Details_Estimated).setVisibility(status.equals(Goal.StatusItem.REACHED) ? View.GONE : View.VISIBLE);
        estimatedPeriod.setVisibility(status.equals(Goal.StatusItem.REACHED) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        requireActivity().invalidateOptionsMenu();
        menu.findItem(R.id.menuLayout_GoalMoreOptions_pause).setVisible(status.equals(Goal.StatusItem.ACTIVE));
        menu.findItem(R.id.menuLayout_GoalMoreOptions_start).setVisible(status.equals(Goal.StatusItem.PAUSED));
        Log.d("_Test", "[GoalDetails] onPrepareOptionsMenu() ");
    }

    private void onDeleteClicked() {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.menuResources_Title_Delete))
            .setMessage(getString(R.string.delete_confirmation, "goal"))
            .setIcon(R.drawable.delete)
            .setPositiveButton("DELETE", (dlg, which) -> {
                dlg.dismiss();
                firestore.document(ID).delete().addOnCompleteListener(completed -> {
                    showResultSnackbar(completed.isSuccessful(), TAG, Message.DELETED);
                    navigateUp();
                });
            })
            .setNegativeButton("CANCEL", (dlg, which) -> dlg.dismiss())
            .create();

        dialog.show();
        Log.d("_Test", "[GoalDetails] onDeleteClicked() ");
    }

    private void onEditClicked() {
        navigate(GoalDetailsDirections.goalDetailsToUpdateGoalDetails().setGoalID(ID));
    }

    private void onStatusChange() {
        Goal.StatusItem item = status.equals(Goal.StatusItem.ACTIVE) ? Goal.StatusItem.PAUSED : Goal.StatusItem.ACTIVE;
        firestore.document(ID).update(Goal.STATUS, item.ordinal()).addOnSuccessListener(succeed -> {
            status = item;
            setButtons();

            Log.d("_Test", "[GoalDetails] onStatusChange() ");
        });
    }

    @OnClick(R.id.goal_Details_AddAmount)
    protected void addAmount() {
        Log.d("_Test", "[GoalDetails] addAmount() ");
        InputValueDialog.getInstance(this);
    }

    @OnClick(R.id.goal_Details_SetReached)
    protected void setReached() {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.goal_reached_warning_title))
                .setMessage(getString(R.string.goal_reached_warning))
                .setIcon(R.drawable.reminder)
                .setPositiveButton("SET REACHED", (dlg, which) -> {
                    firestore.document(ID).update(Goal.STATUS, Goal.StatusItem.REACHED.ordinal()).addOnSuccessListener(succeed -> {
                        status = Goal.StatusItem.REACHED;
                        setButtons();
                    });
                })
                .setNegativeButton("CANCEL", (dlg, which) -> dlg.dismiss())
                .create();

        Log.d("_Test", "[GoalDetails] setReached() ");
        dialog.show();
    }

    @Override
    public void onFinishValueDialog(String finalValue) {
        Log.d("_Test", "[GoalDetails] onFinishValueDialog() ");
        firestore.document(ID).update(
                Goal.LAST_AMOUNT, finalValue,
                Goal.SAVED_AMOUNT, String.valueOf(Double.valueOf(goal.getSavedAmount()) + Double.valueOf(finalValue)))
                .addOnSuccessListener(succeed -> setData());
    }
}

