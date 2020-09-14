package com.umbrella.budgetapp.database.collections

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "stores")
data class Store(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "store_id")
        val id: Long? = null,

        @ColumnInfo(name = "store_name")
        var name: String? = "",

        @ColumnInfo(name = "store_note")
        var note: String? = "",

        @ColumnInfo(name = "store_currency_ref")
        var currencyRef: Long? = 0,

        @ColumnInfo(name  ="store_category_ref")
        var categoryRef: Long? = 0
) : Parcelable
