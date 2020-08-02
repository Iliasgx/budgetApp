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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.umbrella.budgetapp.Customs.DialogSelectionList;
import com.umbrella.budgetapp.Customs.InputValueDialog;
import com.umbrella.budgetapp.Customs.Spinners;
import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Store;
import com.umbrella.budgetapp.Database.Collections.Template;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class UpdateTemplate extends BaseFragment implements UpdateListeners, InputValueDialog.InputValueDialogListener, DialogSelectionList.CategoryDialog.OnCategoryItemSelected, DialogSelectionList.StoreDialog.OnStoreItemSelected {
    private static final String TAG = "Template";
    private String ID;
    private Type type;
    private Template template;

    @BindView(R.id.data_Card_Template_Name) EditText name;
    @BindView(R.id.data_Card_Template_Amount) TextView amount;
    @BindView(R.id.data_Card_Template_Currency) Spinner currency;
    @BindView(R.id.data_Card_Template_Account) Spinner account;
    @BindView(R.id.data_Card_Template_Category) TextView category;
    @BindView(R.id.data_Card_Template_Type) Spinner recordType;
    @BindView(R.id.data_Card_Template_PayType) Spinner payType;
    @BindView(R.id.data_Card_Template_Store) TextView store;
    @BindView(R.id.data_Card_Template_Payee) EditText payee;
    @BindView(R.id.data_Card_Template_Note) EditText note;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_template, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ID = UpdateTemplateArgs.fromBundle(requireArguments()).getTemplateID();

        Log.d("_Test", "[UpdateTemplate] onViewCreated() ID is: " + ID);

        new Spinners.Currencies(this, currency);
        new Spinners.Accounts(this, account);
        bindSimpleSpinner(recordType, getStringArrayList(R.array.recordType));
        bindSimpleSpinner(payType, getStringArrayList(R.array.paymentType));

        type = onLoad(ID == null ? Type.CREATE : Type.UPDATE);

        SetToolbar(getString(type.equals(Type.UPDATE) ?
                R.string.title_edit_template :
                R.string.title_add_template),
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
    @OnFocusChange({R.id.data_Card_Template_Name, R.id.data_Card_Template_Payee, R.id.data_Card_Template_Note})
    protected void onFocusChange() {
        Log.d("_Test", "[UpdateTemplate] onFocusChange() ");
        if (!name.hasFocus()) name.onEditorAction(EditorInfo.IME_ACTION_DONE);
        if (!payee.hasFocus()) payee.onEditorAction(EditorInfo.IME_ACTION_DONE);
        if (!note.hasFocus()) note.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    private void clearFocus() {
        Log.d("_Test", "[UpdateTemplate] clearFocus() ");
        name.clearFocus();
        payee.clearFocus();
        note.clearFocus();
    }

    @Override
    public Type onLoad(Type type) {
        Log.d("_Test", "[UpdateTemplate] onLoad() type is: " + type.name());

        if (type.equals(Type.UPDATE)) {
            firestore.document(ID).get().addOnSuccessListener(data -> {
                template = Objects.requireNonNull(data.toObject(Template.class));

                name.setText(template.getName());
                amount.setText(template.getAmount());
                //currency.setSelection(Spinners.Currencies.getPosition(template.getCurrencyID()));
                //account.setSelection(Spinners.Accounts.getPosition(template.getAccountID()));
                template.getCategoryID().get().addOnSuccessListener(t -> category.setText(Objects.requireNonNull(t.toObject(Category.class)).getName()));
                category.setTag(template.getCategoryID());
                recordType.setSelection(template.getType());
                payType.setSelection(template.getPaymentType());
                template.getStoreID().get().addOnSuccessListener(t -> store.setText(Objects.requireNonNull(t.toObject(Store.class)).getName()));
                store.setTag(template.getStoreID());
                payee.setText(template.getPayee());
                note.setText(template.getNote());
            }).addOnFailureListener(f -> showSnackbar(getString(R.string.snackbar_notLoad)));
        } else {
            template = new Template();

            app.getLastUsedCategory().get().addOnSuccessListener(t -> category.setText(Objects.requireNonNull(t.toObject(Category.class)).getName()));
            category.setTag(app.getLastUsedCategory());
        }
        return type;
    }

    @Override
    public void onUpdate() {
        clearFocus();
        if (reformatText(name.getText()).isEmpty() | amount.getText().equals("0")) {
            showToast(getString(R.string.toast_error_empty));
        } else {
            Log.d("_Test", "[UpdateTemplate] onUpdate() ");

            template.setName(reformatText(name.getText()));
            template.setAmount(amount.getText().toString());
            template.setCurrencyID(((QueryDocumentSnapshot)currency.getTag()).getReference());
            template.setAccountID(((QueryDocumentSnapshot)account.getTag()).getReference());
            template.setCategoryID((DocumentReference)category.getTag());
            template.setType(recordType.getSelectedItemPosition());
            template.setPaymentType(payType.getSelectedItemPosition());
            template.setStoreID(((QueryDocumentSnapshot)store.getTag()).getReference());
            template.setPayee(reformatText(payee.getText()));
            template.setNote(reformatText(note.getText()));

            if (type.equals(Type.CREATE)) {
                app.getUserDocument().collection(Template.COLLECTION).get().addOnSuccessListener(find -> {
                    template.setPosition(find.size());
                    app.getUserDocument().collection(Template.COLLECTION).add(template).addOnCompleteListener(c -> {
                        showResultSnackbar(c.isSuccessful(), TAG, Message.CREATED);
                        if (c.isSuccessful()) navigateUp();
                    });
                });
            } else {
                firestore.document(ID).set(template).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.UPDATED);
                    if (c.isSuccessful()) navigateUp();
                });
            }
        }
    }

    @Override
    public void onDelete() {
        Log.d("_Test", "[UpdateTemplate] onDelete() ");

        clearFocus();
        firestore.document(ID).delete()
                .addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.DELETED);
                    if (c.isSuccessful()) navigateUp();
                });
    }

    @OnClick(R.id.data_Card_Template_Amount)
    protected void updateValue() {
        Log.d("_Test", "[UpdateTemplate] updateValue() ");
        InputValueDialog.getInstance(this);
    }

    @OnClick(R.id.data_Card_Template_Category)
    protected void updateCategory() {
        Log.d("_Test", "[UpdateTemplate] updateCategory() ");
        DialogSelectionList.CategoryDialog.getInstance(this, this);
    }

    @OnClick(R.id.data_Card_Template_Store)
    protected void updateStore() {
        Log.d("_Test", "[UpdateTemplate] updateStore() ");
            DialogSelectionList.StoreDialog.getInstance(this, this);
    }

    @Override
    public void onFinishValueDialog(String finalValue) {
        Log.d("_Test", "[UpdateTemplate] onFinishValueDialog() ");
        amount.setText(finalValue);
    }

    @Override
    public void onSelectCategory(DocumentSnapshot snapshot) {
        Log.d("_Test", "[UpdateTemplate] onSelectCategory() ");
        category.setTag(snapshot);
        category.setText(Objects.requireNonNull(snapshot.toObject(Category.class)).getName());
    }

    @Override
    public void onSelectStore(DocumentSnapshot snapshot) {
        Log.d("_Test", "[UpdateTemplate] onSelectStore() ");
        store.setTag(snapshot);
        store.setText(Objects.requireNonNull(snapshot.toObject(Store.class)).getName());
    }
}

