package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "records")
data class Record(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "record_id")
        val id: Long?,

        @ColumnInfo(name = "description")
        var description: String? = "",

        @ColumnInfo(name = "payee")
        var payee: String? = "",

        @ColumnInfo(name = "category_ref")
        var categoryRef: Long? = 0,

        @ColumnInfo(name = "account_ref")
        var accountRef: Long? = 0,

        @ColumnInfo(name = "store_ref")
        var storeRef: Long? = 0,

        @ColumnInfo(name = "currency_ref")
        var currencyRef: Long? = 0,

        @ColumnInfo(name = "type")
        var type: Int? = 0,

        @ColumnInfo(name = "payment_type")
        var paymentType: Int? = 0,

        @ColumnInfo(name = "amount")
        var amount: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "timestamp")
        var timestamp: Long? = 0
)