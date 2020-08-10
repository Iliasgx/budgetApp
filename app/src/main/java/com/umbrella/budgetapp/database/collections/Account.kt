package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "accounts")
data class Account(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "account_id")
        val id: Long?,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @ColumnInfo(name = "type")
        var type: Int? = 0,

        @ColumnInfo(name = "color")
        var color: Int? = 0,

        @ColumnInfo(name = "current_value")
        var currentValue: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "position")
        var position: Int? = 0,

        @ColumnInfo(name = "currency_ref")
        var currencyRef: Long? = 0,

        @ColumnInfo(name = "exclude_stats")
        var excludeStats: Boolean? = false
)
