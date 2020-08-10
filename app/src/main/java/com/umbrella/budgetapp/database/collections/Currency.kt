package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "currencies")
data class Currency(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "currency_id")
        val id: Long?,

        @ColumnInfo(name = "country_ref", index = true)
        val countryRef: Long? = 0,

        @ColumnInfo(name = "used_rate")
        var usedRate: BigDecimal? = BigDecimal.ONE,

        @ColumnInfo(name = "position")
        var position: Int?
)
