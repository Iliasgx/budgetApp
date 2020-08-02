package com.umbrella.budgetapp.Layouts.UpdateFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.umbrella.budgetapp.Customs.DateTimePickerDialog;
import com.umbrella.budgetapp.Customs.InputValueDialog;
import com.umbrella.budgetapp.Customs.Spinners;
import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Collections.Goal;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class UpdateGoalDetails extends BaseFragment implements UpdateListeners, InputValueDialog.InputValueDialogListener, DateTimePickerDialog.setOnFinishListener {
    private static final String TAG = "Goal";
    private String ID;
    private Type type;
    private Goal goal;

    private transient boolean isTarget = false;

    @BindView(R.id.data_Card_GoalDetails_Name) EditText name;
    @BindView(R.id.data_Card_GoalDetails_Amount) TextView target;
    @BindView(R.id.data_Card_GoalDetails_Saved) TextView saved;
    @BindView(R.id.data_Card_GoalDetails_Date) TextView date;
    @BindView(R.id.data_Card_GoalDetails_Color) Spinner color;
    @BindView(R.id.data_Card_GoalDetails_Icon) Spinner icon;
    @BindView(R.id.data_Card_GoalDetails_Note) EditText note;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_goal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ID = UpdateGoalDetailsArgs.fromBundle(requireArguments()).getGoalID();

        Log.d("_Test", "[UpdateGoalDetails] onViewCreated() ID is: " + ID);

        new Spinners.Colors(this, color, Spinners.Colors.Size.SMALL);
        new Spinners.Icons(this, icon);

        type = onLoad(ID == null ? Type.CREATE : Type.UPDATE);

        SetToolbar(getString(type.equals(Type.UPDATE) ?
                R.string.title_edit_goal :
                R.string.title_add_goal),
                ToolbarNavIcon.CANCEL,
                type.equals(Type.UPDATE) ?
                R.menu.goal_more_options :
                R.menu.save);

        setOnMenuItemClickListener(v -> {
            switch (v.getItemId()) {
                case R.id.menuLayout_GoalMoreOptions_pause: onStatusChange(Goal.StatusItem.PAUSED); break;
                case R.id.menuLayout_GoalMoreOptions_start: onStatusChange(Goal.StatusItem.ACTIVE); break;
                case R.id.menuLayout_GoalMoreOptions_Delete: onDelete(); break;
                case R.id.menuLayout_GoalMoreOptions_Save: onUpdate(); break;
            }
            return true;
        });
        setNavigationOnClickListener(v -> {
            clearFocus();
            navigateUp();
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        Log.d("_Test", "[UpdateGoalDetails] onPrepareOptionsMenu() ");
        super.onPrepareOptionsMenu(menu);
        firestore.document(ID).get().addOnSuccessListener(succeed -> {
            boolean isActive = Objects.requireNonNull(succeed.toObject(Goal.class)).getStatus() == Goal.StatusItem.ACTIVE.ordinal();
            menu.findItem(R.id.menuLayout_GoalMoreOptions_pause).setVisible(isActive);
            menu.findItem(R.id.menuLayout_GoalMoreOptions_start).setVisible(!isActive);
            menu.findItem(R.id.menuLayout_GoalMoreOptions_Save).setVisible(true);
        });
    }

    /*
     * Hide keyboard if focus is lost.
     */
    @OnFocusChange({R.id.data_Card_GoalDetails_Name, R.id.data_Card_GoalDetails_Note})
    protected void onFocusChange() {
        Log.d("_Test", "[UpdateGoalDetails] onFocusChange() ");
        if (!name.hasFocus()) name.onEditorAction(EditorInfo.IME_ACTION_DONE);
        if (!note.hasFocus()) note.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    private void onStatusChange(Goal.StatusItem statusItem) {
        Log.d("_Test", "[UpdateGoalDetails] onStatusChange() ");
        clearFocus();
        firestore.document(ID).update(Goal.STATUS, statusItem.ordinal()).addOnCompleteListener(c -> {
            if (c.isSuccessful()) {
                navigateUp();
            } else {
                showResultSnackbar(false, TAG, Message.UPDATED);
            }
        });
    }

    @Override
    public Type onLoad(Type type) {
        Log.d("_Test", "[UpdateGoalDetails] onLoad() type is: " + type.name());

        if (type.equals(Type.UPDATE)) {
            firestore.document(ID).get().addOnSuccessListener(data -> {
                goal = Objects.requireNonNull(data.toObject(Goal.class));

                name.setText(goal.getName());
                target.setText(goal.getTargetAmount());
                saved.setText(goal.getSavedAmount());
                date.setText(toSimpleDateFormat(goal.getDesiredDate().toDate(), DateFormat.DATE_TIME));
                color.setSelection(goal.getColor());
                icon.setSelection(goal.getIcon());
                note.setText(goal.getNote());
            }).addOnFailureListener(f -> showSnackbar(getString(R.string.snackbar_notLoad)));
        } else {
            goal = new Goal();
            UpdateGoalDetailsArgs details = UpdateGoalDetailsArgs.fromBundle(requireArguments());
            if (details.getGoalTemplateID() != null) {
                getGoalDetails(Integer.valueOf(details.getGoalTemplateID()));
            }
            if (details.getSelectName() != null) name.setText(details.getSelectName());
        }
        return type;
    }

    @Override
    public void onUpdate() {
        clearFocus();
        if (reformatText(name.getText()).isEmpty() | target.getText().equals("0") | date.getText().toString().isEmpty()) {
            showToast(getString(R.string.goal_requiredFields));
        } else {
            Log.d("_Test", "[UpdateGoalDetails] onUpdate() ");

            goal.setName(reformatText(name.getText()));
            goal.setTargetAmount(target.getText().toString());
            goal.setSavedAmount(saved.getText().toString());
            goal.setDesiredDate(new Timestamp((Date)date.getTag()));
            goal.setColor(color.getSelectedItemPosition());
            goal.setIcon(icon.getSelectedItemPosition());
            goal.setNote(note.getText().toString());

            if (type.equals(Type.CREATE)) {
                goal.setUserID(app.getUser());
                goal.setLastAmount("0");
                goal.setStatus(Goal.StatusItem.ACTIVE.ordinal());
                goal.setStartDate(new Timestamp(new Date(System.currentTimeMillis())));
                firestore.collection(Goal.COLLECTION).add(goal).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.CREATED);
                    if (c.isSuccessful()) navigateUp();
                });
            } else {
                firestore.document(ID).set(goal).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.UPDATED);
                    if (c.isSuccessful()) navigateUp();
                });
            }
        }
    }

    @Override
    public void onDelete() {
        Log.d("_Test", "[UpdateGoalDetails] onDelete() ");
        clearFocus();
        firestore.document(ID).delete()
                .addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.DELETED);
                    if (c.isSuccessful()) navigateUp();
                });
    }

    private void clearFocus() {
        Log.d("_Test", "[UpdateGoalDetails] clearFocus() ");
        name.clearFocus();
        note.clearFocus();
    }

    @OnClick({R.id.data_Card_GoalDetails_Amount, R.id.data_Card_GoalDetails_Saved})
    protected void updateValue(TextView view) {
        Log.d("_Test", "[UpdateGoalDetails] updateValue() ");
        isTarget = view.equals(target);
        InputValueDialog.getInstance(this);
    }

    @OnClick(R.id.data_Card_GoalDetails_Date)
    protected void updateDate() {
        Log.d("_Test", "[UpdateGoalDetails] updateDate() ");
        Calendar c = Calendar.getInstance();
        c.setTime(goal.getDesiredDate().toDate());
        DateTimePickerDialog.getInstance(this, c, DateTimePickerDialog.Type.DATE, DateTimePickerDialog.DateOptions.AFTER_TODAY);
    }

    @Override
    public void onFinishValueDialog(String finalValue) {
        Log.d("_Test", "[UpdateGoalDetails] onFinishValueDialog() ");
        if (isTarget) {
            target.setText(finalValue);
        } else {
            saved.setText(finalValue);
        }
    }

    @Override
    public void onFinishDialog(Calendar calendar) {
        Log.d("_Test", "[UpdateGoalDetails] onFinishDialog() ");
        date.setText(toSimpleDateFormat(calendar.getTime(), DateFormat.DATE_TIME));
        date.setTag(calendar.getTime());
    }

    private void getGoalDetails(Integer val) {
        Log.d("_Test", "[UpdateGoalDetails] getGoalDetails() ");
        String setName = "";
        int setColor = 0;
        int setIcon = 0;

        switch (val) {
            case 1:
                setName = getString(R.string.data_Goal_Select_Item1);
                setColor = getIntegerArrayList(R.array.colors).indexOf(getResources().getColor(R.color.goal_item1, null));
                setIcon = 9;
                break;
            case 2:
                setName = getString(R.string.data_Goal_Select_Item2);
                setColor = getIntegerArrayList(R.array.colors).indexOf(getResources().getColor(R.color.goal_item2, null));
                setIcon = 16;
                break;
            case 3:
                setName = getString(R.string.data_Goal_Select_Item3);
                setColor = getIntegerArrayList(R.array.colors).indexOf(getResources().getColor(R.color.goal_item3, null));
                setIcon = 13;
                break;
            case 4:
                setName = getString(R.string.data_Goal_Select_Item4);
                setColor = getIntegerArrayList(R.array.colors).indexOf(getResources().getColor(R.color.goal_item4, null));
                setIcon = 10;
                break;
            case 5:
                setName = getString(R.string.data_Goal_Select_Item5);
                setColor = getIntegerArrayList(R.array.colors).indexOf(getResources().getColor(R.color.goal_item5, null));
                setIcon = 14;
                break;
            case 6:
                setName = getString(R.string.data_Goal_Select_Item6);
                setColor = getIntegerArrayList(R.array.colors).indexOf(getResources().getColor(R.color.goal_item6, null));
                setIcon = 3;
                break;
            case 7:
                setName = getString(R.string.data_Goal_Select_Item7);
                setColor = getIntegerArrayList(R.array.colors).indexOf(getResources().getColor(R.color.goal_item7, null));
                setIcon = 14;
                break;
            case 8:
                setName = getString(R.string.data_Goal_Select_Item8);
                setColor = getIntegerArrayList(R.array.colors).indexOf(getResources().getColor(R.color.goal_item8, null));
                setIcon = 1;
                break;
        }
        name.setText(setName);
        color.setSelection(setColor);
        icon.setSelection(setIcon);
    }
}

