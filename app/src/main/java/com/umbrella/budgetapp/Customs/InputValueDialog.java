package com.umbrella.budgetapp.Customs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.umbrella.budgetapp.R;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Class for showing the Input dialog for values.
 *
 * <p>Show this class to input a new value and retrieve the new value by
 * implementing the InputValueDialogListener.
 *
 * <p>Use this class as {@link #getInstance(Fragment)}
 *
 * <p>@see InputValueDialogListener
 */
public class InputValueDialog extends DialogFragment {
    private Unbinder unbinder;

    private static final BigDecimal MIN_VALUE = new BigDecimal(0.00);
    private static final BigDecimal MAX_VALUE = new BigDecimal(1000000.00);
    private static final int MAX_AFTER_DOT = 2;

    private Operator operator = Operator.NONE;
    private boolean operatorLastPressed = false;
    private String value = "";
    private BigDecimal fixedValue = MIN_VALUE;

    @BindView(R.id.dialog_Amount_Value)
    TextView valueHolder;

    public static void getInstance(@NonNull Fragment fragment) {
        InputValueDialog dialog = new InputValueDialog();
        dialog.setTargetFragment(fragment, 300);
        dialog.show(fragment.getParentFragmentManager(), "VALUE");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_amount, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        requireView().requireViewById(R.id.dialog_Amount_Cancel).setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.dialog_Amount_One,
            R.id.dialog_Amount_Two,
            R.id.dialog_Amount_Three,
            R.id.dialog_Amount_Four,
            R.id.dialog_Amount_Five,
            R.id.dialog_Amount_Six,
            R.id.dialog_Amount_Seven,
            R.id.dialog_Amount_Eight,
            R.id.dialog_Amount_Nine,
            R.id.dialog_Amount_Zero})
    public void onNumpadClick(Button btn) {
        BigDecimal bd = new BigDecimal(value.length() == 0 ? "0" : (value.endsWith(".") ? value.substring(0, value.length() - 1) : value));

        //If smaller than MAX_VALUE
        if (bd.compareTo(MAX_VALUE) < 0) {
            //If dot is not present OR if dot is present && no more chars after dot than MAX_AFTER_DOT
            if (!value.contains(".") || value.substring(value.indexOf("."), value.length() - 1).length() < MAX_AFTER_DOT) {
                //add number behind value
                value = value.concat(String.valueOf(btn.getTag()));
                operatorLastPressed = false;

                showValue();
            } else showToast("Max number after dot reached.");
        } else showToast("Max value reached.");
    }

    @OnClick({R.id.dialog_Amount_Add,
            R.id.dialog_Amount_Subtract,
            R.id.dialog_Amount_Multiply,
            R.id.dialog_Amount_Divide})
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

            showValue();
            value = "";
            operator = Operator.value(String.valueOf(btn.getTag()));
            operatorLastPressed = true;
        } else {
            showToast("Value is 0");
        }
        operatorLastPressed = true;
    }

    @OnClick(R.id.dialog_Amount_Dot)
    public void onDotClick() {
        //If not containing dot already
        if (!value.contains(".")) {
            //set 0 before dot if value is ""
            if (value.length() == 0) value = value.concat("0");
            value = value.concat(".");
            showValue();
        }
    }

    @OnClick(R.id.dialog_Amount_Backspace)
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
            showValue();
        }
    }

    @OnClick(R.id.dialog_Amount_Equals)
    public void onEqualClick() {
        BigDecimal bd = new BigDecimal(value.length() == 0 ? "0" : (value.endsWith(".") ? value.substring(0, value.length() - 1) : value));

        if (bd.compareTo(fixedValue) != 0) {
            Button d = new Button(requireContext());
            d.setTag(-1);

            if (value.equals("")) value = "0";
            onOperatorClick(d);
        }

        value = String.valueOf(fixedValue);
        showValue();

        operator = Operator.NONE;
        operatorLastPressed = false;
    }

    private void showToast(CharSequence text) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void showValue() {
       valueHolder.setText(value);
    }

    public interface InputValueDialogListener {
        void onFinishValueDialog(String finalValue);
    }

    @OnClick(R.id.dialog_Amount_Ok)
    public void sendBackResult() {
        onEqualClick();

        if (fixedValue.compareTo(MIN_VALUE) >= 0) {
            InputValueDialogListener listener = Objects.requireNonNull((InputValueDialogListener)getTargetFragment());
            listener.onFinishValueDialog(NumberFormat.getNumberInstance(Locale.FRANCE).format(fixedValue));
            dismiss();
        } else {
            showToast("Value can't be negative.");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
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

        public static Operator value(String tag){
            for (Operator e: values()) {
                if (e.tag == Integer.valueOf(tag)) {
                    return e;
                }
            }
            return null;
        }
    }
}

