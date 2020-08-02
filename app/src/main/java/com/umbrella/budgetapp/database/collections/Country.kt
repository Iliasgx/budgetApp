package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "countries")
data class Country(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "country_id")
        val id: Long,

        @ColumnInfo(name = "name")
        val name: String? = "",

        @ColumnInfo(name = "symbol")
        val symbol: String? = "",

        @ColumnInfo(name = "default_rate")
        val defaultRate: BigDecimal? = BigDecimal.ZERO
)
