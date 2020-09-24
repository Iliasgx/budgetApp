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
    @Query("SELECT account_id, account_name from accounts ORDER BY account_position ASC")
    fun getAccountBasics() : Flow<List<Account>>

    /**
     * Retrieves all accounts in a crossReference with a Currency.
     *
     * @param limit: Set a limit for displaying accounts.
     * @return The list of accounts in a Flow.
     */
    @Transaction
    @Query("SELECT account_id, account_name, account_color, account_current_value, account_initial_value, account_position, account_exclude_stats, extended_country_ref FROM account_cross ORDER BY account_position ASC LIMIT :limit")
    fun getAllAccountsSmall(@IntRange(from = 1) limit: Int) : Flow<List<ExtendedAccount>>

    /**
     * Retrieves all list information of accounts.
     *
     * @return The list of accounts in a Flow.
     */
    @Query("SELECT account_id, account_name, account_position, account_color, account_type, account_exclude_stats FROM accounts ORDER BY account_position ASC")
    fun getAllAccounts() : Flow<List<Account>>

    /**
     * Find Account by ID. Account with crossReferences.
     *
     * @param id: The id corresponding with the Account.
     * @return A crossReference of the account with the attached references.
     */
    @Transaction
    @Query("SELECT * FROM account_cross WHERE account_id = :id")
    fun getAccountById(id: Long) : Flow<ExtendedAccount>

    /**
     * Change the position of a single account.
     *
     * @param id: The id corresponding with the Account.
     * @param position: The position to where it moved.
     */
    @Query("UPDATE accounts SET account_position = :position WHERE account_id = :id")
    fun changePosition(id: Long, position: Int)

    /**
     * Increases the position of views.
     * Used when a Account is moved DOWN and these views have to move UP in the list.
     *
     * @param ids: An array of ID's of accounts that have to be updated.
     */
    @Query("UPDATE accounts SET account_position = account_position + 1 WHERE account_id IN (:ids)")
    suspend fun increasePositionOfIds(vararg ids: Long)

    /**
     * Decreases the position of views.
     * Used when a Account is moved UP and these views have to move DOWN in the list.
     *
     * @param ids: An array of ID's of accounts that have to be updated.
     */
    @Query("UPDATE accounts SET account_position = account_position - 1 WHERE account_id IN (:ids)")
    suspend fun decreasePositionOfIds(vararg ids: Long)

    @Query("UPDATE accounts SET account_position = account_position + 1 WHERE account_position > :startPos AND account_position < :endPos")
    suspend fun increasePositions(startPos: Int, endPos: Int)

    @Query("UPDATE accounts SET account_position = account_position - 1 WHERE account_position > :startPos AND account_position < :endPos")
    suspend fun decreasePositions(startPos: Int, endPos: Int)
}