package com.umbrella.budgetapp.cache

import com.umbrella.budgetapp.database.collections.Country
import com.umbrella.budgetapp.database.collections.User
import com.umbrella.budgetapp.database.defaults.DefaultCountries

object Memory {
    lateinit var loggedUser: User

    var lastUsedCountry: Country = DefaultCountries().getCountryById(null)
}