package com.umbrella.budgetapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import com.umbrella.budgetapp.database.collections.User
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoUser : Base<User> {

    /**
     * Find User by ID.
     *
     * @param id: The ID corresponding with the User.
     * @return The User represented by the ID.
     */
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserById(id: Long) : Flow<User>
}