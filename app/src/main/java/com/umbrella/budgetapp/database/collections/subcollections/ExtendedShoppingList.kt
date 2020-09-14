package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.ShoppingList

@DatabaseView(
        viewName = "shopping_list_cross",
        value = """SELECT shopping_lists.*, 
                categories.category_id AS extended_category_id, categories.category_name AS extended_category_name, 
                stores.store_id AS extended_store_id, stores.store_name AS extended_store_name 
                FROM shopping_lists 
                INNER JOIN categories ON shopping_lists.shopping_list_category_ref = categories.category_id 
                INNER JOIN stores ON shopping_lists.shopping_list_store_ref = stores.store_id"""
)
data class ExtendedShoppingList (
        @Embedded
        val shoppingList: ShoppingList,

        @ColumnInfo(name = "extended_category_id")
        val categoryId: Long?,

        @ColumnInfo(name = "extended_category_name")
        val categoryName: String?,

        @ColumnInfo(name = "extended_store_id")
        val storeId: Long?,

        @ColumnInfo(name = "extended_store_name")
        val storeName: String?
)