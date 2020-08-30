package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.PlannedPayment

@DatabaseView(
        viewName = "planned_payment_cross_small",
        value = "SELECT * FROM planned_payments INNER JOIN categories ON planned_payments.category_ref = categories.category_id"
)
data class ExtendedPayments(
        @Embedded
        val plannedPayment: PlannedPayment,

        @Embedded
        val category: Category
)