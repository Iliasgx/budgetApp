package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Account

@DatabaseView(
        viewName = "account_cross",
        value = """SELECT accounts.*, 
                currencies.currency_id AS extended_currency_id, currencies.country_ref AS extended_country_ref, currencies.position AS extended_currency_position
                FROM accounts 
                INNER JOIN currencies ON accounts.currency_ref = currencies.currency_id"""
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