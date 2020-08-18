package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.umbrella.budgetapp.database.typeconverters.GoalStatusTypeConverter
import com.umbrella.budgetapp.enums.GoalStatus
import java.math.BigDecimal
import java.util.*

@Entity(tableName = "goals")
@TypeConverters(GoalStatusTypeConverter::class)
data class Goal(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "goal_id")
        val id: Long?,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @ColumnInfo(name = "note")
        var note: String? = "",

        @ColumnInfo(name = "status")
        var status: GoalStatus? = GoalStatus.ACTIVE,

        @ColumnInfo(name = "color")
        var color: Int? = 0,

        @ColumnInfo(name = "icon")
        var icon: Int? = 0,

        @ColumnInfo(name = "target_amount")
        var targetAmount: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "saved_amount")
        var savedAmount: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "last_amount")
        var lastAmount: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "desired_date")
        var desiredDate: Long? = 0,

        @ColumnInfo(name = "start_date")
        var startDate: Long? = Calendar.getInstance().timeInMillis
)