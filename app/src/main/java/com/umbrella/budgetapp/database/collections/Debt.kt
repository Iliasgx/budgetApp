package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.umbrella.budgetapp.enums.DebtType
import java.math.BigDecimal

@Entity(tableName = "debts")
data class Debt(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "debt_id")
        val id: Long,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @ColumnInfo(name = "description")
        var description: String? = "",

        @ColumnInfo(name = "debt_type")
        var debtType: DebtType? = DebtType.LENT,

        @ColumnInfo(name = "amount")
        var amount: BigDecimal? = BigDecimal.ZERO,

        @Relation(parentColumn = "category_id", entityColumn = "category_ref")
        @ColumnInfo(name = "category_ref")
        var category: Category? = null,

        @Relation(parentColumn = "account_id", entityColumn = "account_ref")
        @ColumnInfo(name = "account_ref")
        var account: Account? = null,

        @Relation(parentColumn = "currency_id", entityColumn = "currency_ref")
        @ColumnInfo(name = "currency_ref")
        var currency: Currency? = null,

        @ColumnInfo(name = "timestamp")
        var timestamp: Long? = 0
)
