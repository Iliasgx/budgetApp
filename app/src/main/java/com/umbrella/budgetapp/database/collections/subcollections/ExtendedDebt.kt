package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Debt

@DatabaseView(
        viewName = "debt_cross",
        value = """SELECT DEB.*, CAT.*,
                ACC.account_id AS extended_account_id, ACC.account_name AS extended_account_name, ACC.account_position AS extended_account_position, 
                CURR.currency_id AS extended_currency_id, CURR.currency_country_ref AS extended_country_ref, CURR.currency_position AS extended_currency_position 
                FROM debts DEB
                LEFT JOIN categories CAT ON DEB.debt_category_ref = CAT.category_id 
                LEFT JOIN accounts ACC ON DEB.debt_account_ref = ACC.account_id 
                LEFT JOIN currencies CURR ON DEB.debt_currency_ref = CURR.currency_id"""
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