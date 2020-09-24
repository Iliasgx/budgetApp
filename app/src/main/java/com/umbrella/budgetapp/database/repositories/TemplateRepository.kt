package com.umbrella.budgetapp.database.repositories

import androidx.annotation.IntRange
import com.umbrella.budgetapp.cache.App
import com.umbrella.budgetapp.database.BudgetDatabase
import com.umbrella.budgetapp.database.collections.Template
import com.umbrella.budgetapp.database.dao.DaoTemplate

class TemplateRepository {

    private val daoTemplate : DaoTemplate

    init {
        val db = BudgetDatabase.getDatabase(App.app!!)
        daoTemplate = db.daoTemplate()
    }

    fun getAllTemplates() = daoTemplate.getAllTemplates()

    fun getAllTemplates(@IntRange(from = 1) limit: Int) = daoTemplate.getAllTemplates(limit)

    fun getTemplateById(id: Long) = daoTemplate.getTemplateById(id)

    fun changePosition(id: Long, position: Int) = daoTemplate.changePosition(id, position)

    suspend fun increasePositionOfIds(vararg ids: Long) = daoTemplate.increasePositionOfIds(*ids)

    suspend fun decreasePositionOfIds(vararg ids: Long) = daoTemplate.decreasePositionOfIds(*ids)

    suspend fun increasePositions(startPos: Int, endPos: Int) = daoTemplate.increasePositions(startPos, endPos)

    suspend fun decreasePositions(startPos: Int, endPos: Int) = daoTemplate.decreasePositions(startPos, endPos)

    suspend fun addTemplate(template: Template) = daoTemplate.add(template)

    suspend fun updateTemplate(template: Template) = daoTemplate.update(template)

    suspend fun removeTemplate(template: Template) = daoTemplate.remove(template)
}