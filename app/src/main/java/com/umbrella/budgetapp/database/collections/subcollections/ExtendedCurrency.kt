package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Country
import com.umbrella.budgetapp.database.collections.Currency

@DatabaseView(
        viewName = "currency_country_cross",
        value = "SELECT * FROM currencies INNER JOIN countries ON currencies.country_ref = countries.country_id"
)
data class ExtendedCurrency (
        @Embedded
        val country: Country?,

        @Embedded
        val currency: Currency
)