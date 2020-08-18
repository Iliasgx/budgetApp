package com.umbrella.budgetapp.database.repositories

import com.umbrella.budgetapp.cache.App
import com.umbrella.budgetapp.database.BudgetDatabase
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.dao.DaoCategory

class CategoryRepository {

    private val daoCategory: DaoCategory

    init {
        val db = BudgetDatabase.getDatabase(App.app!!)
        daoCategory = db.daoCategory()
    }

    fun getAllCategories() = daoCategory.getAllCategories()

    fun getCategoryById(id: Long) = daoCategory.getCategoryById(id)

    suspend fun addCategory(category: Category) = daoCategory.add(category)

    suspend fun updateCategory(category: Category) = daoCategory.update(category)

    suspend fun removeCategory(category: Category) = daoCategory.remove(category)
}