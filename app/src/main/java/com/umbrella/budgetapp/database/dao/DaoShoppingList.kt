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
    @Query("SELECT shopping_list_id, shopping_list_name, shopping_list_items FROM shopping_lists ORDER BY shopping_list_name ASC")
    fun getAllShoppingLists() : Flow<List<ShoppingList>>

    /**
     * Retrieves a limited number of shoppingLists.
     *
     * @return The list of shoppingLists in a Flow.
     */
    @Query("SELECT shopping_list_id, shopping_list_name, shopping_list_items FROM shopping_lists ORDER BY shopping_list_name ASC LIMIT :limit")
    fun getAllShoppingLists(limit: Int) : Flow<List<ShoppingList>>

    /**
     * Find ShoppingList By ID.
     *
     * @param id: The id corresponding with the ShoppingList.
     * @return the ShoppingList corresponding with the given id.
     */
    @Query("SELECT * FROM shopping_lists WHERE shopping_list_id =:id")
    fun getShoppingListById(id: Long) : Flow<ShoppingList>

    /**
     * Find an extended ShoppingList by ID.
     *
     * @param id: The id corresponding with the ShoppingList.
     * @return A crossReference of the shoppingLists with the attached entities.
     */
    @Transaction
    @Query("SELECT * FROM shopping_list_cross WHERE shopping_list_id =:id")
    fun getExtendedShoppingListById(id: Long) : ExtendedShoppingList

}