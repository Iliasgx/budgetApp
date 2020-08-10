package com.umbrella.budgetapp.database.collections

import androidx.room.*
import com.umbrella.budgetapp.database.typeconverters.ShoppingListItemListTypeConverter

@Entity(tableName = "shopping_lists")
@TypeConverters(ShoppingListItemListTypeConverter::class)
data class ShoppingList(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "shopping_list_id")
        val id: Long?,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @ColumnInfo(name = "category_ref")
        var categoryRef: Long? = 0,

        @ColumnInfo(name = "store_ref")
        var storeRef: Long? = 0,

        @ColumnInfo(name = "reminder")
        var reminder: Long? = 0,

        @ColumnInfo(name = "items")
        var items: MutableList<ShoppingListItem>? = mutableListOf()
) {
        @Ignore
        val items_count: Int? = 0
}
