package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.PlannedPayment

@DatabaseView(
        viewName = "planned_payment_cross",
        value = """SELECT planned_payments.*, 
                accounts.account_id AS extended_account_id, accounts.name AS extended_account_name, 
                categories.category_id AS extended_category_id, categories.name AS extended_category_name, 
                currency_country_cross.currency_id AS extended_currency_id, currency_country_cross.name AS extended_currency_name 
                FROM planned_payments 
                INNER JOIN accounts ON planned_payments.account_ref = accounts.account_id 
                INNER JOIN categories ON planned_payments.category_ref = categories.category_id 
                INNER JOIN currency_country_cross ON planned_payments.currency_ref = currency_country_cross.currency_id"""
)
data class ExtendedPlannedPayment (
        @Embedded
        val plannedPayment: PlannedPayment?,

        @ColumnInfo(name = "extended_account_id")
        val accountId: Long?,

        @ColumnInfo(name = "extended_account_name")
        val accountName: String?,

        @ColumnInfo(name = "extended_category_id")
        val categoryId: Long?,

        @ColumnInfo(name = "extended_category_name")
        val categoryName: String?,

        @ColumnInfo(name = "extended_currency_id")
        val currencyId: Long?,

        @ColumnInfo(name = "extended_currency_name")
        val currencyName: String?
)