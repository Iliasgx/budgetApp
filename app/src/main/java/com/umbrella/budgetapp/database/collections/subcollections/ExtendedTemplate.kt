package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Template

@DatabaseView(
        viewName = "template_cross",
        value = """SELECT templates.*, 
                categories.category_id AS extended_category_id, categories.name AS extended_category_name, 
                accounts.account_id AS extended_account_id, accounts.name AS extended_account_name, 
                stores.store_id AS extended_store_id, stores.name AS extended_store_name, 
                currency_country_cross.currency_id AS extended_currency_id, currency_country_cross.name AS extended_currency_name 
                FROM templates 
                INNER JOIN categories ON templates.category_ref = categories.category_id 
                INNER JOIN accounts ON templates.account_ref = accounts.account_id 
                INNER JOIN stores ON templates.store_ref = stores.store_id 
                INNER JOIN currency_country_cross ON templates.currency_ref = currency_country_cross.currency_id"""
)
data class ExtendedTemplate (
        @Embedded
        val template: Template?,

        @ColumnInfo(name = "extended_category_id")
        val categoryId: Long?,

        @ColumnInfo(name = "extended_category_name")
        val categoryName: String?,

        @ColumnInfo(name = "extended_account_id")
        val accountId: Long?,

        @ColumnInfo(name = "extended_account_name")
        val accountName: String?,

        @ColumnInfo(name = "extended_store_id")
        val storeId: Long?,

        @ColumnInfo(name = "extended_store_name")
        val storeName: String?,

        @ColumnInfo(name = "extended_currency_id")
        val currencyId: Long?,

        @ColumnInfo(name = "extended_currency_name")
        val currencyName: String?
)