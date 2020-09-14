package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "records")
data class Record(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "record_id")
        val id: Long? = null,

        @ColumnInfo(name = "record_description")
        var description: String? = "",

        @ColumnInfo(name = "record_payee")
        var payee: String? = "",

        @ColumnInfo(name = "record_category_ref")
        var categoryRef: Long? = 0,

        @ColumnInfo(name = "record_account_ref")
        var accountRef: Long? = 0,

        @ColumnInfo(name = "record_store_ref")
        var storeRef: Long? = 0,

        @ColumnInfo(name = "record_currency_ref")
        var currencyRef: Long? = 0,

        @ColumnInfo(name = "record_type")
        var type: Int? = 0,

        @ColumnInfo(name = "record_payment_type")
        var paymentType: Int? = 0,

        @ColumnInfo(name = "record_amount")
        var amount: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "record_timestamp")
        var timestamp: Long? = 0
)