package com.umbrella.budgetapp.database.repositories

import com.umbrella.budgetapp.cache.App
import com.umbrella.budgetapp.database.BudgetDatabase
import com.umbrella.budgetapp.database.collections.ShoppingList
import com.umbrella.budgetapp.database.dao.DaoShoppingList

class ShoppingListRepository {

    private val daoShoppingList: DaoShoppingList

    init {
        val db = BudgetDatabase.getDatabase(App.app!!)
        daoShoppingList = db.daoShoppingList()
    }

    fun getAllShoppingLists() = daoShoppingList.getAllShoppingLists()

    fun getShoppingListById(id: Long) = daoShoppingList.getShoppingListById(id)

    suspend fun addShoppingList(shoppingList: ShoppingList) = daoShoppingList.add(shoppingList)

    suspend fun updateShoppingList(shoppingList: ShoppingList) = daoShoppingList.update(shoppingList)

    suspend fun removeShoppingList(shoppingList: ShoppingList) = daoShoppingList.remove(shoppingList)
}