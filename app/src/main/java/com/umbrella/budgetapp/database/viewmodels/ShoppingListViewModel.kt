package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.ShoppingList
import com.umbrella.budgetapp.database.repositories.ShoppingListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = ShoppingListRepository()

    fun getShoppingListById(id: Long) = repos.getShoppingListById(id).asLiveData()

    fun getExtendedShoppingListById(id: Long) = repos.getExtendedShoppingListById(id)

    fun getAllShoppingLists() = repos.getAllShoppingLists().asLiveData()

    fun getAllShoppingLists(limit: Int) = repos.getAllShoppingLists(limit).asLiveData()

    fun addShoppingList(shoppingList: ShoppingList) = viewModelScope.launch(Dispatchers.IO) { repos.addShoppingList(shoppingList) }

    fun updateShoppingList(shoppingList: ShoppingList) = viewModelScope.launch(Dispatchers.IO) { repos.updateShoppingList(shoppingList) }

    fun removeShoppingList(shoppingList: ShoppingList) = viewModelScope.launch(Dispatchers.IO) { repos.removeShoppingList(shoppingList) }
}