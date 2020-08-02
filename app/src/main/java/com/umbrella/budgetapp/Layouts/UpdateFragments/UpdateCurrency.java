package com.umbrella.budgetapp.Layouts.UpdateFragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Collections.Country;
import com.umbrella.budgetapp.Database.Collections.Currency;
import com.umbrella.budgetapp.Database.Defaults.DefaultCountries;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;


public class UpdateCurrency extends BaseFragment implements UpdateListeners {
    private static final String TAG = "Currency";
    private String ID;
    private Type type;
    private Currency currency;
    private Country currentCountry;

    private List<Country> countries;

    @BindView(R.id.data_Card_Currency_Name) Spinner name;
    @BindView(R.id.data_Card_Currency_Rate) EditText rate;
    @BindView(R.id.data_Card_Currency_InverseRate) EditText inverseRate;
    @BindView(R.id.data_Card_Currency_Code) EditText code;
    @BindView(R.id.data_Card_Currency_Symbol) EditText symbol;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_currency, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ID = UpdateCurrencyArgs.fromBundle(requireArguments()).getCurrencyID();

        Log.d("_Test", "[UpdateCurrency] onViewCreated() ID is: " + ID);

        countries = new DefaultCountries().getDefaultCountries(requireActivity());

        List<String> names = new LinkedList<>();
        countries.forEach(c -> names.add(c.getName()));
        bindSimpleSpinner(name, names);

        type = onLoad(ID == null ? Type.CREATE : Type.UPDATE);

        SetToolbar(getString(type.equals(Type.UPDATE) ?
                R.string.title_edit_currency :
                R.string.title_add_currency),
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
            rate.clearFocus();
            inverseRate.clearFocus();
            navigateUp();
        });
    }

    /*
     * Hide keyboard if focus is lost.
     */
    @OnFocusChange({R.id.data_Card_Currency_Rate, R.id.data_Card_Currency_InverseRate})
    protected void onFocusChange() {
        Log.d("_Test", "[UpdateCurrency] onFocusChange() ");
        if (!rate.hasFocus()) rate.onEditorAction(EditorInfo.IME_ACTION_DONE);
        if (!inverseRate.hasFocus()) inverseRate.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public Type onLoad(Type type) {
        Log.d("_Test", "[UpdateCurrency] onLoad() type is: " + type.name());

        if (type.equals(Type.UPDATE)) {
            firestore.document(ID).get().addOnSuccessListener(data -> {
                currency = Objects.requireNonNull(data.toObject(Currency.class));

                currentCountry = countries.stream().filter(c -> c.getCode().equals(currency.getCountryID())).collect(Collectors.toList()).get(0);

                name.setClickable(false);
                name.setSelection(countries.indexOf(currentCountry));
                rate.setText(currency.getUsedRate());
                inverseRate.setText(new BigDecimal(1).divide(new BigDecimal(currency.getUsedRate()), 5, RoundingMode.HALF_UP).toPlainString());
                code.setText(currentCountry.getCode());
                symbol.setText(currentCountry.getSymbol());
            }).addOnFailureListener(f -> showSnackbar(getString(R.string.snackbar_notLoad)));
        } else currency = new Currency();
        return type;
    }

    @Override
    public void onUpdate() {
        rate.clearFocus();
        inverseRate.clearFocus();
        if (reformatText(rate.getText()).isEmpty() | reformatText(inverseRate.getText()).isEmpty() | (new BigDecimal(rate.getText().toString()).signum() != 1) | (new BigDecimal(rate.getText().toString()).signum() != 1)) {
            showToast(getString(R.string.toast_error_empty));
        } else {
            Log.d("_Test", "[UpdateCurrency] onUpdate() ");
            currency.setCountryID(code.getText().toString());
            currency.setUsedRate(rate.getText().toString());

            if (type.equals(Type.CREATE)) {
                app.getUserDocument().collection(Currency.COLLECTION).get().addOnSuccessListener(find -> {
                    currency.setPosition(find.size());
                    app.getUserDocument().collection(Currency.COLLECTION).add(currency).addOnCompleteListener(c -> {
                        showResultSnackbar(c.isSuccessful(), TAG, Message.CREATED);
                        if (c.isSuccessful()) navigateUp();
                    });
                });
            } else {
                firestore.document(ID).set(currency).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.UPDATED);
                    if (c.isSuccessful()) navigateUp();
                });
            }
        }
    }

    @Override
    public void onDelete() {
        Log.d("_Test", "[UpdateCurrency] onDelete() ");
        rate.clearFocus();
        inverseRate.clearFocus();
        firestore.document(ID).delete()
                .addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.DELETED);
                    if (c.isSuccessful()) navigateUp();
                });
    }

    @OnTextChanged(R.id.data_Card_Currency_Rate)
    public void textChangedRate(CharSequence text) {
        Log.d("_Test", "[UpdateCurrency] textChangedRate() ");
        if (rate.hasFocus()) inverseRate.setText(changeRates(text));
    }

    @OnTextChanged(R.id.data_Card_Currency_InverseRate)
    public void textChangedInverseRate(CharSequence text) {
        Log.d("_Test", "[UpdateCurrency] textChangedInverseRate() ");
        if (inverseRate.hasFocus()) rate.setText(changeRates(text));
    }

    private String changeRates(CharSequence text) {
        Log.d("_Test", "[UpdateCurrency] changeRates() ");

        DecimalFormat df = new DecimalFormat(new BigDecimal(1)
                .divide(new BigDecimal(text.toString().endsWith(".")
                        ? text.toString().substring(0, text.length() - 1)
                        : text.toString()),
                        5, RoundingMode.HALF_UP).toPlainString());

        df.setMaximumFractionDigits(5);
        return df.format("#0.#####");
    }

    @OnItemSelected(R.id.data_Card_Currency_Name)
    public void onCountrySelected(int position) {
        Log.d("_Test", "[UpdateCurrency] onCountrySelected() position is: " + position);
        rate.setText(countries.get(position).getDefaultRate());
        inverseRate.setText(changeRates(rate.getText()));
        code.setText(countries.get(position).getCode());
        symbol.setText(countries.get(position).getSymbol());
    }
}

