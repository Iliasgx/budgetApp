package com.umbrella.budgetapp.Customs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Objects;

/**
 * Custom dialog for setting the date, time or both.
 *
 * <p>Options:
 * <p>  - DATE
 * <p>  - TIME
 * <p>  - DATE_TIME (default)
 * @see Type;
 */
public class DateTimePickerDialog extends DialogFragment {
    private static Calendar cal;
    private static Type type;
    private static DateOptions option;

    public enum Type {
        DATE,
        TIME,
        DATE_TIME
    }

    public enum DateOptions {
        NO_OPTIONS,
        AFTER_TODAY,
        BEFORE_TODAY
    }

    public static DateTimePickerDialog getInstance(@NonNull Fragment fragment, @Nullable Calendar calendar, DateOptions options) {
        return getInstance(fragment, calendar, Type.DATE_TIME, options);
    }

    public static DateTimePickerDialog getInstance(@NonNull Fragment fragment,@Nullable Calendar calendar, @NonNull Type dialogType, DateOptions options) {
        DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog();
        cal = Calendar.getInstance();
        cal.setTimeInMillis(calendar != null ? calendar.getTimeInMillis() : System.currentTimeMillis());
        type = dialogType;
        option = options;

        dateTimePickerDialog.setTargetFragment(fragment, 300);
        dateTimePickerDialog.show(fragment.getParentFragmentManager(), "DATETIME");
        return dateTimePickerDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (type.equals(Type.TIME)) {
            return new TimePickerDialog(requireContext(), onTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        } else {
            DatePickerDialog dpd = new DatePickerDialog(requireContext(), onDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

            if (option.equals(DateOptions.AFTER_TODAY)) {
                dpd.getDatePicker().setMinDate(System.currentTimeMillis());
            } else if (option.equals(DateOptions.BEFORE_TODAY)) {
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
            dpd.show();
            return dpd;
        }
    }

    public interface setOnFinishListener {
        void onFinishDialog(Calendar calendar);
    }

    /**
     * Send back the Calendar that has been set.
     */
    private void sendResults() {
        setOnFinishListener listener = Objects.requireNonNull((setOnFinishListener)getTargetFragment());
        listener.onFinishDialog(cal);
        dismiss();
    }

    /**
     * When the Time dialog has been finished.
     */
    private TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, hourOfDay, minute) -> {
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        sendResults();
    };

    /**
     * When the Date dialog has been finished.
     */
    private DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, dayOfMonth) -> {
        cal.set(year, month, dayOfMonth);
        sendResults();

        //If DATE_TIME --> Time has to be displayed too.
        if (type.equals(Type.DATE_TIME)) new TimePickerDialog(requireContext(), onTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
    };
}