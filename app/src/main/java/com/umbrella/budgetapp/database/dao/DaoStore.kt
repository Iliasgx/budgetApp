package com.umbrella.budgetapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.umbrella.budgetapp.database.collections.Store
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedStore
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoStore : Base<Store> {

    /**
     * Retrieves all stores.
     *
     * @return The list of stores in a Flow.
     */
    @Transaction
    @Query("SELECT * FROM store_cross ORDER BY store_name")
    fun getAllStores() : Flow<List<ExtendedStore>>

    /**
     * Find Store by ID.
     *
     * @param id: The ID corresponding with the Store.
     * @return The Store represented by the ID.
     */
    @Transaction
    @Query("SELECT * FROM store_cross WHERE store_id = :id")
    fun getStoreById(id: Long) : Flow<ExtendedStore>
}