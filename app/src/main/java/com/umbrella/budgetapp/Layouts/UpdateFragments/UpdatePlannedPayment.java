package com.umbrella.budgetapp.Layouts.UpdateFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.umbrella.budgetapp.Customs.DateTimePickerDialog;
import com.umbrella.budgetapp.Customs.DialogSelectionList;
import com.umbrella.budgetapp.Customs.DialogSelectionList.CategoryDialog.OnCategoryItemSelected;
import com.umbrella.budgetapp.Customs.InputValueDialog;
import com.umbrella.budgetapp.Customs.Spinners;
import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.PlannedPayment;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

public class UpdatePlannedPayment extends BaseFragment implements UpdateListeners, InputValueDialog.InputValueDialogListener, DateTimePickerDialog.setOnFinishListener, OnCategoryItemSelected {
    private static final String TAG = "Planned payment";
    private String ID;
    private Type type;
    private PlannedPayment plannedPayment;

    @BindView(R.id.data_Card_PlannedPayment_Name) EditText name;
    @BindView(R.id.data_Card_PlannedPayment_From) TextView fromDate;
    @BindView(R.id.data_Card_PlannedPayment_Reminder) Spinner reminder;
    @BindView(R.id.data_Card_PlannedPayment_Frequency) TextView frequency;
    @BindView(R.id.data_Card_PlannedPayment_Amount) TextView amount;
    @BindView(R.id.data_Card_PlannedPayment_Currency) Spinner currency;
    @BindView(R.id.data_Card_PlannedPayment_Account) Spinner account;
    @BindView(R.id.data_Card_PlannedPayment_Type) Spinner costType;
    @BindView(R.id.data_Card_PlannedPayment_Category) TextView category;
    @BindView(R.id.data_Card_PlannedPayment_PayType) Spinner paymentType;
    @BindView(R.id.data_Card_PlannedPayment_Payee) EditText payee;
    @BindView(R.id.data_Card_PlannedPayment_Note) EditText note;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_planned_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ID = UpdatePlannedPaymentArgs.fromBundle(requireArguments()).getPlannedPaymentID();

        Log.d("_Test", "[UpdatePlannedPayment] onViewCreated() ID is: " + ID);

        bindSimpleSpinner(reminder, getStringArrayList(R.array.reminderOptions));
        bindSimpleSpinner(costType, getStringArrayList(R.array.type));
        bindSimpleSpinner(paymentType, getStringArrayList(R.array.paymentType));
        new Spinners.Accounts(this, account);
        new Spinners.Currencies(this, currency);

        type = onLoad(ID == null ? Type.CREATE : Type.UPDATE);

        SetToolbar(getString(type.equals(Type.UPDATE) ?
                R.string.title_edit_planned_payment :
                R.string.title_add_planned_payment),
                ToolbarNavIcon.CANCEL,
                type.equals(Type.UPDATE) ?
                R.menu.save_delete :
                R.menu.save);

        setOnMenuItemClickListener(v -> {
            switch (v.getItemId()) {
                case R.id.menuLayout_SaveDelete_Delete: onDelete(); break;
                case R.id.menuLayout_SaveDelete_Save: onUpdate(); break;
            }
            return true;
        });
        setNavigationOnClickListener(v -> {
            clearFocus();
            navigateUp();
        });
    }

    /*
     * Hide keyboard if focus is lost.
     */
    @OnFocusChange({R.id.data_Card_PlannedPayment_Name, R.id.data_Card_PlannedPayment_Payee, R.id.data_Card_PlannedPayment_Note})
    protected void onFocusChange() {
        Log.d("_Test", "[UpdatePlannedPayment] onFocusChange() ");
        if (!name.hasFocus()) name.onEditorAction(EditorInfo.IME_ACTION_DONE);
        if (!payee.hasFocus()) payee.onEditorAction(EditorInfo.IME_ACTION_DONE);
        if (!note.hasFocus()) note.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public Type onLoad(Type type) {
        Log.d("_Test", "[UpdatePlannedPayment] onLoad() type is: " + type.name());

        if (type.equals(Type.UPDATE)) {
            firestore.document(ID).get().addOnSuccessListener(data -> {
                plannedPayment = Objects.requireNonNull(data.toObject(PlannedPayment.class));

                name.setText(plannedPayment.getName());
                fromDate.setText(toSimpleDateFormat(plannedPayment.getStartDate().toDate(), DateFormat.DATE));
                reminder.setSelection(plannedPayment.getReminderOptions());
                frequency.setText(getStringArrayList(R.array.frequencyRepeat).get(Integer.valueOf(Objects.requireNonNull(plannedPayment.getFrequency().get("repeating")))));
                amount.setText(plannedPayment.getAmount());
                //currency.setSelection(Spinners.Currencies.getPosition(plannedPayment.getCurrencyID()));
                //account.setSelection(Spinners.Accounts.getPosition(plannedPayment.getAccountID()));
                costType.setSelection(plannedPayment.getType());
                plannedPayment.getCategoryID().get().addOnSuccessListener(t -> category.setText(Objects.requireNonNull(t.toObject(Category.class)).getName()));
                category.setTag(plannedPayment.getCategoryID());
                paymentType.setSelection(plannedPayment.getPaymentType());
                payee.setText(plannedPayment.getPayee());
                note.setText(plannedPayment.getNote());
            }).addOnFailureListener(f -> showSnackbar(getString(R.string.snackbar_notLoad)));
        } else {
            plannedPayment = new PlannedPayment();
            app.getLastUsedCategory().get().addOnSuccessListener(t -> category.setText(Objects.requireNonNull(t.toObject(Category.class)).getName()));
            category.setTag(plannedPayment.getCategoryID());
        }
        return type;
    }

    @Override
    public void onUpdate() {
        clearFocus();
        if (reformatText(name.getText()).isEmpty() | amount.getText().equals("0")) {
            showToast(getString(R.string.toast_error_empty));
        } else {
            Log.d("_Test", "[UpdatePlannedPayment] onUpdate() ");

            plannedPayment.setName(reformatText(name.getText()));
            plannedPayment.setStartDate(new Timestamp((Date)fromDate.getTag()));
            plannedPayment.setReminderOptions(reminder.getSelectedItemPosition());
            //noinspection unchecked
            plannedPayment.setFrequency((Map<String, String>)frequency.getTag());
            plannedPayment.setAmount(amount.getText().toString());
            plannedPayment.setCurrencyID(((QueryDocumentSnapshot)currency.getSelectedItem()).getReference());
            plannedPayment.setAccountID(((QueryDocumentSnapshot)account.getSelectedItem()).getReference());
            plannedPayment.setType(costType.getSelectedItemPosition());
            plannedPayment.setCategoryID((DocumentReference)category.getTag());
            plannedPayment.setPaymentType(paymentType.getSelectedItemPosition());
            plannedPayment.setPayee(payee.getText().toString());
            plannedPayment.setNote(note.getText().toString());

            if (type.equals(Type.CREATE)) {
                plannedPayment.setUserID(app.getUser());
                firestore.collection(PlannedPayment.COLLLECTION).add(plannedPayment).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.CREATED);
                    if (c.isSuccessful()) navigateUp();
                });
            } else {
               firestore.document(ID).set(plannedPayment).addOnCompleteListener(c -> {
                   showResultSnackbar(c.isSuccessful(), TAG, Message.UPDATED);
                   if (c.isSuccessful()) navigateUp();
               });
            }
        }
    }

    @Override
    public void onDelete() {
        Log.d("_Test", "[UpdatePlannedPayment] onDelete() ");
        clearFocus();
        firestore.document(ID).delete()
                .addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.DELETED);
                    if (c.isSuccessful()) navigateUp();
                });
    }

    private void clearFocus() {
        Log.d("_Test", "[UpdatePlannedPayment] clearFocus() ");
        name.clearFocus();
        payee.clearFocus();
        note.clearFocus();
    }

    @OnClick(R.id.data_Card_PlannedPayment_Amount)
    protected void updateValue() {
        Log.d("_Test", "[UpdatePlannedPayment] updateValue() ");
        InputValueDialog.getInstance(this);
    }

    @OnClick(R.id.data_Card_PlannedPayment_From)
    protected void updateDate() {
        Log.d("_Test", "[UpdatePlannedPayment] updateDate() ");
        Calendar c = Calendar.getInstance();
        c.setTime(plannedPayment.getStartDate().toDate());
        DateTimePickerDialog.getInstance(this, c, DateTimePickerDialog.Type.DATE, DateTimePickerDialog.DateOptions.NO_OPTIONS);
    }

    @OnClick(R.id.data_Card_PlannedPayment_Category)
    protected void updateCategory() {
        Log.d("_Test", "[UpdatePlannedPayment] updateCategory() ");
        DialogSelectionList.CategoryDialog.getInstance(this, this);
    }

    @OnClick(R.id.data_Card_PlannedPayment_Frequency)
    protected void updateFrequency() {
        Log.d("_Test", "[UpdatePlannedPayment] updateFrequency() ");
        new SetFrequencyDialog().getInstance(this);
    }

    @Override
    public void onFinishValueDialog(String finalValue) {
        Log.d("_Test", "[UpdatePlannedPayment] onFinishValueDialog() ");
        amount.setText(finalValue);
    }

    @Override
    public void onFinishDialog(Calendar calendar) {
        Log.d("_Test", "[UpdatePlannedPayment] onFinishDialog() ");
        fromDate.setText(toSimpleDateFormat(calendar.getTime(), DateFormat.DATE));
        fromDate.setTag(calendar.getTime());
    }

    @Override
    public void onSelectCategory(DocumentSnapshot snapshot) {
        Log.d("_Test", "[UpdatePlannedPayment] onSelectCategory() ");
        category.setTag(snapshot);
        category.setText(Objects.requireNonNull(snapshot.toObject(Category.class)).getName());
    }

    public class SetFrequencyDialog extends DialogFragment implements DateTimePickerDialog.setOnFinishListener {
        private final String defaultSet = "1";
        private String days = "0000000";
        private int option = -1;

        private final int[] weekDays = {
                R.id.dialog_Frequency_PeriodWeek_Day1,
                R.id.dialog_Frequency_PeriodWeek_Day2,
                R.id.dialog_Frequency_PeriodWeek_Day3,
                R.id.dialog_Frequency_PeriodWeek_Day4,
                R.id.dialog_Frequency_PeriodWeek_Day5,
                R.id.dialog_Frequency_PeriodWeek_Day6,
                R.id.dialog_Frequency_PeriodWeek_Day7};

        @BindView(R.id.dialog_Frequency_Repeating) Spinner repeating;
        @BindView(R.id.dialog_Frequency_Period) EditText period;
        @BindView(R.id.dialog_Frequency_PeriodText) TextView periodText;
        @BindView(R.id.dialog_Frequency_PeriodWeek) GridLayout periodWeek;
        @BindView(R.id.dialog_Frequency_PeriodMonth) RadioGroup periodMonth;
        @BindView(R.id.dialog_Frequency_PeriodEnd) Spinner periodEnd;
        @BindView(R.id.dialog_Frequency_EventComp) LinearLayout eventComp;
        @BindView(R.id.dialog_Frequency_Events) EditText event;
        @BindView(R.id.dialog_Frequency_EventsText) TextView eventCompText;
        @BindView(R.id.dialog_Frequency_DateComp) TextView dateComp;

        public void getInstance(@NonNull Fragment fragment) {
            SetFrequencyDialog dialog = new SetFrequencyDialog();
            dialog.setTargetFragment(fragment, 300);
            dialog.show(fragment.getParentFragmentManager(), "FREQUENCY");
            Log.d("_Test", "[SetFrequencyDialog] getInstance() ");
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog_frequency, container);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getObject(R.id.dialog_Frequency_Cancel).setOnClickListener(v -> dismiss());
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);

            Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);

            bindSimpleSpinner(repeating, getStringArrayList(R.array.frequencyRepeat));
            bindSimpleSpinner(periodEnd, getStringArrayList(R.array.frequencyEnd));
            return dialog;
        }

        @OnClick(R.id.dialog_Frequency_Ok)
        public void sendBackResult() {
            if (repeating.getSelectedItemPosition() == 1 && days.equals("0000000")) {
                showToast(getString(R.string.dialog_frequency_noDays_error));
                return;
            }
            if (repeating.getSelectedItemPosition() == 2 && periodMonth.getCheckedRadioButtonId() == -1) {
                showToast(getString(R.string.dialog_frequency_noMonth_error));
                return;
            }
            if (period.getText().toString().isEmpty()) period.setText(defaultSet);
            if (event.getText().toString().isEmpty()) event.setText(defaultSet);

            int Prd = Integer.valueOf(period.getText().toString());
            int End = periodEnd.getSelectedItemPosition();
            Timestamp EndDate = new Timestamp((Date)dateComp.getTag());
            int EndEvent = Integer.valueOf(event.getText().toString());

            switch (repeating.getSelectedItemPosition()) {
                case 0: //Daily
                    plannedPayment.setFrequency(new PlannedPayment.setFrequency().setDaily(Prd, End, EndDate, EndEvent));
                    break;
                case 1: //Weekly
                    plannedPayment.setFrequency(new PlannedPayment.setFrequency().setWeekly(Prd, End, EndDate, EndEvent, days));
                    break;
                case 2: //Monthly
                    plannedPayment.setFrequency(new PlannedPayment.setFrequency().setMonthly(Prd, End, EndDate, EndEvent, option));
                    break;
                case 3: //Yearly
                    plannedPayment.setFrequency(new PlannedPayment.setFrequency().setYearly(Prd, End, EndDate, EndEvent));
                    break;
            }

            Log.d("_Test", "[SetFrequencyDialog] sendBackResult() ");
            dismiss();
        }

        @OnFocusChange(R.id.dialog_Frequency_Repeating)
        protected void onPeriodLostFocus(boolean focus) {
            Log.d("_Test", "[SetFrequencyDialog] onPeriodLostFocus() ");
            if (!focus) period.setText(defaultSet);
        }

        @OnFocusChange(R.id.dialog_Frequency_Events)
        protected void onEndEventLostFocus(boolean focus) {
            Log.d("_Test", "[SetFrequencyDialog] onEndEventLostFocus() ");
            if (!focus) event.setText(defaultSet);
        }

        @OnClick(R.id.dialog_Frequency_DateComp)
        protected void updateTime() {
            Log.d("_Test", "[SetFrequencyDialog] updateTime() ");
            Calendar c = Calendar.getInstance();
            c.setTime((Date)dateComp.getTag());
            DateTimePickerDialog.getInstance(this, c, DateTimePickerDialog.Type.DATE, DateTimePickerDialog.DateOptions.NO_OPTIONS);
        }

        @Override
        public void onFinishDialog(Calendar calendar) {
            Log.d("_Test", "[SetFrequencyDialog] onFinishDialog() ");
            dateComp.setText(toSimpleDateFormat(calendar.getTime(), DateFormat.DATE));
            dateComp.setTag(calendar.getTime());
        }

        @OnClick({R.id.dialog_Frequency_PeriodWeek_Day1,
                R.id.dialog_Frequency_PeriodWeek_Day2,
                R.id.dialog_Frequency_PeriodWeek_Day3,
                R.id.dialog_Frequency_PeriodWeek_Day4,
                R.id.dialog_Frequency_PeriodWeek_Day5,
                R.id.dialog_Frequency_PeriodWeek_Day6,
                R.id.dialog_Frequency_PeriodWeek_Day7})
        protected void onWeekDayClicked(Button item) {
            Log.d("_Test", "[SetFrequencyDialog] onWeekDayClicked() button chosen: " + item.getTag().toString());
            int colorDefault = getResources().getColor(R.color.defaultTextColor, requireActivity().getTheme());
            int colorSelected = getResources().getColor(R.color.MM, requireActivity().getTheme());
            int place = (int)item.getTag() - 1;
            boolean isSelected = item.getCurrentTextColor() == colorSelected;

            item.setTextColor(isSelected ? colorDefault : colorSelected);
            days = new StringBuilder(days).replace(place, place, isSelected ? "0" : "1").toString();
        }

        @OnClick({R.id.dialog_Frequency_PeriodMonth_Option1, R.id.dialog_Frequency_PeriodMonth_Option2})
        protected void onMonthOption(RadioButton item) {
            Log.d("_Test", "[SetFrequencyDialog] onMonthOption() button chosen is: " + item.getTag().toString());
            option = (int)item.getTag();
        }

        @OnItemSelected(R.id.dialog_Frequency_Repeating)
        protected void onRepeatingChange(int position) {
            Log.d("_Test", "[SetFrequencyDialog] onRepeatingChange() position is: " + position);
            period.setText(defaultSet);

            switch (position) {
                case 0: //Daily
                    periodWeek.setVisibility(View.GONE);
                    periodText.setText(getResources().getQuantityString(R.plurals.dialog_Frequency_PeriodDay_Repeating, 1));
                    for (int i : weekDays) ((Button)getObject(i)).setTextColor(getResources().getColor(R.color.defaultTextColor, requireActivity().getTheme()));
                    periodMonth.clearCheck();
                    break;
                case 1: //Weekly
                    periodWeek.setVisibility(View.VISIBLE);
                    periodText.setText(getResources().getQuantityString(R.plurals.dialog_Frequency_PeriodWeek_Repeating, 1));
                    periodMonth.clearCheck();
                    break;
                case 2: //Monthly
                    periodWeek.setVisibility(View.GONE);
                    periodText.setText(getResources().getQuantityString(R.plurals.dialog_Frequency_PeriodMonth_Repeating, 1));
                    for (int i : weekDays) ((Button)getObject(i)).setTextColor(getResources().getColor(R.color.defaultTextColor, requireActivity().getTheme()));
                    break;
                case 3: //Yearly
                    periodWeek.setVisibility(View.GONE);
                    periodText.setText(getResources().getQuantityString(R.plurals.dialog_Frequency_PeriodYear_Repeating, 1));
                    for (int i : weekDays) ((Button)getObject(i)).setTextColor(getResources().getColor(R.color.defaultTextColor, requireActivity().getTheme()));
                    periodMonth.clearCheck();
                    break;
            }
        }

        @OnItemSelected(R.id.dialog_Frequency_PeriodEnd)
        protected void onEndChange(int position) {
            Log.d("_Test", "[SetFrequencyDialog] onEndChange() position is: " + position);

            switch (position) {
                case 0: //Forever
                    dateComp.setVisibility(View.GONE);
                    dateComp.setText(toSimpleDateFormat(new Date(System.currentTimeMillis()), DateFormat.DATE));
                    eventComp.setVisibility(View.GONE);
                    event.setText(defaultSet);
                    break;
                case 1: //Until date
                    dateComp.setVisibility(View.VISIBLE);
                    eventComp.setVisibility(View.GONE);
                    event.setText(defaultSet);
                    break;
                case 2: //For event
                    dateComp.setVisibility(View.GONE);
                    dateComp.setText(toSimpleDateFormat(new Date(System.currentTimeMillis()), DateFormat.DATE));
                    eventComp.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @OnTextChanged(R.id.dialog_Frequency_Events)
        protected void onEventTextChanged(CharSequence text) {
            Log.d("_Test", "[SetFrequencyDialog] onEventTextChanged() ");
            eventCompText.setText(getResources().getQuantityString(R.plurals.dialog_Frequency_Events, Integer.valueOf(String.valueOf(text))));
        }

        @OnTextChanged(R.id.dialog_Frequency_Period)
        protected void onPeriodTextChanged(CharSequence text) {
            Log.d("_Test", "[SetFrequencyDialog] onPeriodTextChanged() ");
            String result = "";
            int value = Integer.valueOf(String.valueOf(text));

            switch (repeating.getSelectedItemPosition()) {
                case 0: //Daily
                    result = getResources().getQuantityString(R.plurals.dialog_Frequency_PeriodDay_Repeating, value);
                    break;
                case 1: //Weekly
                    result = getResources().getQuantityString(R.plurals.dialog_Frequency_PeriodWeek_Repeating, value);
                    break;
                case 2: //Monthly
                    result = getResources().getQuantityString(R.plurals.dialog_Frequency_PeriodMonth_Repeating, value);
                    break;
                case 3: //Yearly
                    result = getResources().getQuantityString(R.plurals.dialog_Frequency_PeriodYear_Repeating, value);
                    break;
            }
            period.setText(result);
        }
    }
}