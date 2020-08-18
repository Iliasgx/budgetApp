package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Account

@DatabaseView(
        viewName = "account_cross",
        value = """SELECT accounts.*, 
                currency_country_cross.currency_id AS extended_currency_id, currency_country_cross.name AS extended_currency_name, 
                currency_country_cross.symbol AS extended_currency_symbol, currency_country_cross.position AS extended_currency_position 
                FROM accounts 
                INNER JOIN currency_country_cross ON accounts.currency_ref = currency_country_cross.currency_id"""
)
data class ExtendedAccount (
        @Embedded
        val account: Account,

        @ColumnInfo(name = "extended_currency_id")
        val currencyId: Long?,

        @ColumnInfo(name = "extended_currency_name")
        val currencyName: String?,

        @ColumnInfo(name = "extended_currency_symbol")
        val currencySymbol: String?,

        @ColumnInfo(name = "extended_currency_position")
        val currencyPosition: Int?
)