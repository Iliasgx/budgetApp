package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.annotation.IntRange
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.Template
import com.umbrella.budgetapp.database.repositories.TemplateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TemplateViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = TemplateRepository()

    fun getTemplateById(id: Long) = repos.getTemplateById(id).asLiveData()

    fun getAllTemplates()  = repos.getAllTemplates().asLiveData()

    fun getAllTemplates(@IntRange(from = 1) limit: Int) = repos.getAllTemplates(limit).asLiveData()

    fun changePosition(id: Long, position: Int) = repos.changePosition(id, position)

    fun increasePositionOfIds(vararg ids: Long) = viewModelScope.launch(Dispatchers.IO) { repos.increasePositionOfIds(*ids) }

    fun decreasePositionOfIds(vararg ids: Long) = viewModelScope.launch(Dispatchers.IO) { repos.decreasePositionOfIds(*ids) }

    fun increasePositions(startPos: Int, endPos: Int) = viewModelScope.launch(Dispatchers.IO) { repos.increasePositions(startPos, endPos) }

    fun decreasePositions(startPos: Int, endPos: Int) = viewModelScope.launch(Dispatchers.IO) { repos.decreasePositions(startPos, endPos) }

    fun addTemplate(template: Template) = viewModelScope.launch(Dispatchers.IO) { repos.addTemplate(template) }

    fun updateTemplate(template: Template) = viewModelScope.launch(Dispatchers.IO) { repos.updateTemplate(template) }

    fun removeTemplate(template: Template) = viewModelScope.launch(Dispatchers.IO) { repos.removeTemplate(template) }
}