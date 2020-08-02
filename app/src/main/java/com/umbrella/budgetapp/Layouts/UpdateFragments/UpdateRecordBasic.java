package com.umbrella.budgetapp.Layouts.UpdateFragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Customs.DialogSelectionList;
import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Collections.Account;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Currency;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Database.Collections.Template;
import com.umbrella.budgetapp.Database.Defaults.DefaultCountries;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.OnClick;

public class UpdateRecordBasic extends BaseFragment implements UpdateListeners, DialogSelectionList.CategoryDialog.OnCategoryItemSelected {
    private static final String TAG = "Record";
    private Record record = null;

    //Calculator declarations
    private final BigDecimal MIN_VALUE = new BigDecimal(0.00);
    private final BigDecimal MAX_VALUE = new BigDecimal(1000000.00);

    private Operator operator = Operator.NONE;
    private boolean operatorLastPressed = false;
    private String value = "";
    private BigDecimal fixedValue = MIN_VALUE;

    private transient boolean save = true;

    @BindView(R.id.data_Card_RecordBasic_Title1) RadioButton first;
    @BindView(R.id.data_Card_RecordBasic_Title2) RadioButton second;
    @BindView(R.id.data_Card_RecordBasic_Title3) RadioButton third;
    @BindView(R.id.data_Card_RecordBasic_Amount) TextView amount;
    @BindView(R.id.data_Card_RecordBasic_Currency) TextView currency;
    @BindView(R.id.data_Card_RecordBasic_Account) TextView account;
    @BindView(R.id.data_Card_RecordBasic_Category) TextView category;
    @BindView(R.id.data_Card_RecordBasic_More) Button template;
    @BindView(R.id.data_Card_RecordBasic_Mark) TextView mark;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_record_basic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        record = UpdateRecordBasicArgs.fromBundle(requireArguments()).getRecord();

        Log.d("_Test", "[UpdateRecordBasic] onViewCreated() record?null: " + (record != null));

        onLoad(record == null ? Type.CREATE : Type.UPDATE);

        SetToolbar(getString(R.string.title_add_account), ToolbarNavIcon.CANCEL, R.menu.save);
        setOnMenuItemClickListener(v -> {
            onUpdate();
            return true;
        });
        setNavigationOnClickListener(v -> navigateUp());
    }

    @Override
    public Type onLoad(Type type) {
        Log.d("_Test", "[UpdateRecordBasic] onLoad() type is: " + type.name());

        if (type.equals(Type.UPDATE)) {
            switch (record.getType()) {
                case 0:
                    first.toggle();
                    mark.setText("+");
                    break;
                case 1:
                    second.toggle();
                    mark.setText("-");
                    break;
                case 2:
                    third.toggle();
                    mark.setText("±");
                    break;
            }
            amount.setText(record.getAmount());

            record.getCurrencyID().get().addOnSuccessListener(dt ->
                    currency.setText(new DefaultCountries().getDefaultCountries(requireActivity())
                            .stream()
                            .filter(ftr -> ftr.getCode().equals(Objects.requireNonNull(dt.toObject(Currency.class)).getCountryID()))
                            .collect(Collectors.toList())
                            .get(0)
                            .getCode()));
            currency.setTag(record.getCurrencyID());

            record.getCategoryID().get().addOnSuccessListener(dt -> {
                category.setText(Objects.requireNonNull(dt.toObject(Category.class)).getName());
                category.setTag(record.getCategoryID());
            });

            record.getAccountID().get().addOnSuccessListener(c -> account.setText(Objects.requireNonNull(c.toObject(Account.class)).getName()));
            account.setTag(record.getAccountID());
        } else {
            record = new Record();
            first.toggle();

            app.getLastUsedCategory().get().addOnSuccessListener(lst -> {
                category.setTag(lst);
                category.setText(Objects.requireNonNull(lst.toObject(Category.class)).getName());
            });

            app.getSelectedAccounts().get(0).get().addOnSuccessListener(c -> {
                account.setTag(c);
                account.setText(Objects.requireNonNull(c.toObject(Account.class)).getName());
            });

            app.getUserDocument().collection(Currency.COLLECTION).orderBy(Currency.POSITION, Query.Direction.ASCENDING).limit(1).get().addOnSuccessListener(ct -> {
                currency.setTag(ct.getDocuments().get(0));
                currency.setText(new DefaultCountries().getDefaultCountries(requireActivity())
                        .stream()
                        .filter(ftr -> ftr.getCode().equals(Objects.requireNonNull(ct.getDocuments().get(0).toObject(Currency.class)).getCountryID()))
                        .collect(Collectors.toList())
                        .get(0)
                        .getCode());
            });
        }
        return type;
    }

    @Override
    public void onUpdate() {
        if (new BigDecimal(amount.getText().toString()).equals(new BigDecimal(0))) {
            showToast(getString(R.string.record_value_error));
        } else {
            Log.d("_Test", "[UpdateRecordBasic] onUpdate() ");
            if (first.isChecked()) record.setType(0);
            if (second.isChecked()) record.setType(1);
            if (third.isChecked()) record.setType(2);

            record.setAmount(amount.getText().toString());
            record.setCurrencyID((DocumentReference)currency.getTag());
            record.setAccountID((DocumentReference)account.getTag());
            record.setCategoryID((DocumentReference)category.getTag());

            record.setUserID(app.getUser());
            record.setDateTime(new Timestamp(new Date(System.currentTimeMillis())));

            record.setLocation(setGeoPoint());

            if (save) {
                firestore.collection(Record.COLLECTION).add(record).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.CREATED);
                    if (c.isSuccessful()) navigateUp();
                });
            }
        }
    }

    @Override
    public void onDelete() {
        //Can't delete record here
    }

    @OnClick(R.id.data_Card_RecordBasic_Category)
    protected void updateCategory() {
        Log.d("_Test", "[UpdateRecordBasic] updateCategory() ");
        DialogSelectionList.CategoryDialog.getInstance(this, this);
    }

    @Override
    public void onSelectCategory(DocumentSnapshot snapshot) {
        Log.d("_Test", "[UpdateRecordBasic] onSelectCategory() ");
        category.setTag(snapshot);
        category.setText(Objects.requireNonNull(snapshot.toObject(Category.class)).getName());
    }

    private GeoPoint setGeoPoint() {
        Log.d("_Test", "[UpdateRecordBasic] setGeoPoint() ");
        LocationManager MT = (LocationManager)requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (MT.isProviderEnabled(LocationManager.GPS_PROVIDER) || MT.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(requireContext());
            client.getLastLocation().addOnSuccessListener(succeeded -> new GeoPoint(succeeded.getLatitude(), succeeded.getLongitude()));
        }
        return null;
    }

    @OnClick(R.id.data_Card_RecordBasic_Details)
    protected void onNavigateToDetailScreen() {
        Log.d("_Test", "[UpdateRecordBasic] onNavigateToDetailScreen() ");
        onEqualClick();

        save = false;
        onUpdate();
        navigate(UpdateRecordBasicDirections.updateRecordBasicToDetails().setRecord(record));
    }

    @OnClick({R.id.data_Card_RecordBasic_Account, R.id.data_Card_RecordBasic_Currency, R.id.data_Card_RecordBasic_More})
    protected void showExtraDialog(View view) {
        Log.d("_Test", "[UpdateRecordBasic] showExtraDialog() ");
        SelectionDialogs.RV item;

        if (view.equals(account)) {
            item = SelectionDialogs.RV.ACCOUNT;
        } else if (view.equals(currency)) {
            item = SelectionDialogs.RV.CURRENCY;
        } else {
            item = SelectionDialogs.RV.TEMPLATE;
        }

        SelectionDialogs.getInstance(this, item, (itm, snapshot) -> {
            switch (item) {
                case ACCOUNT:
                    account.setTag(snapshot);
                    account.setText(Objects.requireNonNull(snapshot.toObject(Account.class)).getName());
                    break;
                case TEMPLATE:
                    template.setTag(snapshot);
                    template.setText(Objects.requireNonNull(snapshot.toObject(Template.class)).getName());
                    onUpdateWithTemplateData(Objects.requireNonNull(snapshot.toObject(Template.class)));
                    break;
                case CURRENCY:
                    currency.setTag(snapshot);
                    currency.setText(new DefaultCountries().getDefaultCountries(requireActivity()).stream().filter(ftr -> ftr.getCode().equals(Objects.requireNonNull(snapshot.toObject(Currency.class)).getCountryID())).collect(Collectors.toList()).get(0).getCode());
                    break;
            }
        });
    }

    //Update fields when chosen a template.
    private void onUpdateWithTemplateData(Template template) {
        Log.d("_Test", "[UpdateRecordBasic] onUpdateWithTemplateData() ");
        switch (template.getType()) {
            case 0:
                first.toggle();
                mark.setText("+");
                break;
            case 1:
                second.toggle();
                mark.setText("-");
                break;
            case 2:
                third.toggle();
                mark.setText("±");
                break;
        }
        amount.setText(template.getAmount());

        currency.setTag(template.getCurrencyID());
        template.getCurrencyID().get().addOnSuccessListener(dt ->
                currency.setText(new DefaultCountries().getDefaultCountries(requireActivity())
                        .stream()
                        .filter(ftr -> ftr.getCode().equals(Objects.requireNonNull(dt.toObject(Currency.class)).getCountryID()))
                        .collect(Collectors.toList())
                        .get(0)
                        .getCode()));

        template.getAccountID().get().addOnSuccessListener(c -> account.setText(Objects.requireNonNull(c.toObject(Account.class)).getName()));
        account.setTag(template.getAccountID());

        template.getCategoryID().get().addOnSuccessListener(c -> category.setText(Objects.requireNonNull(c.toObject(Category.class)).getName()));
        category.setTag(template.getCategoryID());
    }

    /*
    Calculator functions
     */
    @OnClick({R.id.data_Card_RecordBasic_One,
            R.id.data_Card_RecordBasic_Two,
            R.id.data_Card_RecordBasic_Three,
            R.id.data_Card_RecordBasic_Four,
            R.id.data_Card_RecordBasic_Five,
            R.id.data_Card_RecordBasic_Six,
            R.id.data_Card_RecordBasic_Seven,
            R.id.data_Card_RecordBasic_Eight,
            R.id.data_Card_RecordBasic_Nine,
            R.id.data_Card_RecordBasic_Zero})
    public void onNumpadClick(Button btn) {
        BigDecimal bd = new BigDecimal(value.length() == 0 ? "0" : (value.endsWith(".") ? value.substring(0, value.length() - 1) : value));

        //If smaller than MAX_VALUE
        if (bd.compareTo(MAX_VALUE) < 0) {
            //If dot is not present OR if dot is present && no more chars after dot than MAX_AFTER_DOT
            if (!value.contains(".") || value.substring(value.indexOf("."), value.length() - 1).length() < 2) {
                //add number behind value
                value = value.concat(String.valueOf(btn.getTag()));
                operatorLastPressed = false;

                amount.setText(value);
            } else showToast("Max number after dot reached.");
        } else showToast("Max value reached.");
    }

    @OnClick({R.id.data_Card_RecordBasic_Add,
            R.id.data_Card_RecordBasic_Subtract,
            R.id.data_Card_RecordBasic_Multiply,
            R.id.data_Card_RecordBasic_Divide})
    public void onOperatorClick(Button btn) {
        //If value is not ""
        if (value.length() != 0) {
            BigDecimal bd = new BigDecimal(value.endsWith(".") ? value.substring(0, value.length() - 1) : value);

            switch (operator) {
                case NONE:
                    fixedValue = bd;
                    break;
                case ADD:
                    fixedValue = fixedValue.add(bd);
                    break;
                case SUBTRACT:
                    fixedValue = fixedValue.subtract(bd);
                    break;
                case MULTIPLY:
                    fixedValue = fixedValue.multiply(bd, MathContext.DECIMAL32);
                    break;
                case DIVIDE:
                    //Can not divide by 0
                    if (value.matches("0" + "|" + "0." + "|" + "0.0" + "|" + "0.00")) {
                        showToast("Can not divide by 0.");
                    } else {
                        fixedValue = fixedValue.divide(bd, 2, RoundingMode.UP);
                    }
                    break;
            }
            //If bigger than MAX_VALUE
            if (fixedValue.compareTo(MAX_VALUE) > 0) {
                fixedValue = MAX_VALUE;
                showToast("Cannot exceed max value.");
            }

            //Set value temporarily as the fixedValue to show this value
            value = String.valueOf(fixedValue);

            amount.setText(value);
            value = "";
            operator = Operator.value(String.valueOf(btn.getTag()));
            operatorLastPressed = true;
        } else {
            showToast("Value is 0.");
        }
        operatorLastPressed = true;
    }

    @OnClick(R.id.data_Card_RecordBasic_Dot)
    public void onDotClick() {
        //If not containing dot already
        if (!value.contains(".")) {
            //set 0 before dot if value is ""
            if (value.length() == 0) value = value.concat("0");
            value = value.concat(".");
            amount.setText(value);
        }
    }

    @OnClick(R.id.data_Card_RecordBasic_Backspace)
    public void onBackspaceClick() {
        //If value not ""
        if (value.length() != 0) {
            //Remove last Char
            value = value.substring(0, value.length() - 1);
            //If operator was pressed right before backspace, then disable operator
            if (operatorLastPressed) {
                operatorLastPressed = false;
                operator = Operator.NONE;
            }
            amount.setText(value);
        }
    }

    @OnClick(R.id.data_Card_RecordBasic_Equals)
    public void onEqualClick() {
        BigDecimal bd = new BigDecimal(value.length() == 0 ? "0" : (value.endsWith(".") ? value.substring(0, value.length() - 1) : value));

        if (bd.compareTo(fixedValue) != 0) {
            Button d = new Button(requireContext());
            d.setTag(-1);

            if (value.equals("")) value = "0";
            onOperatorClick(d);
        }

        value = String.valueOf(fixedValue);
        amount.setText(value);

        operator = Operator.NONE;
        operatorLastPressed = false;
    }

    private enum Operator {
        NONE     (-1),
        ADD      (12),
        SUBTRACT (13),
        MULTIPLY (14),
        DIVIDE   (15);

        private int tag;

        Operator(int tag) {
            this.tag = tag;
        }

        public static Operator value(String tag) {
            for (Operator e : values()) {
                if (e.tag == Integer.valueOf(tag)) {
                    return e;
                }
            }
            return null;
        }
    }

    public static class SelectionDialogs extends DialogFragment {
        private static OnDialogItemSelected listener;

        private DaoAccountList accountAdapter;
        private DaoTemplateList templateAdapter;
        private DaoCurrencyList currencyAdapter;

        private static TypedArray icons;
        private static List<Integer> colors;

        private static RV itemRV;

        public enum RV {
            ACCOUNT,
            TEMPLATE,
            CURRENCY
        }

        public static void getInstance(@NonNull BaseFragment fragment, RV item, OnDialogItemSelected itemListener) {
            SelectionDialogs dialog = new SelectionDialogs();
            dialog.setTargetFragment(fragment, 300);
            dialog.show(fragment.getParentFragmentManager(), "VALUE");

            listener = itemListener;
            itemRV = item;

            colors = new LinkedList<>();
            for (int i : fragment.getResources().getIntArray(R.array.colors)) colors.add(i);
            icons = fragment.getResources().obtainTypedArray(R.array.icons);
            Log.d("_Test", "[SelectionDialogs] getInstance() ");
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_recycler_view, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            accountAdapter = new DaoAccountList(new FirestoreRecyclerOptions.Builder<Account>().setQuery(app.getUserDocument().collection(Account.COLLECTION), Account.class).build());
            templateAdapter = new DaoTemplateList(new FirestoreRecyclerOptions.Builder<Template>().setQuery(app.getUserDocument().collection(Template.COLLECTION), Template.class).build());
            currencyAdapter = new DaoCurrencyList(new FirestoreRecyclerOptions.Builder<Currency>().setQuery(app.getUserDocument().collection(Currency.COLLECTION), Currency.class).build());

            RecyclerView rec = view.requireViewById(R.id.fragmentRecyclerView);

            Log.d("_Test", "[SelectionDialogs] onViewCreated() ");

            rec.setHasFixedSize(false);
            rec.setLayoutManager(new LinearLayoutManager(requireActivity()));

            switch (itemRV) {
                case ACCOUNT:
                    rec.setAdapter(accountAdapter);

                    accountAdapter.setOnItemClickListener((which, pos) -> {
                        listener.onSelect(itemRV, which);
                        dismiss();
                    });
                    break;
                case TEMPLATE:
                    rec.setAdapter(templateAdapter);

                    templateAdapter.setOnItemClickListener((which, pos) -> {
                        listener.onSelect(itemRV, which);
                        dismiss();
                    });
                    break;
                case CURRENCY:
                    rec.setAdapter(currencyAdapter);

                    currencyAdapter.setOnItemClickListener((which, pos) -> {
                        listener.onSelect(itemRV, which);
                        dismiss();
                    });
                    break;
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            accountAdapter.startListening();
            templateAdapter.startListening();
            currencyAdapter.startListening();
            Log.d("_Test", "[SelectionDialogs] onStart() ");
        }

        @Override
        public void onStop() {
            super.onStop();
            accountAdapter.stopListening();
            templateAdapter.stopListening();
            currencyAdapter.stopListening();
            Log.d("_Test", "[SelectionDialogs] onStop() ");
        }

        public interface OnDialogItemSelected {
            void onSelect(RV item, DocumentSnapshot snapshot);
        }

        private class DaoAccountList extends CloudRecyclerAdapter<Account, ModelHolder> {
            private OnItemClickListener listener;

            DaoAccountList(@NonNull FirestoreRecyclerOptions<Account> options) {
                super(options);
                Log.d("_Test", "[DaoAccountList] DaoAccountList() ");
            }

            @NonNull
            @Override
            public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_basic, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull Account account) {
                holder.Name.setText(account.getName());
                holder.Img.setImageResource(R.drawable.savings);
                holder.Img.setColorFilter(colors.get(account.getColor()));

                holder.itemView.setOnClickListener(v -> listener.onItemClick(getSnapshots().getSnapshot(position), position));
            }

            @Override
            public void setOnItemClickListener(OnItemClickListener listener) {
                this.listener = listener;
            }
        }

        private class DaoTemplateList extends CloudRecyclerAdapter<Template, ModelHolder> {
            private OnItemClickListener listener;

            DaoTemplateList(@NonNull FirestoreRecyclerOptions<Template> options) {
                super(options);
                Log.d("_Test", "[DaoTemplateList] DaoTemplateList() ");
            }

            @NonNull
            @Override
            public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_basic, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull Template template) {
                holder.Name.setText(template.getName());

                template.getCategoryID().get().addOnSuccessListener(data -> {
                   Category category = Objects.requireNonNull(data.toObject(Category.class));

                   holder.Img.setImageResource(icons.getResourceId(category.getIcon(), -1));
                   holder.Img.setColorFilter(colors.get(category.getColor()));
                });

                holder.itemView.setOnClickListener(v -> listener.onItemClick(getSnapshots().getSnapshot(position), position));
            }

            @Override
            public void setOnItemClickListener(OnItemClickListener listener) {
                this.listener = listener;
            }
        }

        private class DaoCurrencyList extends CloudRecyclerAdapter<Currency, ModelHolder> {
            private OnItemClickListener listener;

            DaoCurrencyList(@NonNull FirestoreRecyclerOptions<Currency> options) {
                super(options);
                Log.d("_Test", "[DaoCurrencyList] DaoCurrencyList() ");
            }

            @NonNull
            @Override
            public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_basic, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull Currency currency) {
                holder.Name.setText(new DefaultCountries().getDefaultCountries(requireActivity()).stream().filter(ftr -> ftr.getCode().equals(Objects.requireNonNull(currency.getCountryID()))).collect(Collectors.toList()).get(0).getCode());
                holder.Img.setImageResource(R.drawable.saving_account);
                holder.Img.setColorFilter(colors.get(3));

                holder.itemView.setOnClickListener(v -> listener.onItemClick(getSnapshots().getSnapshot(position), position));
            }

            @Override
            public void setOnItemClickListener(OnItemClickListener listener) {
                this.listener = listener;
            }
        }

        static class ModelHolder extends RecyclerView.ViewHolder {
            ImageView Img;
            TextView Name;

            ModelHolder(final View itemView) {
                super(itemView);
                Img = itemView.findViewById(R.id.list_Basic_Img);
                Name = itemView.findViewById(R.id.list_Basic_Name);
            }
        }
    }
}

