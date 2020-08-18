package com.umbrella.budgetapp.database.repositories

import androidx.annotation.IntRange
import com.umbrella.budgetapp.cache.App
import com.umbrella.budgetapp.database.BudgetDatabase
import com.umbrella.budgetapp.database.collections.PlannedPayment
import com.umbrella.budgetapp.database.dao.DaoPlannedPayment

class PlannedPaymentRepository {

    private val daoPlannedPayment: DaoPlannedPayment

    init {
        val db = BudgetDatabase.getDatabase(App.app!!)
        daoPlannedPayment = db.daoPlannedPayment()
    }

    fun getAllPlannedPayments() = daoPlannedPayment.getAllPlannedPayments()

    fun getAllPlannedPayments(@IntRange(from = 1) limit: Int) = daoPlannedPayment.getAllPlannedPayments(limit)

    fun getPlannedPaymentById(id: Long) = daoPlannedPayment.getPlannedPaymentById(id)

    suspend fun addPlannedPayment(plannedPayment: PlannedPayment) = daoPlannedPayment.add(plannedPayment)

    suspend fun updatePlannedPayment(plannedPayment: PlannedPayment) = daoPlannedPayment.update(plannedPayment)

    suspend fun removePlannedPayment(plannedPayment: PlannedPayment) = daoPlannedPayment.remove(plannedPayment)
}