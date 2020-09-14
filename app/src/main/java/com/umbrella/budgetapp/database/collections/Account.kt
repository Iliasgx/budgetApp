package com.umbrella.budgetapp.database.collections

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
@Entity(tableName = "accounts")
data class Account(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "account_id")
        val id: Long? = null,

        @ColumnInfo(name = "account_name")
        var name: String? = "",

        @ColumnInfo(name = "account_type")
        var type: Int? = 0,

        @ColumnInfo(name = "account_color")
        var color: Int? = 0,

        @ColumnInfo(name = "account_current_value")
        var currentValue: BigDecimal? = BigDecimal.ZERO,

        @ColumnInfo(name = "account_position")
        var position: Int? = 0,

        @ColumnInfo(name = "account_currency_ref")
        var currencyRef: Long? = 0,

        @ColumnInfo(name = "account_exclude_stats")
        var excludeStats: Boolean? = false
) : Parcelable
