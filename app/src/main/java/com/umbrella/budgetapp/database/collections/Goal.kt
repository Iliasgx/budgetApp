package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.umbrella.budgetapp.enums.GoalStatus
import java.math.BigDecimal
import java.util.*

@Entity(tableName = "goals")
data class Goal(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "goal_id")
        val id: Long? = null,

        @ColumnInfo(name = "goal_name")
        var name: String? = "",

        @ColumnInfo(name = "goal_note")
        var note: String? = "",

        @ColumnInfo(name = "goal_status")
        var status: GoalStatus? = GoalStatus.ACTIVE,

        @ColumnInfo(name = "goal_color")
        var color: Int? = 0,

        @ColumnInfo(name = "goal_icon")
        var icon: Int? = 0,

        @ColumnInfo(name = "goal_target_amount")
        var targetAmount: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "goal_saved_amount")
        var savedAmount: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "goal_last_amount")
        var lastAmount: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "goal_desired_date")
        var desiredDate: Long? = 0,

        @ColumnInfo(name = "goal_start_date")
        var startDate: Long? = Calendar.getInstance().timeInMillis
)