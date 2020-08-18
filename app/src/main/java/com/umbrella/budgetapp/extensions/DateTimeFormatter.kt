package com.umbrella.budgetapp.extensions

import java.text.SimpleDateFormat
import java.util.*

class DateTimeFormatter {

    fun dateFormat(value: Long, escapeChar: Char = '-') : String
            = SimpleDateFormat("DD${escapeChar}MM${escapeChar}YYYY", Locale.getDefault()).format(value)

    fun dateTimeFormat(value: Long, escapeChar: Char = '-') : String
            = SimpleDateFormat("DD${escapeChar}MM${escapeChar}YYYY hh:mm", Locale.getDefault()).format(value)

    fun timeFormat(value: Long) : String
            = SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(value)

}