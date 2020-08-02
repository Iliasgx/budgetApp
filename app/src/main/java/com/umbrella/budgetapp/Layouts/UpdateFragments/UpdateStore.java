package com.umbrella.budgetapp.Layouts.UpdateFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.umbrella.budgetapp.Customs.DialogSelectionList;
import com.umbrella.budgetapp.Customs.Spinners;
import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Collections.Account;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Store;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class UpdateStore extends BaseFragment implements UpdateListeners, DialogSelectionList.CategoryDialog.OnCategoryItemSelected {
    private static final String TAG = "Store";
    private String ID;
    private Type type;
    private Store store;

    @BindView(R.id.data_Card_Store_Img) ImageView img;
    @BindView(R.id.data_Card_Store_Name) EditText name;
    @BindView(R.id.data_Card_Store_Currency) Spinner currency;
    @BindView(R.id.data_Card_Store_Category) TextView category;
    @BindView(R.id.data_Card_Store_Note) EditText note;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ID = UpdateStoreArgs.fromBundle(requireArguments()).getStoreID();

        Log.d("_Test", "[UpdateStore] onViewCreated() ID is: " + ID);

        new Spinners.Currencies(this, currency);

        type = onLoad(ID == null ? Type.CREATE : Type.UPDATE);

        SetToolbar(getString(type.equals(Type.UPDATE) ?
                R.string.title_edit_store :
                R.string.title_add_store),
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
            note.clearFocus();
            navigateUp();
        });
    }

    /*
     * Hide keyboard if focus is lost.
     */
    @OnFocusChange(R.id.data_Card_Store_Name)
    protected void onFocusChange() {
        Log.d("_Test", "[UpdateStore] onFocusChange() ");
        if (!name.hasFocus()) name.onEditorAction(EditorInfo.IME_ACTION_DONE);
        if (!note.hasFocus()) note.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public Type onLoad(Type type) {
        Log.d("_Test", "[UpdateStore] onLoad() type is: " + type.name());

        if (type.equals(Type.UPDATE)) {
            firestore.document(ID).get().addOnSuccessListener(data -> {
                store = Objects.requireNonNull(data.toObject(Store.class));

                name.setText(store.getName());
                //currency.setSelection(Spinners.Currencies.getPosition(store.getCurrencyID()));
                store.getCategoryID().get().addOnSuccessListener(c -> {
                    Category cats = Objects.requireNonNull(c.toObject(Category.class));
                    category.setText(cats.getName());
                    category.setTag(c.getReference());
                });
            }).addOnFailureListener(f -> showSnackbar(getString(R.string.snackbar_notLoad)));
        } else store = new Store();
        return type;
    }

    @Override
    public void onUpdate() {
        name.clearFocus();
        note.clearFocus();
        if (reformatText(name.getText()).isEmpty()) {
            showToast(getString(R.string.toast_error_empty));
        } else {
            Log.d("_Test", "[UpdateStore] onUpdate() ");

            store.setName(reformatText(name.getText()));
            store.setNote(reformatText(note.getText()));
            store.setCategoryID(((QueryDocumentSnapshot)category.getTag()).getReference());
            store.setCurrencyID(((QueryDocumentSnapshot)currency.getSelectedItem()).getReference());

            if (type.equals(Type.CREATE)) {
                store.setUserID(app.getUser());
                app.getUserDocument().collection(Account.COLLECTION).add(store).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.CREATED);
                    if (c.isSuccessful()) navigateUp();
                });
            } else {
                firestore.document(ID).set(store).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.UPDATED);
                    if (c.isSuccessful()) navigateUp();
                });
            }
        }
    }

    @Override
    public void onDelete() {
        Log.d("_Test", "[UpdateStore] onDelete() ");
        name.clearFocus();
        note.clearFocus();
        firestore.document(ID).delete()
                .addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.DELETED);
                    if (c.isSuccessful()) navigateUp();
                });
    }

    @OnClick(R.id.data_Card_Store_Category)
    protected void updateCategory() {
        Log.d("_Test", "[UpdateStore] updateCategory() ");
        DialogSelectionList.CategoryDialog.getInstance(this, this);
    }

    @Override
    public void onSelectCategory(DocumentSnapshot snapshot) {
        Log.d("_Test", "[UpdateStore] onSelectCategory() ");
        category.setTag(snapshot);
        category.setText(Objects.requireNonNull(snapshot.toObject(Category.class)).getName());
    }
}

