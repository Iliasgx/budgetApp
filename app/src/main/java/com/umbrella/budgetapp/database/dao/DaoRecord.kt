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
    /**
     * Retrieves all records with crossReferences.
     *
     * @return The list of records in a Flow.
     */
    @Transaction
    @Query("SELECT record_id, category_name, category_icon, category_color, extended_account_name, record_type, record_amount, record_timestamp, record_description, extended_country_ref FROM record_cross ORDER BY record_timestamp DESC")
    fun getAllRecords() : Flow<List<ExtendedRecord>>

    /**
     * Retrieves all records.
     *
     * @param accountIds: An array of account ID's to sort with.
     * @return The list of records in a Flow.
     */
    @Query("SELECT record_amount, record_type, record_timestamp FROM records WHERE record_account_ref IN (:accountIds) ORDER BY record_timestamp DESC")
    fun getAllRecordsOfAccounts(vararg accountIds: Long) : Flow<List<Record>>

    /**
     * Retrieves all records with crossReferences.
     *
     * @param accountIds: An array of account ID's to sort with.
     * @param limit: Limit number of records.
     *
     * @return The list of records in a Flow.
     */
    @Transaction
    @Query("SELECT record_id, category_name, category_icon, category_color, extended_account_name, record_type, record_amount, record_timestamp, record_description, extended_country_ref FROM record_cross WHERE record_account_ref IN (:accountIds) AND record_timestamp BETWEEN :startDate AND :endDate ORDER BY record_timestamp DESC LIMIT :limit")
    fun getAllRecordsOfAccounts(vararg accountIds: Long, @IntRange(from = 1) limit: Int, startDate: Long, endDate: Long) : Flow<List<ExtendedRecord>>


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
    @Query("SELECT record_id, category_name, category_icon, category_color, extended_account_name, record_type, record_amount, record_timestamp, record_description, extended_country_ref FROM record_cross WHERE record_account_ref IN (:accountIds) ORDER BY record_timestamp DESC LIMIT :limit OFFSET :offset")
    fun getAllRecordsOfAccounts(vararg accountIds: Long, @IntRange(from = 1) limit: Int, offset: Int = 0) : Flow<List<ExtendedRecord>>

    /**
     * Retrieves all relevant information of records (with corssReferences) for the Statistics.
     *
     * @param startDate start timestamp of data needed.
     * @param endDate end timestamp of data needed.
     *
     * @return The list of records between the two timestamps.
     */
    @Transaction
    @Query("SELECT record_type, category_name, category_color, category_icon, extended_country_ref, record_timestamp, record_amount FROM record_cross WHERE record_timestamp BETWEEN :startDate AND :endDate")
    fun getStatisticalRecords(startDate: Long, endDate: Long) : Flow<List<ExtendedRecord>>

    /**
     * Find Record by ID with crossReferences.
     *
     * @param id: The ID corresponding with the Record.
     * @return The Record represented by the ID with crossReferences.
     */
    @Transaction
    @Query("SELECT * FROM record_cross WHERE record_id = :id")
    fun getRecordById(id: Long) : Flow<ExtendedRecord>

}