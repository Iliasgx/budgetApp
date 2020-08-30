package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.DatabaseView

@DatabaseView(
        viewName = "currency_name",
        value = """SELECT currencies.currency_id AS id, countries.name AS name 
                    FROM currencies 
                    INNER JOIN countries ON currencies.country_ref = countries.country_id
                    ORDER BY currencies.position ASC"""
)
data class CurrencyAndName (
        val id: Long?,

        val name: String?
)