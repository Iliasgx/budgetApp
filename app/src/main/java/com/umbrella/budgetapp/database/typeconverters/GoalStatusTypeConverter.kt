package com.umbrella.budgetapp.database.typeconverters

import androidx.room.TypeConverter
import com.umbrella.budgetapp.enums.GoalStatus

class GoalStatusTypeConverter {

    @TypeConverter
    fun fromGoalStatus(goalStatus: GoalStatus): Int {
        return goalStatus.ordinal
    }

    @TypeConverter
    fun toGoalStatus(value: Int = 0): GoalStatus {
        return GoalStatus.values()[value]
    }
}