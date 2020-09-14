package com.umbrella.budgetapp.database.collections

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
@Entity(tableName = "templates")
data class Template(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "template_id")
        val id: Long? = null,

        @ColumnInfo(name = "template_name")
        var name: String? = "",

        @ColumnInfo(name = "template_account_ref")
        var accountRef: Long? = 0,

        @ColumnInfo(name = "template_category_ref")
        var categoryRef: Long? = 0,

        @ColumnInfo(name = "template_currency_ref")
        var currencyRef: Long? = 0,

        @ColumnInfo(name = "template_store_ref")
        var storeRef: Long? = 0,

        @ColumnInfo(name = "template_payee")
        var payee: String? = "",

        @ColumnInfo(name = "template_note")
        var note: String? = "",

        @ColumnInfo(name = "template_type")
        var type: Int? = 0,

        @ColumnInfo(name = "template_payment_type")
        var paymentType: Int? = 0,

        @ColumnInfo(name = "template_position")
        var position: Int? = 0,

        @ColumnInfo(name = "template_amount")
        var amount: BigDecimal? = BigDecimal.ZERO
) : Parcelable
