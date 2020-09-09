package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Template

@DatabaseView(
        viewName = "template_cross",
        value = """SELECT templates.*, categories.*, 
                accounts.account_id AS extended_account_id, accounts.name AS extended_account_name, accounts.position AS extended_account_position, 
                stores.store_id AS extended_store_id, stores.name AS extended_store_name, 
                currencies.currency_id AS extended_currency_id, currencies.country_ref AS extended_country_ref, currencies.position AS extended_currency_position
                FROM templates 
                INNER JOIN categories ON templates.category_ref = categories.category_id 
                INNER JOIN accounts ON templates.account_ref = accounts.account_id 
                INNER JOIN stores ON templates.store_ref = stores.store_id 
                INNER JOIN currencies ON templates.currency_ref = currencies.currency_id"""
)
data class ExtendedTemplate (
        @Embedded
        val template: Template,

        @Embedded(prefix = "cat_")
        val category: Category,

        @ColumnInfo(name = "extended_account_id")
        val accountId: Long?,

        @ColumnInfo(name = "extended_account_name")
        val accountName: String?,

        @ColumnInfo(name = "extended_account_position")
        val accountPosition: Int?,

        @ColumnInfo(name = "extended_store_id")
        val storeId: Long?,

        @ColumnInfo(name = "extended_store_name")
        val storeName: String?,

        @ColumnInfo(name = "extended_currency_id")
        val currencyId: Long?,

        @ColumnInfo(name = "extended_country_ref")
        val countryRef: Long?,

        @ColumnInfo(name = "extended_currency_position")
        val currencyPosition: Int?
)