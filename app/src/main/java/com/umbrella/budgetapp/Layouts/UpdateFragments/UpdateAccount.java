package com.umbrella.budgetapp.Layouts.UpdateFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.umbrella.budgetapp.Customs.InputValueDialog;
import com.umbrella.budgetapp.Customs.Spinners;
import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Collections.Account;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class UpdateAccount extends BaseFragment implements UpdateListeners, InputValueDialog.InputValueDialogListener {
    private static final String TAG = "Account";
    private String ID = null;
    private Type type;
    private Account account;

    @BindView(R.id.data_Card_Account_Name) EditText name;
    @BindView(R.id.data_Card_Account_Type) Spinner accountType;
    @BindView(R.id.data_Card_Account_CurrValue) TextView currentValue;
    @BindView(R.id.data_Card_Account_Currency) Spinner currency;
    @BindView(R.id.data_Card_Account_Color) Spinner color;
    @BindView(R.id.data_Card_Account_ExcludeStats) Switch excludeStats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ID = UpdateAccountArgs.fromBundle(requireArguments()).getAccountID();

        Log.d("_Test", "[UpdateAccount] onViewCreated() ID is: " + ID);

        bindSimpleSpinner(accountType, getStringArrayList(R.array.accountType));
        new Spinners.Currencies(this, currency);
        new Spinners.Colors(this, color, Spinners.Colors.Size.LARGE);

        type = onLoad(ID == null ? Type.CREATE : Type.UPDATE);

        SetToolbar(getString(type.equals(Type.UPDATE) ?
                R.string.title_edit_account :
                R.string.title_add_account),
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
    @OnFocusChange(R.id.data_Card_Account_Name)
    protected void onFocusChange() {
        Log.d("_Test", "[UpdateAccount] onFocusChange() ");
        if (!name.hasFocus()) name.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public Type onLoad(Type type) {
        Log.d("_Test", "[UpdateAccount] onLoad() type is: " + type.name());
        if (type.equals(Type.UPDATE)) {
            firestore.document(ID).get().addOnSuccessListener(data -> {
                account = Objects.requireNonNull(data.toObject(Account.class));

                name.setText(account.getName());
                accountType.setSelection(account.getType());
                currentValue.setText(account.getCurrentValue());
                //currency.setSelection(Spinners.Currencies.getPosition(account.getCurrencyID()));
                color.setSelection(account.getColor());
                excludeStats.setChecked(account.isExcludeStats());
            }).addOnFailureListener(f -> showSnackbar(getString(R.string.snackbar_notLoad)));
        } else account = new Account();
        return type;
    }

    @Override
    public void onUpdate() {
        name.clearFocus();
        if (reformatText(name.getText()).isEmpty() | currentValue.getText().equals("0")) {
            showToast(getString(R.string.toast_error_empty));
        } else {
            Log.d("_Test", "[UpdateAccount] onUpdate() ");
            account.setName(reformatText(name.getText()));
            account.setType(accountType.getSelectedItemPosition());
            account.setCurrentValue(currentValue.getText().toString());
            account.setCurrencyID(((QueryDocumentSnapshot)currency.getSelectedItem()).getReference());
            account.setColor(color.getSelectedItemPosition());
            account.setExcludeStats(excludeStats.isChecked());

            if (type.equals(Type.CREATE)) {
                app.getUserDocument().collection(Account.COLLECTION).get().addOnSuccessListener(find -> {
                    account.setPosition(find.size());
                    app.getUserDocument().collection(Account.COLLECTION).add(account).addOnCompleteListener(c -> {
                        showResultSnackbar(c.isSuccessful(), TAG, Message.CREATED);
                        if (c.isSuccessful()) navigateUp();
                    });
                });
            } else {
                firestore.document(ID).set(account).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.UPDATED);
                    if (c.isSuccessful()) navigateUp();
                });
            }
        }
    }

    @Override
    public void onDelete() {
        Log.d("_Test", "[UpdateAccount] onDelete() ");
        name.clearFocus();
        firestore.document(ID).delete()
                .addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.DELETED);
                    if (c.isSuccessful()) navigateUp();
                });
    }

    @OnClick(R.id.data_Card_Account_CurrValue)
    protected void updateValue() {
        Log.d("_Test", "[UpdateAccount] updateValue() ");
        InputValueDialog.getInstance(this);
    }

    @Override
    public void onFinishValueDialog(String finalValue) {
        Log.d("_Test", "[UpdateAccount] onFinishValueDialog() value is: " + finalValue);
        currentValue.setText(finalValue);
    }
}

