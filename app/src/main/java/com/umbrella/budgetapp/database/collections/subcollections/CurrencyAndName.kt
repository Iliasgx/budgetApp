package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.DatabaseView

@DatabaseView(
        viewName = "currency_name",
        value = "SELECT currency_id AS id, country_ref as countryRef FROM currencies"
)
data class CurrencyAndName (
        val id: Long,

        val countryRef: Long
)