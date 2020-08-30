package com.umbrella.budgetapp.database.dao

import androidx.annotation.IntRange
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedAccount
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoAccount : Base<Account> {

    /**
     * Retrieves basic information of the accounts.
     *
     * @return The list of accounts in a Flow.
     */
    @Query("SELECT account_id, name from accounts ORDER BY position ASC")
    fun getAccountBasics() : Flow<List<Account>>

    /**
     * Retrieves all accounts in a crossReference with a Currency.
     *
     * @param limit: Set a limit for displaying accounts.
     * @return The list of accounts in a Flow.
     */
    @Transaction
    @Query("SELECT account_id, name, color, current_value, position, exclude_stats, extended_currency_symbol FROM account_cross ORDER BY position ASC LIMIT :limit")
    fun getAllAccountsSmall(@IntRange(from = 1) limit: Int) : Flow<List<ExtendedAccount>>

    /**
     * Retrieves all list information of accounts.
     *
     * @return The list of accounts in a Flow.
     */
    @Transaction
    @Query("SELECT account_id, name, position, color, type, exclude_stats FROM accounts ORDER BY position ASC")
    fun getAllAccounts() : Flow<List<Account>>

    /**
     * Find Account by ID. Account with crossReferences.
     *
     * @param id: The id corresponding with the Account.
     * @return A crossReference of the account with the attached references.
     */
    @Transaction
    @Query("SELECT * FROM account_cross WHERE account_id = :id")
    fun getAccountById(id: Long) : ExtendedAccount

    /**
     * Change the position of a single account.
     *
     * @param id: The id corresponding with the Account.
     * @param position: The position to where it moved.
     */
    @Query("UPDATE accounts SET position = :position WHERE account_id = :id")
    fun changePosition(id: Long, position: Int)

    /**
     * Increases the position of views.
     * Used when a Account is moved DOWN and these views have to move UP in the list.
     *
     * @param ids: An array of ID's of accounts that have to be updated.
     */
    @Query("UPDATE accounts SET position = position + 1 WHERE account_id IN (:ids)")
    suspend fun increasePositionOfIds(vararg ids: Long)

    /**
     * Decreases the position of views.
     * Used when a Account is moved UP and these views have to move DOWN in the list.
     *
     * @param ids: An array of ID's of accounts that have to be updated.
     */
    @Query("UPDATE accounts SET position = position - 1 WHERE account_id IN (:ids)")
    suspend fun decreasePositionOfIds(vararg ids: Long)

    @Query("UPDATE accounts SET position = position + 1 WHERE position > :startPos AND position < :endPos")
    suspend fun increasePositions(startPos: Int, endPos: Int)

    @Query("UPDATE accounts SET position = position - 1 WHERE position > :startPos AND position < :endPos")
    suspend fun decreasePositions(startPos: Int, endPos: Int)
}