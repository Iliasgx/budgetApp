package com.umbrella.budgetapp.Layouts.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.umbrella.budgetapp.Customs.DateTimePickerDialog;
import com.umbrella.budgetapp.Customs.DialogSelectionList;
import com.umbrella.budgetapp.Customs.InputValueDialog;
import com.umbrella.budgetapp.Customs.Spinners;
import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Adapters.DaoShoppingListItems;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Database.Collections.ShoppingItem;
import com.umbrella.budgetapp.Database.Collections.ShoppingList;
import com.umbrella.budgetapp.Database.Collections.ShoppingListItem;
import com.umbrella.budgetapp.Database.Collections.User;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class ShoppingListItems extends BaseFragment implements DateTimePickerDialog.setOnFinishListener {
    private static final String TAG = "Shopping list";
    private DaoShoppingListItems adapter;
    private DocumentReference ID;
    private Timestamp reminderIsSet;

    private List<DocumentSnapshot> selectedItems = new LinkedList<>();

    private Button addRecord;
    private TextView reminder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ID = firestore.document(ShoppingListItemsArgs.fromBundle(requireArguments()).getShoppingListID());

        SetToolbar(getString(R.string.title_ShoppingList_Items), ToolbarNavIcon.BACK, R.menu.reminder_options);
        setNavigationOnClickListener(v -> navigateUp());
        setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuLayout_ReminderOptions_Reminder: onReminder(); break;
                case R.id.menuLayout_ReminderOptions_Rename: onRename(); break;
                case R.id.menuLayout_ReminderOptions_CreateRecord: onCreateRecord();break;
                case R.id.menuLayout_ReminderOptions_DeleteList: onDeleteList(); break;
            }
            return true;
        });

        ID.get().addOnSuccessListener(succeed -> reminderIsSet = Objects.requireNonNull(succeed.toObject(ShoppingList.class)).getReminder());

        getObject(R.id.fragmentFloatingActionButton).setVisibility(View.VISIBLE);

        addRecord = new Button(new ContextThemeWrapper(requireContext(), R.style.Button_Add), null, R.style.Button_Add);
        addRecord.setText(getString(R.string.customObject_shoppingListItems));
        addRecord.setVisibility(View.GONE);
        addRecord.setOnClickListener(v -> onCreateRecord());

        Log.d("_Test", "[ShoppingListItems] onViewCreated() ");
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = ID.collection(ShoppingListItem.COLLECTION);
        FirestoreRecyclerOptions<ShoppingListItem> options = new FirestoreRecyclerOptions.Builder<ShoppingListItem>().setQuery(query, ShoppingListItem.class).build();
        adapter = new DaoShoppingListItems(options);
        bindRecyclerView(getObject(R.id.fragmentRecyclerView), adapter);

        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            Log.d("_Test", "[ShoppingListItems] setUpRecyclerView() onItemClickListener");
            if (selectedItems.contains(documentSnapshot)) {
                Log.d("_Test", "[ShoppingListItems] setUpRecyclerView() remove selected");
                selectedItems.remove(documentSnapshot);
            } else {
                Log.d("_Test", "[ShoppingListItems] setUpRecyclerView() select");
                selectedItems.add(documentSnapshot);
            }
            addRecord.setVisibility(selectedItems.size() == 0 ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        Log.d("_Test", "[ShoppingListItems] onStart() ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d("_Test", "[ShoppingListItems] onStop() ");
    }

    @OnClick(R.id.fragmentFloatingActionButton)
    protected void onClick() {
        Log.d("_Test", "[ShoppingListItems] onClick() ");
        // TODO: or to shoppingItem fragment or dialog
    }

    private void onReminder() {
        Log.d("_Test", "[ShoppingListItems] onReminder() ");
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle(getString(R.string.dialog_Reminder_Title));
        dialog.setMessage(getString(R.string.dialog_Reminder_Date));

        reminder = new TextView(new ContextThemeWrapper(requireContext(), R.style.Spinner_Underlined), null, R.style.Spinner_Underlined);
        reminder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (reminderIsSet != null) reminder.setText(toSimpleDateFormat(reminderIsSet.toDate(), DateFormat.DATE_TIME));

        reminder.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            if (reminderIsSet != null) c.setTime(reminderIsSet.toDate());
            DateTimePickerDialog.getInstance(this, c, DateTimePickerDialog.Type.DATE_TIME, DateTimePickerDialog.DateOptions.AFTER_TODAY);
        });
        dialog.setView(reminder);

        dialog.setPositiveButton(getString(R.string.dialog_Reminder_Positive), (dlg, which) -> {
            if (reminder.getTag() != null) {
                ID.update(ShoppingList.REMINDER, new Timestamp((Date)reminder.getTag()));
                reminderIsSet = new Timestamp((Date)reminder.getTag());
                // TODO: SET reminder in ANDROID.

                Log.d("_Test", "[ShoppingListItems] onReminder() tag is not null - POSITIVE BUTTON");
            }
            dlg.dismiss();
        });
        dialog.setNegativeButton(getString(R.string.dialog_Reminder_Negative), (dlg, which) -> {
            reminder.setTag(null);
            reminderIsSet = null;
            ID.update(ShoppingList.REMINDER, null);

            Log.d("_Test", "[ShoppingListItems] onReminder() - NEGATIVE BUTTON");
            // TODO: REMOVE reminder of ANDROID.
        });
        dialog.show();
    }

    @Override
    public void onFinishDialog(Calendar calendar) {
        if (reminder != null) {
            reminder.setText(toSimpleDateFormat(calendar.getTime(), DateFormat.DATE_TIME));
            reminder.setTag(calendar.getTime());
            Log.d("_Test", "[ShoppingListItems] onFinishDialog() reminder not null");
        }
    }

    private void onRename() {
        Log.d("_Test", "[ShoppingListItems] onRename() ");
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle(getString(R.string.customObject_renameBoxTitle));
        dialog.setMessage(getString(R.string.customObject_renameBoxMessage));

        final EditText input = new EditText(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);
        dialog.setView(input);
        dialog.setIcon(R.drawable.shopping_list);

        dialog.setPositiveButton("SAVE", (dlg, which) -> {
            String value = input.getText().toString();

            if (!value.isEmpty()) {
                if (value.length() < 3) {
                    ID.update(ShoppingList.NAME, value).addOnCompleteListener(completed -> {
                        Log.d("_Test", "[ShoppingListItems] onRename() is okay - POSITIVE BUTTON");
                        showResultSnackbar(completed.isSuccessful(), TAG, Message.UPDATED);
                        dlg.dismiss();
                    });
                } else showToast(getString(R.string.shoppingListItems_shortInput, 3));
            } else showToast(getString(R.string.toast_error_empty));
        });
        dialog.setNegativeButton("CANCEL", (dlg, which) -> dlg.dismiss());
        dialog.show();
    }

    private void onCreateRecord() {
        Log.d("_Test", "[ShoppingListItems] onCreateRecord() ");
        new RecordDialog().getInstance();
    }

    private void onDeleteList() {
        ID.delete().addOnCompleteListener(completed -> {
            Log.d("_Test", "[ShoppingListItems] onDeleteList() ");
            showResultSnackbar(completed.isSuccessful(), TAG, Message.DELETED);
            navigateUp();
        });
    }

    public class RecordDialog extends DialogFragment implements UpdateListeners, DialogSelectionList.CategoryDialog.OnCategoryItemSelected, InputValueDialog.InputValueDialogListener {

        @BindView(R.id.dialog_Record_Account) Spinner account;
        @BindView(R.id.dialog_Record_Category) TextView category;
        @BindView(R.id.dialog_Record_Amount) TextView amount;
        @BindView(R.id.dialog_Record_Currency) Spinner currency;
        @BindView(R.id.dialog_Record_Note) EditText note;

        public void getInstance() {
            RecordDialog dialog = new RecordDialog();
            dialog.setTargetFragment(this, 300);
            dialog.show(getParentFragmentManager(), "RECORD");
            Log.d("_Test", "[RecordDialog] getInstance() ");
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog_record, container);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getObject(R.id.dialog_Record_Cancel).setOnClickListener(v -> dismiss());

            new Spinners.Accounts(this, account);
            new Spinners.Currencies(this, currency);

            onLoad(Type.CREATE);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
            return dialog;
        }

        /*
         * Hide keyboard if focus is lost.
         */
        @OnFocusChange(R.id.dialog_Record_Note)
        protected void onFocusChange() {
            Log.d("_Test", "[RecordDialog] onFocusChange() ");
            if (!note.hasFocus()) note.onEditorAction(EditorInfo.IME_ACTION_DONE);
        }

        @Override
        public Type onLoad(Type type) {
            app.getLastUsedCategory().get().addOnSuccessListener(t -> category.setText(Objects.requireNonNull(t.toObject(Category.class)).getName()));
            category.setTag(app.getLastUsedCategory());
            //account.setSelection(Spinners.Accounts.getPosition(app.getSelectedAccounts().get(0)));

            AtomicDouble AtmDouble = new AtomicDouble(0.00);
            selectedItems.forEach(curr -> {
                ShoppingListItem item = Objects.requireNonNull(curr.toObject(ShoppingListItem.class));

                item.getShoppingItemID().get().addOnSuccessListener(data -> {
                    ShoppingItem shoppingItem = Objects.requireNonNull(data.toObject(ShoppingItem.class));
                    amount.setText(String.valueOf(AtmDouble.addAndGet(Double.valueOf(shoppingItem.getDefAmount()) * item.getNumber())));
                });
            });
            Log.d("_Test", "[RecordDialog] onLoad() ");
            return type;
        }

        @Override
        @OnClick(R.id.dialog_Record_Create)
        public void onUpdate() {
            note.clearFocus();
            if (amount.getText().equals("0")) {
                showToast(getString(R.string.toast_error_empty));
            } else {
                app.getUserDocument().get().addOnSuccessListener(user -> {
                    ID.get().addOnSuccessListener(list -> {

                        Log.d("_Test", "[RecordDialog] onUpdate() onSuccessListener");
                        Record record = new Record(
                                app.getUser(),
                                reformatText(note.getText()),
                                (DocumentReference)category.getTag(),
                                amount.getText().toString(),
                                ((QueryDocumentSnapshot)currency.getSelectedItem()).getReference(),
                                1,
                                ((QueryDocumentSnapshot)account.getSelectedItem()).getReference(),
                                0,
                                Objects.requireNonNull(user.toObject(User.class)).getFullName(),
                                Objects.requireNonNull(list.toObject(ShoppingList.class)).getStoreID(),
                                new Timestamp(new Date(System.currentTimeMillis())),
                                setGeoPoint());

                        firestore.collection(Record.COLLECTION).add(record).addOnCompleteListener(completed -> {
                            showResultSnackbar(completed.isSuccessful(), "record", Message.CREATED);

                            if (completed.isSuccessful()) {
                                dismiss();
                                navigateUp();
                            }
                        });
                    });
                });
            }

            Log.d("_Test", "[RecordDialog] onUpdate() ");
        }

        @Override
        public void onDelete(){}

        private GeoPoint setGeoPoint() {
            Log.d("_Test", "[RecordDialog] setGeoPoint() ");
            LocationManager MT = (LocationManager)requireActivity().getSystemService(Context.LOCATION_SERVICE);
            if (MT.isProviderEnabled(LocationManager.GPS_PROVIDER) || MT.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(requireContext());
                client.getLastLocation().addOnSuccessListener(succeeded -> new GeoPoint(succeeded.getLatitude(), succeeded.getLongitude()));
            }
            return null;
        }

        @OnClick(R.id.dialog_Record_Category)
        protected void onCategoryClick() {
            Log.d("_Test", "[RecordDialog] onCategoryClick() ");
            DialogSelectionList.CategoryDialog.getInstance(this, this);
        }

        @OnClick(R.id.dialog_Record_Amount)
        protected void onAmountClick() {
            Log.d("_Test", "[RecordDialog] onAmountClick() ");
            InputValueDialog.getInstance(this);
        }

        @Override
        public void onSelectCategory(DocumentSnapshot snapshot) {
            Log.d("_Test", "[RecordDialog] onSelectCategory() ");
            category.setTag(snapshot);
            category.setText(Objects.requireNonNull(snapshot.toObject(Category.class)).getName());
        }

        @Override
        public void onFinishValueDialog(String finalValue) {
            Log.d("_Test", "[RecordDialog] onFinishValueDialog() value is: " + finalValue);
            amount.setText(finalValue);
        }
    }
}
