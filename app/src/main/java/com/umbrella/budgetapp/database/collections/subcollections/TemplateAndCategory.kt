package com.umbrella.budgetapp.database.collections.subcollections

import android.os.Parcelable
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Template
import kotlinx.android.parcel.Parcelize

@Parcelize
@DatabaseView(
        viewName = "template_cross_category",
        value = "SELECT * FROM templates LEFT JOIN categories ON templates.template_category_ref = categories.category_id"
)
data class TemplateAndCategory (
        @Embedded
        val template: Template,

        @Embedded
        val category: Category
) : Parcelable