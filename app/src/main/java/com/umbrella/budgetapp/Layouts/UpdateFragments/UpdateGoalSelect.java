package com.umbrella.budgetapp.Layouts.UpdateFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class UpdateGoalSelect extends BaseFragment {

    @BindView(R.id.data_Card_GoalSelect_Name)
    EditText name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_goal_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetToolbar(getString(R.string.title_add_goal_select), ToolbarNavIcon.CANCEL);

        setNavigationOnClickListener(v -> {
            name.clearFocus();
            navigateUp();
        });
        Log.d("_Test", "[UpdateGoalSelect] onViewCreated() ");
    }

    /*
     * Hide keyboard if focus is lost.
     */
    @OnFocusChange(R.id.data_Card_GoalSelect_Name)
    protected void onFocusChange() {
        Log.d("_Test", "[UpdateGoalSelect] onFocusChange() ");
        if (!name.hasFocus()) name.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }


    @OnClick(R.id.data_Card_GoalSelect_Create)
    public void onSelect() {
        Log.d("_Test", "[UpdateGoalSelect] onSelect() ");
        nav(null);
    }

    @OnClick({
            R.id.data_Card_GoalSelect_Img1,
            R.id.data_Card_GoalSelect_Img2,
            R.id.data_Card_GoalSelect_Img3,
            R.id.data_Card_GoalSelect_Img4,
            R.id.data_Card_GoalSelect_Img5,
            R.id.data_Card_GoalSelect_Img6,
            R.id.data_Card_GoalSelect_Img7,
            R.id.data_Card_GoalSelect_Img8})
    public void onItemClick(ImageView v) {
        Log.d("_Test", "[UpdateGoalSelect] onItemClick() ");
        nav(String.valueOf(v.getTag()));
    }

    private void nav(String tag) {
        String value = null;
        if (!reformatText(name.getText()).isEmpty()) value = reformatText(name.getText());

        Log.d("_Test", "[UpdateGoalSelect] nav() tag is: " + tag);

       navigate(UpdateGoalSelectDirections.updateGoalSelectToDetails()
               .setSelectName(value)
               .setGoalTemplateID(tag));
    }
}

