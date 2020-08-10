package com.umbrella.budgetapp.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface Base<T> {

    /**
     * Insert an object in the database.
     *
     * @param itemOf the object to be inserted.
     */
    @Insert
    suspend fun add(itemOf: T)

    /**
     * Insert an array of objects in the database.
     *
     * @param itemOf the object to be inserted.
     */
    @Insert
    suspend fun add(vararg itemOf: T)

    /**
     * Delete an object from the database.
     *
     * @param itemOf the object to be deleted.
     */
    @Delete
    suspend fun remove(itemOf: T)

    /**
     * Update an object from the database.
     *
     * @param itemOf the object to be updated
     */
    @Update
    suspend fun update(itemOf: T)
}