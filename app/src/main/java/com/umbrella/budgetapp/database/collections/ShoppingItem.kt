package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "shopping_items")
data class ShoppingItem (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "shopping_item_id")
        val id: Long,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @ColumnInfo(name = "default_amount")
        var defAmount: BigDecimal? = BigDecimal.ZERO
)