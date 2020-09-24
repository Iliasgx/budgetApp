package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Template

@DatabaseView(
        viewName = "template_cross",
        value = """SELECT templates.*, categories.*, 
                accounts.account_id AS extended_account_id, accounts.account_name AS extended_account_name, accounts.account_position AS extended_account_position, 
                stores.store_id AS extended_store_id, stores.store_name AS extended_store_name, 
                currencies.currency_id AS extended_currency_id, currencies.currency_country_ref AS extended_country_ref, currencies.currency_position AS extended_currency_position
                FROM templates 
                LEFT JOIN categories ON templates.template_category_ref = categories.category_id 
                LEFT JOIN accounts ON templates.template_account_ref = accounts.account_id 
                LEFT JOIN stores ON templates.template_store_ref = stores.store_id 
                LEFT JOIN currencies ON templates.template_currency_ref = currencies.currency_id"""
)
data class ExtendedTemplate (
        @Embedded
        val template: Template,

        @Embedded
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