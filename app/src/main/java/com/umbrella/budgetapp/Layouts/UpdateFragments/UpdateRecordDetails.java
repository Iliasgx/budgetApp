package com.umbrella.budgetapp.Layouts.UpdateFragments;

import android.content.Context;
import android.location.LocationManager;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.umbrella.budgetapp.Customs.DateTimePickerDialog;
import com.umbrella.budgetapp.Customs.DialogSelectionList;
import com.umbrella.budgetapp.Customs.InputValueDialog;
import com.umbrella.budgetapp.Customs.Spinners;
import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Collections.Account;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Database.Collections.Store;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.MainActivity;
import com.umbrella.budgetapp.R;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class UpdateRecordDetails extends BaseFragment implements UpdateListeners, InputValueDialog.InputValueDialogListener, DateTimePickerDialog.setOnFinishListener, DialogSelectionList.CategoryDialog.OnCategoryItemSelected, DialogSelectionList.StoreDialog.OnStoreItemSelected {
    private static final String TAG = "Record";
    private String ID;
    private Type type;
    private Record record;

    private UpdateRecordDetailsArgs args;
    private boolean save = true;

    private transient boolean isDate = false;

    @BindView(R.id.data_Card_RecordDetails_Description) EditText description;
    @BindView(R.id.data_Card_RecordDetails_Category) TextView category;
    @BindView(R.id.data_Card_RecordDetails_Amount) TextView amount;
    @BindView(R.id.data_Card_RecordDetails_Currency) Spinner currency;
    @BindView(R.id.data_Card_RecordDetails_PayType) Spinner payType;
    @BindView(R.id.data_Card_RecordDetails_Date) TextView date;
    @BindView(R.id.data_Card_RecordDetails_Time) TextView time;
    @BindView(R.id.data_Card_RecordDetails_Payee) EditText payee;
    @BindView(R.id.data_Card_RecordDetails_Type) Spinner recordType;
    @BindView(R.id.data_Card_RecordDetails_Store) TextView store;
    @BindView(R.id.data_Card_RecordDetails_Account) Spinner account;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_record_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        args = UpdateRecordDetailsArgs.fromBundle(requireArguments());
        ID = args.getRecordID();
        record = args.getRecord();

        bindSimpleSpinner(payType, getStringArrayList(R.array.paymentType));
        bindSimpleSpinner(recordType, getStringArrayList(R.array.recordType));

        type = onLoad(ID == null ? Type.CREATE : Type.UPDATE);

        SetToolbar(
                record == null ?
                    getString(R.string.title_addChange_record) :
                    NumberFormat.getCurrencyInstance(Locale.FRANCE).format(Double.valueOf(record.getAmount())),
                type.equals(Type.UPDATE) ?
                    ToolbarNavIcon.BACK :
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
            description.clearFocus();
            payee.clearFocus();
            if (args.getRecordID() == null) { //To Basic
                save = false;
                onUpdate();
                navigate(UpdateRecordDetailsDirections.updateRecordDetailsToBasic().setRecord(record));
            } else { //Cancel;
                navigateUp();
            }
        });
    }

    /*
     * Hide keyboard if focus is lost.
     */
    @OnFocusChange({R.id.data_Card_RecordDetails_Description, R.id.data_Card_RecordDetails_Payee})
    protected void onFocusChange() {
        if (!description.hasFocus()) description.onEditorAction(EditorInfo.IME_ACTION_DONE);
        if (!payee.hasFocus()) payee.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }


    @Override
    public Type onLoad(Type type) {
        if (type.equals(Type.UPDATE)) {
            firestore.document(ID).get().addOnSuccessListener(data -> {
                record = Objects.requireNonNull(data.toObject(Record.class));
                setData();
            }).addOnFailureListener(f -> showSnackbar(getString(R.string.snackbar_notLoad)));
        } else setData();
        return type;
    }

    private void setData() {
        description.setText(record.getDescription());
        record.getCategoryID().get().addOnSuccessListener(t -> category.setText(Objects.requireNonNull(t.toObject(Category.class)).getName()));
        category.setTag(record.getCategoryID());
        amount.setText(record.getAmount());
        payType.setSelection(record.getPaymentType());
        date.setText(toSimpleDateFormat(record.getDateTime() == null ? new Date(System.currentTimeMillis()) : record.getDateTime().toDate(), DateFormat.DATE));
        date.setTag(record.getDateTime() == null ? new Date(System.currentTimeMillis()) : record.getDateTime().toDate());
        time.setText(toSimpleDateFormat(record.getDateTime() == null ? new Date(System.currentTimeMillis()) : record.getDateTime().toDate(), DateFormat.TIME));
        time.setTag(record.getDateTime() == null ? new Date(System.currentTimeMillis()) : record.getDateTime().toDate());
        payee.setText(record.getPayee());
        recordType.setSelection(record.getType());

        Spinners.Currencies.initializeAndSetDefault(this, currency, record.getCurrencyID());
        Spinners.Accounts.initializeAndSetDefault(this, account, record.getAccountID());

        DocumentReference storeRef = (record.getStoreID() == null ? app.getLastUsedStore() : record.getStoreID());
        storeRef.get().addOnSuccessListener(ct -> {
            Store str = Objects.requireNonNull(ct.toObject(Store.class));
            store.setText(str.getName());
            store.setTag(ct.getReference());
        });
        requireActivity().setTitle(NumberFormat.getCurrencyInstance(Locale.FRANCE).format(Double.valueOf(record.getAmount())));
    }

    @Override
    public void onUpdate() {
        description.clearFocus();
        payee.clearFocus();
        if (amount.getText().equals("0")) {
            showToast(getString(R.string.toast_error_empty));
        } else {
            record.setDescription(reformatText(description.getText()));
            record.setPayee(reformatText(payee.getText()));
            record.setCategoryID((DocumentReference)category.getTag());
            record.setAccountID((DocumentReference)account.getTag());
            record.setCurrencyID((DocumentReference)currency.getTag());
            record.setStoreID(((QueryDocumentSnapshot)store.getTag()).getReference());
            record.setType(recordType.getSelectedItemPosition());
            record.setPaymentType(payType.getSelectedItemPosition());
            record.setAmount(amount.getText().toString());
            record.setDateTime(new Timestamp(new Date(System.currentTimeMillis())));
            record.setLocation(setGeoPoint());

            if (type.equals(Type.CREATE)) {
                record.setUserID(app.getUser());
                if (save) {
                    firestore.collection(Record.COLLECTION).add(record).addOnCompleteListener(c -> {
                        showResultSnackbar(c.isSuccessful(), TAG, Message.CREATED);
                        if (c.isSuccessful()) navigateUp();
                    });
                }
            } else {
                firestore.document(ID).set(record).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.UPDATED);
                    if (c.isSuccessful()) navigateUp();
                });
            }
        }
    }

    @Override
    public void onDelete() {
        description.clearFocus();
        payee.clearFocus();

        record.getAccountID().get().addOnSuccessListener(hasRecord -> {
            BigDecimal accountValue = new BigDecimal(Objects.requireNonNull(hasRecord.toObject(Account.class)).getCurrentValue());

            if (record.getType() == 0) { // +
                accountValue = accountValue.subtract(new BigDecimal(record.getAmount()));
            } else if (record.getType() == 1) { // -
                accountValue = accountValue.add(new BigDecimal(record.getAmount()));
            }

            record.getAccountID().update(Record.AMOUNT, accountValue.toPlainString());
        });

        firestore.document(ID).delete()
                .addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.DELETED);
                    if (c.isSuccessful()) navigateUp();
                });
    }

    private GeoPoint setGeoPoint() {
        LocationManager MT = (LocationManager)requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (MT.isProviderEnabled(LocationManager.GPS_PROVIDER) || MT.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(requireContext());
            client.getLastLocation().addOnSuccessListener(succeeded -> new GeoPoint(succeeded.getLatitude(), succeeded.getLongitude()));
        }
        return null;
    }

    @OnClick(R.id.data_Card_RecordDetails_Amount)
    protected void updateValue() {
        InputValueDialog.getInstance(this);
    }

    @OnClick(R.id.data_Card_RecordDetails_Date)
    protected void updateDate() {
        isDate = true;
        Calendar c = Calendar.getInstance();
        c.setTime((Date)date.getTag());
        DateTimePickerDialog.getInstance(this, c, DateTimePickerDialog.Type.DATE, DateTimePickerDialog.DateOptions.BEFORE_TODAY);
    }

    @OnClick(R.id.data_Card_RecordDetails_Time)
    protected void updateTime() {
        isDate = false;
        Calendar c = Calendar.getInstance();
        c.setTime((Date)time.getTag());
        DateTimePickerDialog.getInstance(this, c, DateTimePickerDialog.Type.TIME, DateTimePickerDialog.DateOptions.BEFORE_TODAY);
    }

    @OnClick(R.id.data_Card_RecordDetails_Category)
    protected void updateCategory() {
        DialogSelectionList.CategoryDialog.getInstance(this, this);
    }

    @OnClick(R.id.data_Card_RecordDetails_Store)
    protected void updateStore() {
        DialogSelectionList.StoreDialog.getInstance(this, this);
    }

    @Override
    public void onFinishValueDialog(String finalValue) {
        String format = NumberFormat.getCurrencyInstance(Locale.FRANCE).format(Double.valueOf(finalValue));
        amount.setText(format);
        Objects.requireNonNull(((MainActivity)requireActivity()).getSupportActionBar()).setTitle(format);
    }

    @Override
    public void onFinishDialog(Calendar calendar) {
        if (isDate) {
            date.setText(toSimpleDateFormat(calendar.getTime(), DateFormat.DATE));
            date.setTag(calendar.getTime());
        } else {
            time.setText(toSimpleDateFormat(calendar.getTime(), DateFormat.TIME));
            time.setTag(calendar.getTime());
        }
    }

    @Override
    public void onSelectCategory(DocumentSnapshot snapshot) {
        category.setTag(snapshot);
        category.setText(Objects.requireNonNull(snapshot.toObject(Category.class)).getName());
    }

    @Override
    public void onSelectStore(DocumentSnapshot snapshot) {
        store.setTag(snapshot);
        store.setText(Objects.requireNonNull(snapshot.toObject(Store.class)).getName());
    }
}

