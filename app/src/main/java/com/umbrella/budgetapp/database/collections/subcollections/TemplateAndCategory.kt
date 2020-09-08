package com.umbrella.budgetapp.database.collections.subcollections

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Template

@DatabaseView(
        viewName = "template_cross_category",
        value = "SELECT * FROM templates INNER JOIN categories ON templates.category_ref = categories.category_id"
)
data class TemplateAndCategory (
        @Embedded
        val template: Template,

        @Embedded(prefix = "cat_")
        val category: Category
)