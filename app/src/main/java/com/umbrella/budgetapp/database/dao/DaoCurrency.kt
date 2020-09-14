package com.umbrella.budgetapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.umbrella.budgetapp.database.collections.Currency
import com.umbrella.budgetapp.database.collections.subcollections.CurrencyAndName
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoCurrency : Base<Currency> {

    /**
     * Retrieves all currencies in a crossReference with a Country.
     *
     * @return The list of currencies in a Flow.
     */
    @Transaction
    @Query("SELECT * FROM currencies ORDER BY currency_position ASC")
    fun getAllCurrencies() : Flow<List<Currency>>

    /**
     * Retrieves name and ID of currencies with a crossReference with a Country.
     *
     * @return The basic list of currencies in a Flow.
     */
    @Transaction
    @Query("SELECT * FROM currency_name")
    fun getBasicCurrencies() : Flow<List<CurrencyAndName>>

    /**
     * Find Currency by ID.
     *
     * @param id: The id corresponding with the Currency.
     * @return A crossReference of the currency with the attached Country.
     */
    @Transaction
    @Query("SELECT * FROM currencies WHERE currency_id =:id")
    fun getCurrencyById(id: Long) : Flow<Currency>

    /**
     * Change the position of a single Currency.
     *
     * @param id: The id corresponding with the Currency.
     * @param position: The position to where it moved.
     */
    @Query("UPDATE currencies SET currency_position = :position WHERE currency_id = :id")
    fun changePosition(id: Long, position: Int)

    /**
     * Increases the position of views.
     * Used when a Currency is moved DOWN and these views have to move UP in the list.
     *
     * @param ids: An array of ID's of currencies that have to be updated.
     */
    @Query("UPDATE currencies SET currency_position = currency_position + 1 WHERE currency_id IN (:ids)")
    suspend fun increasePositionOfIds(vararg ids: Long)

    /**
     * Decreases the position of views.
     * Used when a Currency is moved UP and these views have to move DOWN in the list.
     *
     * @param ids: An array of ID's of currencies that have to be updated.
     */
    @Query("UPDATE currencies SET currency_position = currency_position - 1 WHERE currency_id IN (:ids)")
    suspend fun decreasePositionOfIds(vararg ids: Long)

    @Query("UPDATE currencies SET currency_position = currency_position + 1 WHERE currency_position > :startPos AND currency_position < :endPos")
    suspend fun increasePositions(startPos: Int, endPos: Int)

    @Query("UPDATE currencies SET currency_position = currency_position - 1 WHERE currency_position > :startPos AND currency_position < :endPos")
    suspend fun decreasePositions(startPos: Int, endPos: Int)
}