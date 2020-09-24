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
     * Retrieves all goals.
     *
     * @param status: The status of which goals should be showed.
     * @return The list of goals in a Flow that have a specific status.
     *
     * @see GoalStatus
     */
    @Query("SELECT goal_id, goal_name, goal_status, goal_color, goal_icon, goal_target_amount, goal_saved_amount, goal_desired_date FROM goals WHERE goal_status = :status ORDER BY goal_desired_date ASC")
    fun getAllGoals(status: GoalStatus) : Flow<List<Goal>>

    /**
     * Find Goal by ID.
     *
     * @param id: The ID corresponding with the Goal.
     * @return The Goal represented by the ID.
     */
    @Query("SELECT * FROM goals WHERE goal_id = :id")
    fun getGoalById(id: Long) : Flow<Goal>

}