package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "category_id")
        val id: Long,

        @ColumnInfo(name = "name")
        var name: String? = "",

        @ColumnInfo(name = "icon")
        var icon: Int? = 0,

        @ColumnInfo(name = "color")
        var color: Int? = 0
)