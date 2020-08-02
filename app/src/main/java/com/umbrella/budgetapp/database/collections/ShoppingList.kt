package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "shopping_lists")
data class ShoppingList(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "shopping_list_id")
        val id: Long,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @Relation(parentColumn = "category_id", entityColumn = "category_ref")
        @ColumnInfo(name = "category_ref")
        var category: Category? = null,

        @Relation(parentColumn = "store_id", entityColumn = "store_ref")
        @ColumnInfo(name = "store_ref")
        var store: Store? = null,

        @ColumnInfo(name = "reminder")
        var reminder: Long? = 0
)
