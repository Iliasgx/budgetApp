package com.umbrella.budgetapp.database.collections.subcollections

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Store
import kotlinx.android.parcel.Parcelize

@Parcelize
@DatabaseView(
        viewName = "store_cross",
        value = """SELECT stores.*, categories.*,
                currencies.currency_id AS extended_currency_id, currencies.currency_position AS extended_currency_position, currencies.currency_country_ref AS extended_country_ref 
                FROM stores
                INNER JOIN currencies ON stores.store_currency_ref = currencies.currency_id 
                INNER JOIN categories ON stores.store_category_ref = categories.category_id"""
)
data class ExtendedStore (
        @Embedded
        val store: Store,

        @Embedded
        val category: Category?,

        @ColumnInfo(name = "extended_currency_id")
        val currencyId: Long?,

        @ColumnInfo(name = "extended_country_ref")
        val countryRef: Long?,

        @ColumnInfo(name = "extended_currency_position")
        val currencyPosition: Int?
) : Parcelable