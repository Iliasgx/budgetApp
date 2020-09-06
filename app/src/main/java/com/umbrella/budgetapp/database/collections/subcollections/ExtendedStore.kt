package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Store

@DatabaseView(
        viewName = "store_cross",
        value = """SELECT stores.*, categories.*, 
                currency_country_cross.currency_id AS extended_currency_id, currency_country_cross.name AS extended_currency_name, currency_country_cross.position AS extended_currency_position, currency_country_cross.symbol AS extended_currency_symbol 
                FROM stores 
                INNER JOIN currency_country_cross ON stores.currency_ref = currency_country_cross.currency_id 
                INNER JOIN categories ON stores.category_ref = categories.category_id"""
)
data class ExtendedStore (
        @Embedded
        val store: Store,

        @Embedded
        val category: Category,

        @ColumnInfo(name = "extended_currency_id")
        val currencyId: Long?,

        @ColumnInfo(name = "extended_currency_name")
        val currencyName: String?,

        @ColumnInfo(name = "extended_currency_symbol")
        val currencySymbol: String?,

        @ColumnInfo(name = "extended_currency_position")
        val currencyPosition: Int?
)