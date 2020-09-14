package com.umbrella.budgetapp.database.collections

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
@Entity(tableName = "currencies")
data class Currency(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "currency_id")
        val id: Long? = null,

        @ColumnInfo(name = "currency_country_ref")
        val countryRef: Long? = 0,

        @ColumnInfo(name = "currency_used_rate")
        var usedRate: BigDecimal? = BigDecimal.ONE,

        @ColumnInfo(name = "currency_position")
        var position: Int? = 0
) : Parcelable
