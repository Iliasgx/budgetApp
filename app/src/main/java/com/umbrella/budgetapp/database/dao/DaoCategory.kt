package com.umbrella.budgetapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import com.umbrella.budgetapp.database.collections.Category
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoCategory : Base<Category> {

    /**
     * Retrieves all categories.
     *
     * @return The list of categories in a Flow.
     */
    @Query("SELECT category_id, category_name, category_icon, category_color FROM categories ORDER BY category_name")
    fun getAllCategories() : Flow<List<Category>>

    /**
     * Find Category by ID.
     *
     * @param id: The ID corresponding with the Category.
     * @return The Category represented by the ID.
     */
    @Query("SELECT * FROM categories WHERE category_id = :id")
    fun getCategoryById(id: Long) : Flow<Category>


}