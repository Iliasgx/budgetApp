package com.umbrella.budgetapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import com.umbrella.budgetapp.database.collections.Goal
import com.umbrella.budgetapp.enums.GoalStatus
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoGoal : Base<Goal> {

    /**
     * Retrieves all goals. {Active, Paused}
     *
     * @param status: The status of which goals should be showed.
     * @return The list of active or paused goals in a Flow.
     *
     * @see GoalStatus
     */
    @Query("SELECT goal_id, name, status, color, icon, target_amount, saved_amount FROM goals WHERE status = :status ORDER BY desired_date ASC")
    fun getAllGoalsUnreached(status: Int) : Flow<List<Goal>>

    /**
     * Retrieves all goals. {Reached}
     *
     * @return The list of reached goals in a Flow.
     */
    @Query("SELECT goal_id, name, color, icon, saved_amount, desired_date FROM goals ORDER BY desired_date ASC")
    fun getAllGoalsReached() : Flow<List<Goal>>

    /**
     * Find Goal by ID.
     *
     * @param id: The ID corresponding with the Goal.
     * @return The Goal represented by the ID.
     */
    @Query("SELECT * FROM goals WHERE goal_id = :id")
    fun getGoalById(id: Long) : Goal

}