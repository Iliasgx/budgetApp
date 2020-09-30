package com.umbrella.budgetapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.extensions.setNavigationResult
import com.umbrella.budgetapp.ui.dialogs.FilterDialog.Filter.FilterOption.*
import kotlinx.android.synthetic.main.dialog_filter.*
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.temporal.WeekFields
import java.util.*

class FilterDialog : DialogFragment() {

    private val args by navArgs<FilterDialogArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog_Filter_SelectPeriod.setSelection(if (args.currentFilter == 0) Memory.homeCashFlowFilter else Memory.homeRecordsFilter)

        dialog_Filter_Ok.setOnClickListener { saveData() }
        dialog_Filter_Cancel.setOnClickListener { dialog?.cancel() }
    }

    private fun saveData() {
        setNavigationResult("filter@${args.currentFilter}", Companion.getFilter(dialog_Filter_SelectPeriod.selectedItemPosition))
        dismiss()
    }

    public class Filter(private val option: FilterOption) {

        fun current() : Triple<FilterOption, Long, Long> {
            return Triple(option, when(option) {
                TODAY ->          now().startToMilli()
                WEEK_CURRENT ->   now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).startToMilli()
                MONTH_CURRENT ->  now().withDayOfMonth(1).startToMilli()
                YEAR_CURRENT ->   now().minusYears(1L).startToMilli()
                WEEK ->           now().minusWeeks(1L).startToMilli()
                MONTH ->          now().minusMonths(1L).startToMilli()
                SEMESTER ->       now().minusMonths(5L).startToMilli()
                YEAR ->           now().minusYears(1L).startToMilli()
                ANYTIME ->        0L // Every possible Long
            }, now().toInstant(ZoneOffset.UTC).toEpochMilli())
        }

        fun previous() : Triple<FilterOption, Long, Long> {
            return Triple(option, when(option) {
                TODAY ->          now().minusDays(1L).startToMilli()
                WEEK_CURRENT ->   now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).minusWeeks(1L).startToMilli()
                MONTH_CURRENT ->  now().withDayOfMonth(1).minusMonths(1L).startToMilli()
                YEAR_CURRENT ->   now().minusYears(2L).startToMilli()
                WEEK ->           now().minusWeeks(2L).startToMilli()
                MONTH ->          now().minusMonths(2L).startToMilli()
                SEMESTER ->       now().minusMonths(11L).startToMilli()
                YEAR ->           now().minusYears(2L).startToMilli()
                ANYTIME ->        0L // Every possible Long
            }, when(option) {
                TODAY ->          now().startToMilli()
                WEEK_CURRENT ->   now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).startToMilli()
                MONTH_CURRENT ->  now().withDayOfMonth(1).startToMilli()
                YEAR_CURRENT ->   now().minusYears(1L).startToMilli()
                WEEK ->           now().minusWeeks(1L).startToMilli()
                MONTH ->          now().minusMonths(1L).startToMilli()
                SEMESTER ->       now().minusMonths(5L).startToMilli()
                YEAR ->           now().minusYears(1L).startToMilli()
                ANYTIME ->        now().toInstant(ZoneOffset.UTC).toEpochMilli() + 1000L
            } - 1000L)
        }

        enum class FilterOption {
            YEAR_CURRENT, //Default
            TODAY,
            WEEK_CURRENT,
            MONTH_CURRENT,
            WEEK,
            MONTH,
            SEMESTER,
            YEAR,
            ANYTIME;

            companion object { fun getFilter(value: Int) = values()[value] }
        }

        private fun LocalDateTime.startToMilli() : Long {
            return LocalDateTime.of(toLocalDate(), LocalTime.MIN).toInstant(ZoneOffset.UTC).toEpochMilli()
        }
    }
}