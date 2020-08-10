package com.umbrella.budgetapp.database.dao

import androidx.annotation.IntRange
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.umbrella.budgetapp.database.collections.PlannedPayment
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedPlannedPayment
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoPlannedPayment : Base<PlannedPayment> {

    // TODO: Sort by next due day, can't be done in query. Should do in Mapping the requested data
    /**
     * Retrieves all plannedPayments.
     *
     * @return The list of plannedPayments in a Flow.
     */
    @Query("SELECT planned_payments_id, name, payee, note, start_date, frequency, type, amount FROM planned_payments")
    fun getAllPlannedPayments() : Flow<List<PlannedPayment>>

    /**
     * Retrieves all plannedPayments.
     *
     * @param limit: Limit the number of items represented.
     * @return The list of plannedPayments in a Flow.
     */
    @Query("SELECT planned_payments_id, name, payee, note, start_date, frequency, type, amount FROM planned_payments LIMIT :limit")
    fun getAllPlannedPayments(@IntRange(from = 1) limit: Int) : Flow<List<PlannedPayment>>

    /**
     * Find PlannedPayment by ID with crossReferences.
     *
     * @param id: The ID corresponding with the PlannedPayment.
     * @return The Store represented by the ID with crossReferences.
     */
    @Transaction
    @Query("SELECT * FROM planned_payment_cross WHERE planned_payments_id = :id")
    fun getPlannedPaymentById(id: Long) : ExtendedPlannedPayment
}