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
        val id: Long? = null,

        @ColumnInfo(name = "debt_name")
        var name: String? = "",

        @ColumnInfo(name = "debt_description")
        var description: String? = "",

        @ColumnInfo(name = "debt_type")
        var debtType: DebtType? = DebtType.LENT,

        @ColumnInfo(name = "debt_amount")
        var amount: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "debt_category_ref")
        var categoryRef: Long? = 0,

        @ColumnInfo(name = "debt_account_ref")
        var accountRef: Long? = 0,

        @ColumnInfo(name = "debt_currency_ref")
        var currencyRef: Long? = 0,

        @ColumnInfo(name = "debt_timestamp")
        var timestamp: Long? = 0
)
