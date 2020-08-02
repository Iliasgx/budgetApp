package com.umbrella.budgetapp.Layouts.Fragments;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Database.Adapters.DaoShoppingItems;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.ShoppingItem;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class ShoppingItems extends BaseFragment {
    private DaoShoppingItems adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetToolbar(getString(R.string.title_ShoppingItems), ToolbarNavIcon.BACK, R.menu.search);
        setNavigationOnClickListener(v -> {
            ((InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), 0);
            navigateUp();
        });
        setOnMenuItemClickListener(item -> {
            SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView)item.getActionView();

            if (searchView != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
                searchView.setIconifiedByDefault(true);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        updateAdapter(newText);
                        item.collapseActionView();
                        return true;
                    }
                });
            }
            return true;
        });

        Log.d("_Test", "[ShoppingItems] onViewCreated() ");
        updateAdapter(null);
    }

    private void updateAdapter(@Nullable String search) {
        Query query = firestore.collection(ShoppingItem.COLLECTION).whereEqualTo(ShoppingItem.USER, app.getUser());
        if (search != null && !search.isEmpty()) {
            Log.d("_Test", "[ShoppingItems] updateAdapter() search is not empty and is: " + search);
            query.whereGreaterThanOrEqualTo(Category.NAME, search);
        }

        FirestoreRecyclerOptions<ShoppingItem> options = new FirestoreRecyclerOptions.Builder<ShoppingItem>().setQuery(query, ShoppingItem.class).build();
        adapter = new DaoShoppingItems(options);
        bindRecyclerView(getObject(R.id.fragmentRecyclerView), adapter);

        Log.d("_Test", "[ShoppingItems] updateAdapter() ");
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        Log.d("_Test", "[ShoppingItems] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d("_Test", "[ShoppingItems] onStop() ");
    }

    @OnClick(R.id.fragmentFloatingActionButton)
    protected void onAdd() {
        Log.d("_Test", "[ShoppingItems] onAdd() ");
        new AddItemDialog().getInstance();
    }

    public class AddItemDialog extends DialogFragment {

        @BindView(R.id.dialog_newItem_ItemName) EditText name;
        @BindView(R.id.dialog_newItem_ItemPrice) EditText price;

        public void getInstance() {
            AddItemDialog dialog = new AddItemDialog();
            dialog.setTargetFragment(this, 300);
            dialog.show(getParentFragmentManager(), "ITEM");
            Log.d("_Test", "[AddItemDialog] getInstance() ");
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog_new_item, container);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getObject(R.id.dialog_newItem_Cancel).setOnClickListener(v -> dismiss());
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
            return dialog;
        }

        @OnClick(R.id.dialog_newItem_Ok)
        public void sendBackResult() {
            String name = reformatText(((EditText)getObject(R.id.dialog_newItem_ItemName)).getText());
            String price = reformatText(((EditText)getObject(R.id.dialog_newItem_ItemPrice)).getText());

            if (!name.isEmpty() && name.length() < 3) {
                if (!price.isEmpty() && Double.valueOf(price) == 0) {
                    firestore.collection(ShoppingItem.COLLECTION).add(new ShoppingItem(app.getUser(), name, price));
                    Log.d("_Test", "[AddItemDialog] sendBackResult() is okay");
                } else showToast(getString(R.string.record_value_error));
            } else showToast(getString(R.string.toast_error_empty));
            dismiss();
        }
    }
}

