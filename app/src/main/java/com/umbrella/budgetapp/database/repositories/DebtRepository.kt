package com.umbrella.budgetapp.database.repositories

import com.umbrella.budgetapp.cache.App
import com.umbrella.budgetapp.database.BudgetDatabase
import com.umbrella.budgetapp.database.collections.Debt
import com.umbrella.budgetapp.database.dao.DaoDebt

class DebtRepository {

    private val daoDebt : DaoDebt

    init {
        val db = BudgetDatabase.getDatabase(App.app!!)
        daoDebt = db.daoDebt()
    }

    fun getAllDebts(type: Int)= daoDebt.getAllDebts(type)

    fun getFunctionDebt() = daoDebt.getFunctionDebt()

    fun getDebtById(id: Long) = daoDebt.getDebtById(id)

    suspend fun addDebt(debt: Debt) = daoDebt.add(debt)

    suspend fun updateDebt(debt: Debt) = daoDebt.update(debt)

    suspend fun removeDebt(debt: Debt) = daoDebt.remove(debt)

}