package com.umbrella.budgetapp.database.collections

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "categories")
data class Category(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "category_id")
        val id: Long? = null,

        @ColumnInfo(name = "category_name")
        var name: String? = "",

        @ColumnInfo(name = "category_icon")
        var icon: Int? = 0,

        @ColumnInfo(name = "category_color")
        var color: Int? = 0
) : Parcelable