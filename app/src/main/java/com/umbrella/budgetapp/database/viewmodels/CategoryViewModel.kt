package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.repositories.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = CategoryRepository()

    fun getCategoryById(id: Long) = repos.getCategoryById(id)

    fun getAllCategories() : LiveData<List<Category>> = repos.getAllCategories().asLiveData()

    fun addCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) { repos.addCategory(category)}

    fun updateCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) { repos.updateCategory(category) }

    fun removeCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) { repos.removeCategory(category) }
}