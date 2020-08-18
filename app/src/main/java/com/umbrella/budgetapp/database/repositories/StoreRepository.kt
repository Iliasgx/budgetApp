package com.umbrella.budgetapp.database.repositories

import com.umbrella.budgetapp.cache.App
import com.umbrella.budgetapp.database.BudgetDatabase
import com.umbrella.budgetapp.database.collections.Store
import com.umbrella.budgetapp.database.dao.DaoStore

class StoreRepository {

    private val daoStore: DaoStore

    init {
        val db = BudgetDatabase.getDatabase(App.app!!)
        daoStore = db.daoStore()
    }

    fun getAllStores() = daoStore.getAllStores()

    fun getStoreById(id: Long) = daoStore.getStoreById(id)

    suspend fun addStore(store: Store) = daoStore.add(store)

    suspend fun updateStore(store: Store) = daoStore.update(store)

    suspend fun removeStore(store: Store) = daoStore.remove(store)
}