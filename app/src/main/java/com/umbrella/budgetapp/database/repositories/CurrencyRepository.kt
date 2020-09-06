package com.umbrella.budgetapp.database.repositories

import com.umbrella.budgetapp.cache.App
import com.umbrella.budgetapp.database.BudgetDatabase
import com.umbrella.budgetapp.database.collections.Currency
import com.umbrella.budgetapp.database.dao.DaoCurrency

class CurrencyRepository {

    private val daoCurrency: DaoCurrency

    init {
        val db = BudgetDatabase.getDatabase(App.app!!)
        daoCurrency = db.daoCurrency()
    }

    fun getAllCurrencies() = daoCurrency.getAllCurrencies()

    fun getBasicCurrencies() = daoCurrency.getBasicCurrencies()
    
    fun getCurrencyById(id: Long) = daoCurrency.getCurrencyById(id)

    fun changePosition(id: Long, position: Int) = daoCurrency.changePosition(id, position)

    suspend fun increasePositionOfIds(vararg ids: Long) = daoCurrency.increasePositionOfIds(*ids)

    suspend fun decreasePositionOfIds(vararg ids: Long) = daoCurrency.decreasePositionOfIds(*ids)

    suspend fun increasePositions(startPos: Int, endPos: Int) = daoCurrency.increasePositions(startPos, endPos)

    suspend fun decreasePositions(startPos: Int, endPos: Int) = daoCurrency.decreasePositions(startPos, endPos)

    suspend fun addCurrency(currency: Currency) = daoCurrency.add(currency)

    suspend fun updateCurrency(currency: Currency) = daoCurrency.update(currency)

    suspend fun removeCurrency(currency: Currency) = daoCurrency.remove(currency)
}