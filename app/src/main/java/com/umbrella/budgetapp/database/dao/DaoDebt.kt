package com.umbrella.budgetapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.umbrella.budgetapp.database.collections.Debt
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedDebt
import com.umbrella.budgetapp.database.collections.subcollections.SumFunction
import com.umbrella.budgetapp.enums.DebtType
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoDebt : Base<Debt> {

    /**
     * Retrieves all debts with crossReferences.
     *
     * @param type: The DebtType of which debts should be showed.
     * @return The list of debts in a Flow.
     *
     * @see DebtType
     */
    @Transaction
    @Query("SELECT debt_id, name, debt_type, amount, extended_currency_name, timestamp FROM debt_cross WHERE debt_type = :type ORDER BY timestamp ASC")
    fun getAllDebts(type: Int) : Flow<List<ExtendedDebt>>

    /**
     * Get the sum of amount in debts ordered by debtType.
     *
     * @return An array of the sum for each debtType.
     */
    @Query("SELECT SUM(amount) AS total FROM debts GROUP BY debt_type")
    fun getFunctionDebt() : Flow<Array<SumFunction>>

    /**
     * Find Debt by ID.
     *
     * @param id: The ID corresponding with the Debt.
     * @return The Debt represented by the ID with crossReferences.
     */
    @Transaction
    @Query("SELECT * FROM debt_cross WHERE debt_id = :id")
    fun getDebtById(id: Long) : ExtendedDebt

}