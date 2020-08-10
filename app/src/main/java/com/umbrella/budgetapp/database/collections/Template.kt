package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "templates")
data class Template(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "template_id")
        val id: Long?,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @ColumnInfo(name = "account_ref")
        var accountRef: Long? = 0,

        @ColumnInfo(name = "category_ref")
        var categoryRef: Long? = 0,

        @ColumnInfo(name = "currency_ref")
        var currencyRef: Long? = 0,

        @ColumnInfo(name = "store_ref")
        var storeRef: Long? = 0,

        @ColumnInfo(name = "payee")
        var payee: String? = "",

        @ColumnInfo(name = "note")
        var note: String? = "",

        @ColumnInfo(name = "type")
        var type: Int? = 0,

        @ColumnInfo(name = "payment_type")
        var paymentType: Int? = 0,

        @ColumnInfo(name = "position")
        var position: Int? = 0,

        @ColumnInfo(name = "amount")
        var amount: BigDecimal? = BigDecimal.ZERO
)
