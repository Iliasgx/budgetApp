package com.umbrella.budgetapp.extensions

import java.text.SimpleDateFormat
import java.util.*

class DateTimeFormatter {

    fun dateFormat(value: Long, escapeChar: Char = '-') : String
            = SimpleDateFormat("dd${escapeChar}MM${escapeChar}yyyy", Locale.getDefault()).format(value)

    fun dateTimeFormat(value: Long, escapeChar: Char = '-') : String
            = SimpleDateFormat("dd${escapeChar}MM${escapeChar}yyyy hh:mm", Locale.getDefault()).format(value)

    fun timeFormat(value: Long) : String
            = SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(value)

}