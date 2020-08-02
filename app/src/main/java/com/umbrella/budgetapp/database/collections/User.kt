package com.umbrella.budgetapp.database.collections

import androidx.room.*

@Entity(tableName = "users")
data class User(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "user_id")
        val id: Long,

        @ColumnInfo(name = "first_name")
        var firstName: String? = "",

        @ColumnInfo(name = "last_name")
        var lastName: String? = "",

        @Ignore
        var fullName: String = "$firstName $lastName",

        @ColumnInfo(name = "email")
        var email: String? = "",

        @ColumnInfo(name = "birthday")
        var birtday: Long? = 0,

        @ColumnInfo(name = "gender")
        var gender: Int? = 0,

        @Relation(parentColumn = "category_id", entityColumn = "last_used_category_ref")
        @ColumnInfo(name = "last_used_category_ref")
        var lastUsedCategory: Category? = null,

        @Relation(parentColumn = "store_id", entityColumn = "last_used_store_ref")
        @ColumnInfo(name = "last_used_store_ref")
        var lastUsedStore: Store? = null,

        @ColumnInfo(name = "pref_home_cashflow_filter")
        var prefHomeCashFlowFilter: Int? = -1,

        @ColumnInfo(name = "pref_home_records_filter")
        var prefHomeRecordsFilter: Int? = -1,

        @ColumnInfo(name = "pref_records_filter")
        var prefRecordsFilter: Int? = -1,

        @ColumnInfo(name = "pref_planned_payments_sorting")
        var prefPlannedPaymentsSorting: Int? = -1,

        @ColumnInfo(name = "pref_statistics_filter")
        var prefStatisticsFilter: Int? = -1
)
