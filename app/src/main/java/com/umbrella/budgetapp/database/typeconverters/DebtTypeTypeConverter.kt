package com.umbrella.budgetapp.database.typeconverters

import androidx.room.TypeConverter
import com.umbrella.budgetapp.enums.DebtType

class DebtTypeTypeConverter {
    @TypeConverter
    fun fromDebtType(debtType: DebtType): Int {
        return debtType.ordinal
    }

    @TypeConverter
    fun toDebtType(value: Int = 0): DebtType {
        return DebtType.values()[value]
    }
}