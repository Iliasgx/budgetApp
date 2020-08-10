package com.umbrella.budgetapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import com.umbrella.budgetapp.database.collections.Category
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoCategory {

    /**
     * Retrieves all categories.
     *
     * @return The list of categories in a Flow.
     */
    @Query("SELECT category_id, name, icon, color FROM categories ORDER BY name")
    fun getAllCategories() : Flow<List<Category>>

    /**
     * Find Category by ID.
     *
     * @param id: The ID corresponding with the Category.
     * @return The Category represented by the ID.
     */
    @Query("SELECT * FROM categories WHERE category_id = :id")
    fun getCategoryById(id: Long) : Category


}