package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.umbrella.budgetapp.database.typeconverters.FrequencyTypeConverter
import com.umbrella.budgetapp.database.typeconverters.PayTypeTypeConverter
import com.umbrella.budgetapp.enums.PayType
import java.math.BigDecimal

@Entity(tableName = "planned_payments")
@TypeConverters(FrequencyTypeConverter::class, PayTypeTypeConverter::class)
data class PlannedPayment(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "planned_payment_id")
        val id: Long? = null,

        @ColumnInfo(name = "planned_payment_name")
        var name: String? = "",

        @ColumnInfo(name = "planned_payment_payee")
        var payee: String? = "",

        @ColumnInfo(name = "planned_payment_note")
        var note: String? = "",

        @ColumnInfo(name = "planned_payment_account_ref")
        var accountRef: Long? = 0,

        @ColumnInfo(name = "planned_payment_category_ref")
        var categoryRef: Long? = 0,

        @ColumnInfo(name = "planned_payment_currency_ref")
        var currencyRef: Long? = 0,

        @ColumnInfo(name = "planned_payment_start_date")
        var startDate: Long? = 0,

        @ColumnInfo(name = "planned_payment_frequency")
        var frequency: Map<String, String>? = emptyMap(),

        @ColumnInfo(name = "planned_payment_type")
        var type: PayType? = PayType.INCOME,

        @ColumnInfo(name = "planned_payment_payment_type")
        var paymentType: Int? = 0,

        @ColumnInfo(name = "planned_payment_reminder_options")
        var reminderOptions: Int? = 0,

        @ColumnInfo(name = "planned_payment_amount")
        var amount: BigDecimal? = BigDecimal.ZERO
) {
    class FrequencyBuilder {
        private var repeating: Int? = 0
        private var period: Int? = 0
        private var ending: Int? = 0
        private var endingUntil: Long? = 0
        private var endingEvents: Int? = 0
        private var days: String? = ""
        private var recurrentMonth: Int? = 0

        fun setDaily(Period: Int, Ending: Int, EndingUntil: Long?, EndingEvents: Int): Map<String, String?> {
            repeating = 1
            this.period = Period
            this.ending = Ending
            this.endingUntil = EndingUntil
            this.endingEvents = EndingEvents
            days = "0000000"
            recurrentMonth = 0
            return setParams()
        }

        fun setWeekly(Period: Int, Ending: Int, EndingUntil: Long?, EndingEvents: Int, Days: String?): Map<String, String?> {
            repeating = 2
            this.period = Period
            this.ending = Ending
            this.endingUntil = EndingUntil
            this.endingEvents = EndingEvents
            this.days = Days
            recurrentMonth = 0
            return setParams()
        }

        fun setMonthly(Period: Int, Ending: Int, EndingUntil: Long?, EndingEvents: Int, RecurrentMonth: Int): Map<String, String?> {
            repeating = 2
            this.period = Period
            this.ending = Ending
            this.endingUntil = EndingUntil
            this.endingEvents = EndingEvents
            days = "0000000"
            this.recurrentMonth = RecurrentMonth
            return setParams()
        }

        fun setYearly(Period: Int, Ending: Int, EndingUntil: Long?, EndingEvents: Int): Map<String, String?> {
            repeating = 1
            this.period = Period
            this.ending = Ending
            this.endingUntil = EndingUntil
            this.endingEvents = EndingEvents
            days = "0000000"
            recurrentMonth = 0
            return setParams()
        }

        private fun setParams(): Map<String, String?> {
            val map: MutableMap<String, String?> = HashMap()
            map["repeating"] = repeating.toString()
            map["period"] = period.toString()
            map["ending"] = ending.toString()
            map["endingUntil"] = endingUntil.toString()
            map["endingEvents"] = endingEvents.toString()
            map["days"] = days
            map["recurrentMonth"] = recurrentMonth.toString()
            return map
        }
    }
}

