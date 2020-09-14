package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.umbrella.budgetapp.database.typeconverters.ShoppingListItemListTypeConverter

@Entity(tableName = "shopping_lists")
@TypeConverters(ShoppingListItemListTypeConverter::class)
data class ShoppingList(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "shopping_list_id")
        val id: Long? = null,

        @ColumnInfo(name = "shopping_list_name")
        var name: String? = "",

        @ColumnInfo(name = "shopping_list_category_ref")
        var categoryRef: Long? = 0,

        @ColumnInfo(name = "shopping_list_store_ref")
        var storeRef: Long? = 0,

        @ColumnInfo(name = "shopping_list_reminder")
        var reminder: Long? = 0,

        @ColumnInfo(name = "shopping_list_items")
        var items: MutableList<ShoppingListItem>? = mutableListOf()
)
