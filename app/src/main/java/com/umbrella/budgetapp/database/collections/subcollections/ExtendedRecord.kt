package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Record

@DatabaseView(
        viewName = "record_cross",
        value = """SELECT records.*, categories.*, 
                accounts.account_id AS extended_account_id, accounts.name AS extended_account_name, accounts.position AS extended_account_position, 
                stores.store_id AS extended_store_id, stores.name AS extended_store_name, 
                currencies.currency_id AS extended_currency_id, currencies.country_ref AS extended_country_ref, currencies.position AS extended_currency_position 
                FROM records 
                INNER JOIN accounts ON records.account_ref = accounts.account_id 
                INNER JOIN categories ON records.category_ref = categories.category_id 
                INNER JOIN stores ON records.store_ref = stores.store_id 
                INNER JOIN currencies ON records.currency_ref = currencies.currency_id"""
)
data class ExtendedRecord (
        @Embedded
        val record: Record,

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