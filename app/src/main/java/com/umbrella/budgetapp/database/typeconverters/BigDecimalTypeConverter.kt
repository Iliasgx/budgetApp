package com.umbrella.budgetapp.database.typeconverters

import androidx.room.TypeConverter
import java.math.BigDecimal

class BigDecimalTypeConverter {

    companion object {
        @TypeConverter
        @JvmStatic
        fun fromBigDecimal(value: BigDecimal): String {
            return value.toString()
        }

        @TypeConverter
        @JvmStatic
        fun toBigDecimal(value: String): BigDecimal {
            return value.toBigDecimal()
        }
    }
}