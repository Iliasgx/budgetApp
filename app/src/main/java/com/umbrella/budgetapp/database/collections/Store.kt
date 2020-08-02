package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "stores")
data class Store(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "store_id")
        val id: Long,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @ColumnInfo(name = "note")
        var note: String? = "",

        @Relation(parentColumn = "currency_id", entityColumn = "currency_ref")
        @ColumnInfo(name = "currency_ref")
        var currency: Currency? = null,

        @Relation(parentColumn = "category_id", entityColumn = "category_ref")
        @ColumnInfo(name  ="category_ref")
        var category: Category? = null
)
