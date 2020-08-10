package com.umbrella.budgetapp.database.collections

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class ShoppingListItem (
        var position: Int = 0,
        var name: String = "",
        var number: Int = 1,
        var amount: BigDecimal = BigDecimal.ZERO,
        var checked: Boolean = false
) : Parcelable