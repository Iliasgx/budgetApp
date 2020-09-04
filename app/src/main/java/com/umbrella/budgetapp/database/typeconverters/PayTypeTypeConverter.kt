package com.umbrella.budgetapp.database.typeconverters

import androidx.room.TypeConverter
import com.umbrella.budgetapp.enums.PayType

class PayTypeTypeConverter {

    @TypeConverter
    fun fromPayType(payType: PayType): Int {
        return payType.ordinal
    }

    @TypeConverter
    fun toPayType(value: Int = 0): PayType {
        return PayType.values()[value]
    }
}