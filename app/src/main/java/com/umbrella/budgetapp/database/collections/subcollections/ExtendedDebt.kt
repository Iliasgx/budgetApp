package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Debt

@DatabaseView(
        viewName = "debt_cross",
        value = """SELECT debts.*, categories.*,
                accounts.account_id AS extended_account_id, accounts.account_name AS extended_account_name, accounts.account_position AS extended_account_position, 
                currencies.currency_id AS extended_currency_id, currencies.currency_country_ref AS extended_country_ref, currencies.currency_position AS extended_currency_position 
                FROM debts 
                INNER JOIN categories ON debts.debt_category_ref = categories.category_id 
                INNER JOIN accounts ON debts.debt_account_ref = accounts.account_id 
                INNER JOIN currencies ON debts.debt_currency_ref = currencies.currency_id"""
)
data class ExtendedDebt (
        @Embedded
        val debt: Debt,

        @Embedded
        val category: Category?,

        @ColumnInfo(name = "extended_account_id")
        val accountId: Long?,

        @ColumnInfo(name = "extended_account_name")
        val accountName: String?,

        @ColumnInfo(name = "extended_account_position")
        val accountPosition: Int?,

        @ColumnInfo(name = "extended_currency_id")
        val currencyId: Long?,

        @ColumnInfo(name = "extended_country_ref")
        val countryRef: Long?,

        @ColumnInfo(name = "extended_currency_position")
        val currencyPosition: Int?
)