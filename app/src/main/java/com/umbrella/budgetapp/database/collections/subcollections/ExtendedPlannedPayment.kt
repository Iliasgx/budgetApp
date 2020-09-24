package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.PlannedPayment

@DatabaseView(
        viewName = "planned_payment_cross",
        value = """SELECT planned_payments.*, 
                accounts.account_id AS extended_account_id, accounts.account_name AS extended_account_name, accounts.account_position AS extended_account_position, 
                categories.category_id AS extended_category_id, categories.category_name AS extended_category_name, 
                currencies.currency_id AS extended_currency_id, currencies.currency_country_ref AS extended_country_ref, currencies.currency_position AS extended_currency_position 
                FROM planned_payments 
                LEFT JOIN accounts ON planned_payments.planned_payment_account_ref = accounts.account_id 
                LEFT JOIN categories ON planned_payments.planned_payment_category_ref = categories.category_id 
                LEFT JOIN currencies ON planned_payments.planned_payment_currency_ref = currencies.currency_id"""
)
data class ExtendedPlannedPayment (
        @Embedded
        val plannedPayment: PlannedPayment,

        @ColumnInfo(name = "extended_account_id")
        val accountId: Long?,

        @ColumnInfo(name = "extended_account_name")
        val accountName: String?,

        @ColumnInfo(name = "extended_account_position")
        val accountPosition: Int?,

        @ColumnInfo(name = "extended_category_id")
        val categoryId: Long?,

        @ColumnInfo(name = "extended_category_name")
        val categoryName: String?,

        @ColumnInfo(name = "extended_currency_id")
        val currencyId: Long?,

        @ColumnInfo(name = "extended_country_ref")
        val countryRef: Long?,

        @ColumnInfo(name = "extended_currency_position")
        val currencyPosition: Int?
)