package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.math.BigDecimal

@Entity(tableName = "currencies")
data class Currency(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "currency_id")
        val id: Long,

        @Relation(parentColumn = "country_ref", entityColumn = "country_id")
        @ColumnInfo(name = "country_ref")
        val country: Country,

        @ColumnInfo(name = "used_rate")
        var usedRate: BigDecimal? = country.defaultRate,

        @ColumnInfo(name = "position")
        var position: Int
)
