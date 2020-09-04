package com.umbrella.budgetapp.database.dao

import androidx.annotation.IntRange
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.umbrella.budgetapp.database.collections.Record
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedRecord
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoRecord : Base<Record> {
    // TODO: Create RawQuery for altering query at runtime for FILTER options

    /**
     * Retrieves all records with crossReferences.
     *
     * @return The list of records in a Flow.
     */
    @Transaction
    @Query("SELECT record_id, name, icon, color, extended_account_name, type, amount, timestamp, description, extended_currency_symbol FROM record_cross ORDER BY timestamp DESC")
    fun getAllRecords() : Flow<List<ExtendedRecord>>

    /**
     * Retrieves all records with crossReferences.
     *
     * @param accountIds: An array of account ID's to sort with.
     * @return The list of records in a Flow.
     */
    @Transaction
    @Query("SELECT record_id, name, icon, color, extended_account_name, type, amount, timestamp, description, extended_currency_symbol FROM record_cross WHERE account_ref IN (:accountIds) ORDER BY timestamp DESC")
    fun getAllRecordsOfAccounts(vararg accountIds: Long) : Flow<List<ExtendedRecord>>

    /**
     * Retrieves all records with crossReferences.
     *
     * @param accountIds: An array of account ID's to sort with.
     * @param limit: Limit number of records.
     * @param offset: Start x records further, in combination with [limit] and Pagination
     *
     * @return The list of records in a Flow.
     */
    @Transaction
    @Query("SELECT record_id, name, icon, color, extended_account_name, type, amount, timestamp, description, extended_currency_symbol FROM record_cross WHERE account_ref IN (:accountIds) ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    fun getAllRecordsOfAccounts(vararg accountIds: Long, @IntRange(from = 1) limit: Int, offset: Int = 0) : Flow<List<ExtendedRecord>>

    /**
     * Find Record by ID with crossReferences.
     *
     * @param id: The ID corresponding with the Record.
     * @return The Record represented by the ID with crossReferences.
     */
    @Transaction
    @Query("SELECT * FROM record_cross WHERE record_id = :id")
    fun getRecordById(id: Long) : ExtendedRecord

}