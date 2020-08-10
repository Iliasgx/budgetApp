package com.umbrella.budgetapp.database.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "user_id")
        val id: Long,

        @ColumnInfo(name = "first_name")
        var firstName: String = "",

        @ColumnInfo(name = "last_name")
        var lastName: String = "",

        @ColumnInfo(name = "email")
        var email: String = "",

        @ColumnInfo(name = "birthday")
        var birthday: Long = 0,

        @ColumnInfo(name = "gender")
        var gender: Int = 0,

        @ColumnInfo(name = "last_used_category_ref")
        var lastUsedCategoryRef: Long? = 0,

        @ColumnInfo(name = "last_used_store_ref")
        var lastUsedStoreRef: Long? = 0,

        @ColumnInfo(name = "pref_home_cashflow_filter")
        var prefHomeCashFlowFilter: Int? = null,

        @ColumnInfo(name = "pref_home_records_filter")
        var prefHomeRecordsFilter: Int? = null,

        @ColumnInfo(name = "pref_records_filter")
        var prefRecordsFilter: Int? = null,

        @ColumnInfo(name = "pref_planned_payments_sorting")
        var prefPlannedPaymentsSorting: Int? = null,

        @ColumnInfo(name = "pref_statistics_filter")
        var prefStatisticsFilter: Int? = null
)
