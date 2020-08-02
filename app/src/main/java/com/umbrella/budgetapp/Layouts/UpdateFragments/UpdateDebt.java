package com.umbrella.budgetapp.Layouts.UpdateFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.umbrella.budgetapp.Customs.DateTimePickerDialog;
import com.umbrella.budgetapp.Customs.DialogSelectionList;
import com.umbrella.budgetapp.Customs.DialogSelectionList.CategoryDialog.OnCategoryItemSelected;
import com.umbrella.budgetapp.Customs.InputValueDialog;
import com.umbrella.budgetapp.Customs.Spinners;
import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Debt;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class UpdateDebt extends BaseFragment implements UpdateListeners, InputValueDialog.InputValueDialogListener, DateTimePickerDialog.setOnFinishListener, OnCategoryItemSelected {
    private static final String TAG = "Debt";
    private String ID;
    private int debtType;
    private Type type;
    private Debt debt;

    @BindView(R.id.data_Card_Debt_Name) EditText name;
    @BindView(R.id.data_Card_Debt_Description) EditText description;
    @BindView(R.id.data_Card_Debt_Category) TextView category;
    @BindView(R.id.data_Card_Debt_Account) Spinner account;
    @BindView(R.id.data_Card_Debt_Amount) TextView amount;
    @BindView(R.id.data_Card_Debt_Currency) Spinner currency;
    @BindView(R.id.data_Card_Debt_Date) TextView date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_debt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ID = UpdateDebtArgs.fromBundle(requireArguments()).getDebtID();
        debtType = UpdateDebtArgs.fromBundle(requireArguments()).getDebtType();

        Log.d("_Test", "[UpdateDebt] onViewCreated() ID is: " + ID + "\nDebtType is: " + debtType);

        new Spinners.Accounts(this, account);
        new Spinners.Currencies(this, currency);

        type = onLoad(ID == null ? Type.CREATE : Type.UPDATE);

        SetToolbar(getString(type.equals(Type.UPDATE) ?
                R.string.title_edit_debt :
                debtType == Debt.DebtType.LENT.ordinal() ?
                        R.string.title_add_debt_lent :
                        R.string.title_add_debt_borrowed),
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
            name.clearFocus();
            navigateUp();
        });
    }

    /*
     * Hide keyboard if focus is lost.
     */
    @OnFocusChange({R.id.data_Card_Debt_Name, R.id.data_Card_Debt_Description})
    protected void onFocusChange() {
        Log.d("_Test", "[UpdateDebt] onFocusChange() ");
        if (!name.hasFocus()) name.onEditorAction(EditorInfo.IME_ACTION_DONE);
        if (!description.hasFocus()) description.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public Type onLoad(Type type) {
        Log.d("_Test", "[UpdateDebt] onLoad() type is " + type.name());

        if (type.equals(Type.UPDATE)) {
            firestore.document(ID).get().addOnSuccessListener(data -> {
                debt = Objects.requireNonNull(data.toObject(Debt.class));

                name.setText(debt.getName());
                description.setText(debt.getDescription());
                debt.getCategoryID().get().addOnSuccessListener(t -> category.setText(Objects.requireNonNull(t.toObject(Category.class)).getName()));
                category.setTag(debt.getCategoryID());
                //account.setSelection(Spinners.Accounts.getPosition(debt.getAccountID()));
                amount.setText(debt.getAmount());
                //currency.setSelection(Spinners.Currencies.getPosition(debt.getCurrencyID()));
                date.setText(toSimpleDateFormat(debt.getDate().toDate(), DateFormat.DATE_TIME));
            }).addOnFailureListener(f -> showSnackbar(getString(R.string.snackbar_notLoad)));
        } else {
            debt = new Debt();
            app.getLastUsedCategory().get().addOnSuccessListener(data -> {
                Category cats = Objects.requireNonNull(data.toObject(Category.class));
                category.setText(cats.getName());
                category.setTag(data.getReference());
            });
        }
        return type;
    }

    @Override
    public void onUpdate() {
        name.clearFocus();
        description.clearFocus();
        if (reformatText(name.getText()).isEmpty() | amount.getText().equals("0")) {
            showToast(getString(R.string.toast_error_empty));
        } else {
            Log.d("_Test", "[UpdateDebt] onUpdate() ");

            debt.setUserID(app.getUser());
            debt.setName(reformatText(name.getText()));
            debt.setDescription(reformatText(description.getText()));
            debt.setCategoryID((DocumentReference)category.getTag());
            debt.setAccountID((DocumentReference)account.getSelectedItem());
            debt.setCurrencyID((DocumentReference)currency.getSelectedItem());
            debt.setAmount(amount.getText().toString());
            debt.setDate(new Timestamp((Date)date.getTag()));
            debt.setDebtType(debtType);

            if (type.equals(Type.CREATE)) {
                firestore.collection(Debt.COLLECTION).add(debt).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.CREATED);
                    if (c.isSuccessful()) navigateUp();
                });
            } else {
                firestore.document(ID).set(debt).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.UPDATED);
                    if (c.isSuccessful()) navigateUp();
                });
            }
        }
    }

    @Override
    public void onDelete() {
        Log.d("_Test", "[UpdateDebt] onDelete() ");
        name.clearFocus();
        description.clearFocus();
        firestore.document(ID).delete()
                .addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.DELETED);
                    if (c.isSuccessful()) navigateUp();
                });
    }

    @OnClick(R.id.data_Card_Debt_Amount)
    protected void updateValue() {
        Log.d("_Test", "[UpdateDebt] updateValue() ");
        InputValueDialog.getInstance(this);
    }

    @OnClick(R.id.data_Card_Debt_Date)
    protected void updateDate() {
        Log.d("_Test", "[UpdateDebt] updateDate() ");
        Calendar c = Calendar.getInstance();
        c.setTime(debt.getDate().toDate());
        DateTimePickerDialog.getInstance(this, c, DateTimePickerDialog.DateOptions.BEFORE_TODAY);
    }

    @OnClick(R.id.data_Card_Debt_Category)
    protected void updateCategory() {
        Log.d("_Test", "[UpdateDebt] updateCategory() ");
        DialogSelectionList.CategoryDialog.getInstance(this, this);
    }

    @Override
    public void onFinishValueDialog(String finalValue) {
        Log.d("_Test", "[UpdateDebt] onFinishValueDialog() ");
        amount.setText(finalValue);
    }

    @Override
    public void onFinishDialog(Calendar calendar) {
        Log.d("_Test", "[UpdateDebt] onFinishDialog() ");
        date.setText(toSimpleDateFormat(calendar.getTime(), DateFormat.DATE_TIME));
        date.setTag(calendar.getTime());
    }

    @Override
    public void onSelectCategory(DocumentSnapshot snapshot) {
        Log.d("_Test", "[UpdateDebt] onSelectCategory() ");
        category.setTag(snapshot);
        category.setText(Objects.requireNonNull(snapshot.toObject(Category.class)).getName());
    }
}

