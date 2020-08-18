package com.umbrella.budgetapp.extensions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.*

sealed class Dialogs {

    class DatePicker(context: Context, listener: OnSelectDate) : Dialogs() {
        private val dialog : DatePickerDialog

        interface OnSelectDate {
            fun dateSelected(timeInMillis: Long)
        }

        init {
            val initDate : Calendar = Calendar.getInstance()
            dialog = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, y, m, d ->
                val temp : Calendar = Calendar.getInstance()
                temp.set(y, m, d)
                listener.dateSelected(temp.timeInMillis)
            }, initDate[Calendar.YEAR], initDate[Calendar.MONTH], initDate[Calendar.DAY_OF_MONTH])
        }

        fun show() = dialog.show()

        fun initialDate(date: Long) {
            val temp = Calendar.getInstance().apply { timeInMillis = date }
            dialog.updateDate(temp[Calendar.YEAR], temp[Calendar.MONTH], temp[Calendar.DAY_OF_MONTH])
        }

        fun before(date: Long) {
            dialog.datePicker.maxDate = date
        }

        fun beforeToday() {
            dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        }

        fun fromToday() {
            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
        }
    }

    class TimePicker(context: Context, listener: OnSelectTime) : Dialogs() {
        private val dialog: TimePickerDialog

        interface OnSelectTime {
            fun timeSelected(timeInMillis: Long)
        }

        init {
            val initTime : Calendar = Calendar.getInstance()
            dialog = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, h, m ->
                val temp : Calendar = Calendar.getInstance()
                temp.apply {
                    set(Calendar.HOUR_OF_DAY, h)
                    set(Calendar.MINUTE, m)
                }
                listener.timeSelected(temp.timeInMillis)
            }, initTime[Calendar.HOUR_OF_DAY], initTime[Calendar.MINUTE], true)
        }

        fun show() = dialog.show()
    }

    class DateTimePicker(context: Context, listener: OnSelectDateTime) : Dialogs() {
        private val dialogDate: DatePicker
        private lateinit var dialogTime: TimePicker

        interface OnSelectDateTime {
            fun dateTimeSelected(timeInMillis: Long)
        }

        init {
            dialogDate = DatePicker(context, object : DatePicker.OnSelectDate {
                override fun dateSelected(timeInMillis: Long) {

                    val date = Calendar.getInstance().apply { setTimeInMillis(timeInMillis) }

                    dialogTime = TimePicker(context, object : TimePicker.OnSelectTime {
                        override fun timeSelected(timeInMillis: Long) {

                            val time = Calendar.getInstance().apply { setTimeInMillis(timeInMillis) }

                            val tempCalendar = Calendar.getInstance().apply {
                                set(
                                    date[Calendar.YEAR],
                                    date[Calendar.MONTH],
                                    date[Calendar.DAY_OF_MONTH],
                                    time[Calendar.HOUR_OF_DAY],
                                    time[Calendar.MINUTE]
                                )
                            }

                            listener.dateTimeSelected(tempCalendar.timeInMillis)
                        }
                    })

                    dialogTime.show()
                }
            })
        }

        fun show() = dialogDate.show()

        fun initialDate(date: Long) {
            dialogDate.initialDate(date)
        }

        fun before(date: Long) {
            dialogDate.before(date)
        }

        fun beforeToday() {
            dialogDate.beforeToday()
        }

        fun fromToday() {
            dialogDate.fromToday()
        }
    }
}