package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.Store
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedStore
import com.umbrella.budgetapp.database.repositories.StoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = StoreRepository()

    fun getStoreById(id: Long) = repos.getStoreById(id).asLiveData()

    fun getAllStores() : LiveData<List<ExtendedStore>> = repos.getAllStores().asLiveData()

    fun addStore(store: Store) = viewModelScope.launch(Dispatchers.IO) { repos.addStore(store) }

    fun updateStore(store: Store) = viewModelScope.launch(Dispatchers.IO) { repos.updateStore(store) }

    fun removeStore(store: Store) = viewModelScope.launch(Dispatchers.IO) { repos.removeStore(store) }
}