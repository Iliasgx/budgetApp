package com.umbrella.budgetapp.database.repositories

import com.umbrella.budgetapp.cache.App
import com.umbrella.budgetapp.database.BudgetDatabase
import com.umbrella.budgetapp.database.collections.Goal
import com.umbrella.budgetapp.database.dao.DaoGoal
import com.umbrella.budgetapp.enums.GoalStatus

class GoalRepository {

    private val daoGoal : DaoGoal

    init {
        val db = BudgetDatabase.getDatabase(App.app!!)
        daoGoal = db.daoGoal()
    }

    fun getAllGoalsUnreached(status: GoalStatus) = daoGoal.getAllGoalsUnreached(status)

    fun getAllGoalsReached() = daoGoal.getAllGoalsReached()

    fun getGoalById(id: Long) = daoGoal.getGoalById(id)

    suspend fun addGoal(goal: Goal) = daoGoal.add(goal)

    suspend fun updateGoal(goal: Goal) = daoGoal.update(goal)

    suspend fun removeGoal(goal: Goal) = daoGoal.remove(goal)
}