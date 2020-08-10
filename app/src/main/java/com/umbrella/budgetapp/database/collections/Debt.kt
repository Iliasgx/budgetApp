package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.umbrella.budgetapp.database.typeconverters.DebtTypeTypeConverter
import com.umbrella.budgetapp.enums.DebtType
import java.math.BigDecimal

@Entity(tableName = "debts")
@TypeConverters(DebtTypeTypeConverter::class)
data class Debt(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "debt_id")
        val id: Long?,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @ColumnInfo(name = "description")
        var description: String? = "",

        @ColumnInfo(name = "debt_type")
        var debtType: DebtType? = DebtType.LENT,

        @ColumnInfo(name = "amount")
        var amount: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "category_ref")
        var categoryRef: Long? = 0,

        @ColumnInfo(name = "account_ref")
        var accountRef: Long? = 0,

        @ColumnInfo(name = "currency_ref")
        var currencyRef: Long? = 0,

        @ColumnInfo(name = "timestamp")
        var timestamp: Long? = 0
)
