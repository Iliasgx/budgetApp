package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "user_id")
        val id: Long? = null,

        @ColumnInfo(name = "user_first_name")
        var firstName: String = "",

        @ColumnInfo(name = "user_last_name")
        var lastName: String = "",

        @ColumnInfo(name = "user_email")
        var email: String = "",

        @ColumnInfo(name = "user_birthday")
        var birthday: Long = 0,

        @ColumnInfo(name = "user_gender")
        var gender: Int = 0
)
