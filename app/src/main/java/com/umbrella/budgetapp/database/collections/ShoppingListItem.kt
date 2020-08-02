package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list_items")
data class ShoppingListItem (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "shopping_list_item_id")
        val id: Long,

        @ColumnInfo(name = "number")
        var number: Int? = 0,

        @ColumnInfo(name = "position")
        var position: Int? = 0,

        @ColumnInfo(name = "checked")
        var checked: Boolean? = false
)