package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Account

@DatabaseView(
        viewName = "account_cross",
        value = """SELECT accounts.*, 
                currencies.currency_id AS extended_currency_id, currencies.currency_country_ref AS extended_country_ref, currencies.currency_position AS extended_currency_position
                FROM accounts 
                LEFT JOIN currencies ON accounts.account_currency_ref = currencies.currency_id"""
)
data class ExtendedAccount (
        @Embedded
        val account: Account,

        @ColumnInfo(name = "extended_currency_id")
        val currencyId: Long?,

        @ColumnInfo(name = "extended_country_ref")
        val countryRef: Long?,

        @ColumnInfo(name = "extended_currency_position")
        val currencyPosition: Int?
)