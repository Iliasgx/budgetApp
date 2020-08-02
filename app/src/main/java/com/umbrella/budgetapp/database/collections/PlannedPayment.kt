package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.math.BigDecimal

@Entity(tableName = "planned_payments")
data class PlannedPayment(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "planned_payments_id")
        val id: Long,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @ColumnInfo(name = "payee")
        var payee: String? = "",

        @ColumnInfo(name = "note")
        var note: String? = "",

        @Relation(parentColumn = "account_id", entityColumn = "account_ref")
        @ColumnInfo(name = "account_ref")
        var account: Account? = null,

        @Relation(parentColumn = "category_id", entityColumn = "category_ref")
        @ColumnInfo(name = "category_ref")
        var category: Category? = null,

        @Relation(parentColumn = "currency_id", entityColumn = "currency_ref")
        @ColumnInfo(name = "currency_ref")
        var currency: Currency? = null,

        @ColumnInfo(name = "start_date")
        var startDate: Long? = 0,

        @ColumnInfo(name = "frequency")
        var frequency: Map<String, String>? = emptyMap(),

        @ColumnInfo(name = "type")
        var type: Int? = 0,

        @ColumnInfo(name = "payment_type")
        var paymentType: Int? = 0,

        @ColumnInfo(name = "reminder_options")
        var reminderOptions: Int? = 0,

        @ColumnInfo(name = "amount")
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
            map["endingUntil"] = java.lang.String.valueOf(endingUntil)
            map["endingEvents"] = endingEvents.toString()
            map["days"] = days
            map["recurrentMonth"] = recurrentMonth.toString()
            return map
        }
    }
}

