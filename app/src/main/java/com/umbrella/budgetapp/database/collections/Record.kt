package com.umbrella.budgetapp.database.collections

import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.math.BigDecimal

@Entity(tableName = "records")
data class Record(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "record_id")
        val id: Long,

        @ColumnInfo(name = "description")
        var description: String? = "",

        @ColumnInfo(name = "payee")
        var payee: String? = "",

        @Relation(parentColumn = "category_id", entityColumn = "category_ref")
        @ColumnInfo(name = "category_ref")
        var category: Category? = null,

        @Relation(parentColumn = "account_id", entityColumn = "account_ref")
        @ColumnInfo(name = "account_ref")
        var account: Account? = null,

        @Relation(parentColumn = "store_id", entityColumn = "store_ref")
        @ColumnInfo(name = "store_ref")
        var store: Store? = null,

        @Relation(parentColumn = "currency_id", entityColumn = "currency_ref")
        @ColumnInfo(name = "currency_ref")
        var currency: Currency? = null,

        @ColumnInfo(name = "type")
        var type: Int? = 0,

        @ColumnInfo(name = "payment_type")
        var paymentType: Int? = 0,

        @ColumnInfo(name = "amount")
        var amount: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "timestamp")
        var timestamp: Long? = 0,

        @ColumnInfo(name = "location")
        var location: Location? = null
)