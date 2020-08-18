package com.umbrella.budgetapp.database.repositories

import com.umbrella.budgetapp.cache.App
import com.umbrella.budgetapp.database.BudgetDatabase
import com.umbrella.budgetapp.database.collections.User
import com.umbrella.budgetapp.database.dao.DaoUser

class UserRepository {

    private val daoUser: DaoUser

    init {
        val db = BudgetDatabase.getDatabase(App.app!!)
        daoUser = db.daoUser()
    }

    fun getUserById(id: Long) = daoUser.getUserById(id)

    suspend fun addUser(user: User) = daoUser.add(user)

    suspend fun updateUser(user: User) = daoUser.update(user)
}