package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.annotation.IntRange
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedAccount
import com.umbrella.budgetapp.database.repositories.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = AccountRepository()

    fun getAccountById(id: Long) = repos.getAccountById(id)

    fun getAccountBasics() : LiveData<List<Account>> = repos.getAccountBasics().asLiveData()

    fun getAllAccountsSmall(@IntRange(from = 1) limit: Int) : LiveData<List<ExtendedAccount>> = repos.getAllAccountsSmall(limit).asLiveData()

    fun getAllAccounts() : LiveData<List<ExtendedAccount>> = repos.getAllAccounts().asLiveData()

    fun changePosition(id: Long, position: Int) = repos.changePosition(id, position)

    fun increasePositionOfIds(vararg ids: Long) = viewModelScope.launch(Dispatchers.IO) { repos.increasePositionOfIds(*ids) }

    fun decreasePositionOfIds(vararg ids: Long) = viewModelScope.launch(Dispatchers.IO) { repos.decreasePositionOfIds(*ids) }

    fun addAccount(account: Account) = viewModelScope.launch(Dispatchers.IO) { repos.addAccount(account) }

    fun updateAccount(account: Account) = viewModelScope.launch(Dispatchers.IO) { repos.updateAccount(account) }

    fun removeAccount(account: Account) = viewModelScope.launch(Dispatchers.IO) { repos.removeAccount(account) }
}