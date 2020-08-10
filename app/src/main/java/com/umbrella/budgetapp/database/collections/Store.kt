package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stores")
data class Store(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "store_id")
        val id: Long?,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @ColumnInfo(name = "note")
        var note: String? = "",

        @ColumnInfo(name = "currency_ref")
        var currencyRef: Long? = 0,

        @ColumnInfo(name  ="category_ref")
        var categoryRef: Long? = 0
)
