package com.umbrella.budgetapp.Layouts.UpdateFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.umbrella.budgetapp.Customs.DialogSelectionList;
import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.ShoppingList;
import com.umbrella.budgetapp.Database.Collections.Store;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class UpdateShoppingList extends BaseFragment implements UpdateListeners, DialogSelectionList.CategoryDialog.OnCategoryItemSelected, DialogSelectionList.StoreDialog.OnStoreItemSelected {
    private static final String TAG = "Shopping list";
    private String ID;
    private Type type;
    private ShoppingList shoppingList;

    @BindView(R.id.data_Card_ShoppingList_Name) EditText name;
    @BindView(R.id.data_Card_ShoppingList_Category) TextView category;
    @BindView(R.id.data_Card_ShoppingList_Store) TextView store;
    @BindView(R.id.data_Card_ShoppingList_Create) Button createList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ID = UpdateShoppingListArgs.fromBundle(requireArguments()).getShoppingListID();

        Log.d("_Test", "[UpdateShoppingList] onViewCreated() ID is: " + ID);

        type = onLoad(ID == null ? Type.CREATE : Type.UPDATE);

        createList.setVisibility(ID == null ? View.VISIBLE : View.GONE);

        SetToolbar(getString(type.equals(Type.UPDATE) ?
                R.string.title_edit_shopping_list :
                R.string.title_add_shopping_list),
                ToolbarNavIcon.CANCEL,
                type.equals(Type.UPDATE) ?
                R.menu.save_delete :
                0);

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
    @OnFocusChange(R.id.data_Card_ShoppingList_Name)
    protected void onFocusChange() {
        Log.d("_Test", "[UpdateShoppingList] onFocusChange() ");
        if (!name.hasFocus()) name.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public Type onLoad(Type type) {
        Log.d("_Test", "[UpdateShoppingList] onLoad() type is: " + type.name());

        if (type.equals(Type.UPDATE)) {
            firestore.document(ID).get().addOnSuccessListener(data -> {
                shoppingList = Objects.requireNonNull(data.toObject(ShoppingList.class));

                name.setText(shoppingList.getName());

                DocumentReference categoryRef = (shoppingList.getCategoryID() == null ? app.getLastUsedCategory() : shoppingList.getCategoryID());
                DocumentReference storeRef = (shoppingList.getStoreID() == null ? app.getLastUsedStore() : shoppingList.getStoreID());

                categoryRef.get().addOnSuccessListener(ct -> {
                    Category cats = Objects.requireNonNull(ct.toObject(Category.class));
                    category.setText(cats.getName());
                    category.setTag(ct.getReference());
                });
                storeRef.get().addOnSuccessListener(ct -> {
                    Store str = Objects.requireNonNull(ct.toObject(Store.class));
                    store.setText(str.getName());
                    store.setTag(ct.getReference());
                });
            }).addOnFailureListener(f -> showSnackbar(getString(R.string.snackbar_notLoad)));
        } else {
            shoppingList = new ShoppingList();

            app.getLastUsedCategory().get().addOnSuccessListener(data -> {
                Category cats = Objects.requireNonNull(data.toObject(Category.class));
                category.setText(cats.getName());
                category.setTag(data.getReference());
            });
            app.getLastUsedStore().get().addOnSuccessListener(data -> {
                Store str = Objects.requireNonNull(data.toObject(Store.class));
                store.setText(str.getName());
                store.setTag(data.getReference());
            });
        }
        return type;
    }

    @Override
    public void onUpdate() {
        name.clearFocus();
        if (reformatText(name.getText()).isEmpty()) {
            showToast(getString(R.string.toast_error_empty));
        } else {
            Log.d("_Test", "[UpdateShoppingList] onUpdate() ");
            shoppingList.setName(reformatText(name.getText()));
            shoppingList.setCategoryID(((QueryDocumentSnapshot)category.getTag()).getReference());
            shoppingList.setStoreID(((QueryDocumentSnapshot)store.getTag()).getReference());

            if (type.equals(Type.CREATE)) {
                shoppingList.setUserID(app.getUser());
                firestore.collection(ShoppingList.COLLECTION).add(shoppingList).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.CREATED);
                    if (c.isSuccessful()) navigateUp();
                });
            } else {
                firestore.document(ID).set(shoppingList).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.UPDATED);
                    if (c.isSuccessful()) navigateUp();
                });
            }
        }
    }

    @Override
    public void onDelete() {
        Log.d("_Test", "[UpdateShoppingList] onDelete() ");
        name.clearFocus();
        firestore.document(ID).delete()
                .addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.DELETED);
                    if (c.isSuccessful()) navigateUp();
                });
    }

    @OnClick(R.id.data_Card_ShoppingList_Create)
    protected void onCreateList() {
        Log.d("_Test", "[UpdateShoppingList] onCreateList() ");
        onUpdate();
    }

    @OnClick(R.id.data_Card_ShoppingList_Category)
    protected void updateCategory() {
        Log.d("_Test", "[UpdateShoppingList] updateCategory() ");
        DialogSelectionList.CategoryDialog.getInstance(this, this);
    }

    @OnClick(R.id.data_Card_ShoppingList_Store)
    protected void updateStore() {
        DialogSelectionList.StoreDialog.getInstance(this, this);
    }

    @Override
    public void onSelectCategory(DocumentSnapshot snapshot) {
        Log.d("_Test", "[UpdateShoppingList] onSelectCategory() ");
        category.setTag(snapshot);
        category.setText(Objects.requireNonNull(snapshot.toObject(Category.class)).getName());
    }

    @Override
    public void onSelectStore(DocumentSnapshot snapshot) {
        Log.d("_Test", "[UpdateShoppingList] onSelectStore() ");
        store.setTag(snapshot);
        store.setText(Objects.requireNonNull(snapshot.toObject(Store.class)).getName());
    }
}

