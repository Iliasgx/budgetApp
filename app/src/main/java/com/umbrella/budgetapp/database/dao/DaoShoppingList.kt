package com.umbrella.budgetapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.umbrella.budgetapp.database.collections.ShoppingList
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedShoppingList
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoShoppingList : Base<ShoppingList> {

    /**
     * Retrieves all shoppingLists.
     *
     * @return The list of shoppingLists in a Flow.
     */
    @Query("SELECT shopping_list_id, name, COUNT(items) AS items_count FROM shopping_lists ORDER BY name")
    fun getAllShoppingLists() : Flow<List<ShoppingList>>

    /**
     * Find ShoppingList by ID.
     *
     * @param id: The id corresponding with the ShoppingList.
     * @return A crossReference of the shoppingLists with the attached entities.
     */
    @Transaction
    @Query("SELECT * FROM shopping_list_cross WHERE shopping_list_id =:id")
    fun getShoppingListById(id: Long) : ExtendedShoppingList
}