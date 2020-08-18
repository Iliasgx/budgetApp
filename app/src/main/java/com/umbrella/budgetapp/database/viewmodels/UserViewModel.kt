package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.User
import com.umbrella.budgetapp.database.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = UserRepository()

    fun getUserById(id: Long) = repos.getUserById(id)

    fun addUser(user: User) = viewModelScope.launch(Dispatchers.IO) { repos.addUser(user) }

    fun updateUser(user: User) = viewModelScope.launch(Dispatchers.IO) { repos.updateUser(user) }

}