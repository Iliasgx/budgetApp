package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.Template
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedTemplate
import com.umbrella.budgetapp.database.repositories.TemplateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TemplateViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = TemplateRepository()

    fun getTemplateById(id: Long) = repos.getTemplateById(id)

    fun getAllTemplates() : LiveData<List<ExtendedTemplate>> = repos.getAllTemplates().asLiveData()

    fun changePosition(id: Long, position: Int) = repos.changePosition(id, position)

    fun increasePositionOfIds(vararg ids: Long) = viewModelScope.launch(Dispatchers.IO) { repos.increasePositionOfIds(*ids) }

    fun decreasePositionOfIds(vararg ids: Long) = viewModelScope.launch(Dispatchers.IO) { repos.decreasePositionOfIds(*ids) }

    fun addTemplate(template: Template) = viewModelScope.launch(Dispatchers.IO) { repos.addTemplate(template) }

    fun updateTemplate(template: Template) = viewModelScope.launch(Dispatchers.IO) { repos.updateTemplate(template) }

    fun removeTemplate(template: Template) = viewModelScope.launch(Dispatchers.IO) { repos.removeTemplate(template) }
}