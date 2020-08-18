package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.Debt
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedDebt
import com.umbrella.budgetapp.database.collections.subcollections.SumFunction
import com.umbrella.budgetapp.database.repositories.DebtRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DebtViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = DebtRepository()

    fun getDebtById(id: Long) = repos.getDebtById(id)

    fun getAllDebts(type: Int) : LiveData<List<ExtendedDebt>> = repos.getAllDebts(type).asLiveData()

    fun getFunctionDebt() : LiveData<Array<SumFunction>> = repos.getFunctionDebt().asLiveData()

    fun addDebt(debt: Debt) = viewModelScope.launch(Dispatchers.IO) { repos.addDebt(debt) }

    fun updateDebt(debt: Debt) = viewModelScope.launch(Dispatchers.IO) { repos.updateDebt(debt) }

    fun removeDebt(debt: Debt) = viewModelScope.launch(Dispatchers.IO) { repos.removeDebt(debt) }
}