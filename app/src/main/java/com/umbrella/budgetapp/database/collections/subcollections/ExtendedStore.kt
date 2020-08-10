package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Store

@DatabaseView(
        viewName = "store_cross",
        value = """SELECT stores.*, 
                currency_country_cross.currency_id AS extended_currency_id, currency_country_cross.name AS extended_currency_name, 
                categories.category_id AS extended_category_id, categories.name AS extended_category_name 
                FROM stores 
                INNER JOIN currency_country_cross ON stores.currency_ref = currency_country_cross.currency_id 
                INNER JOIN categories ON stores.category_ref = categories.category_id"""
)
data class ExtendedStore (
        @Embedded
        val store: Store?,

        @ColumnInfo(name = "extended_currency_id")
        val currencyId: Long?,

        @ColumnInfo(name = "extended_currency_name")
        val currencyName: String?,

        @ColumnInfo(name = "extended_category_id")
        val categoryId: Long?,

        @ColumnInfo(name = "extended_category_name")
        val categoryName: String?
)