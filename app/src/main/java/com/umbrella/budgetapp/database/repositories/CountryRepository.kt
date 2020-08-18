package com.umbrella.budgetapp.database.repositories

import com.umbrella.budgetapp.cache.App
import com.umbrella.budgetapp.database.BudgetDatabase
import com.umbrella.budgetapp.database.collections.Country
import com.umbrella.budgetapp.database.dao.DaoCountry

class CountryRepository {

    private val daoCountry: DaoCountry

    init {
        val db = BudgetDatabase.getDatabase(App.app!!)
        daoCountry = db.daoCountry()
    }

    fun getAllCountries() = daoCountry.getAllCountries()

    fun getCountryById(id: Long) = daoCountry.getCountryById(id)

    suspend fun addCountry(country: Country) = daoCountry.add(country)
}