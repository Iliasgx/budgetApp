package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.math.BigDecimal

@Entity(tableName = "templates")
data class Template(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "template_id")
        val id: Long,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @Relation(parentColumn = "account_id", entityColumn = "account_ref")
        @ColumnInfo(name = "account_ref")
        var account: Account? = null,

        @Relation(parentColumn = "category_id", entityColumn = "category_ref")
        @ColumnInfo(name = "category_ref")
        var category: Category? = null,

        @Relation(parentColumn = "currency_id", entityColumn = "currency_ref")
        @ColumnInfo(name = "currency_ref")
        var currency: Currency? = null,

        @Relation(parentColumn = "store_id", entityColumn = "store_ref")
        @ColumnInfo(name = "store")
        var store: Store? = null,

        @ColumnInfo(name = "payee")
        var payee: String? = "",

        @ColumnInfo(name = "note")
        var note: String? = "",

        @ColumnInfo(name = "type")
        var type: Int? = 0,

        @ColumnInfo(name = "payment_type")
        var paymentType: Int? = 0,

        @ColumnInfo(name = "position")
        var position: Int? = 0,

        @ColumnInfo(name = "amount")
        var amount: BigDecimal? = BigDecimal.ZERO
)
