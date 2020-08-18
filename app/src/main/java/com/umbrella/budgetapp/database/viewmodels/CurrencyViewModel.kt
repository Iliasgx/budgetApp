package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.Currency
import com.umbrella.budgetapp.database.collections.subcollections.CurrencyAndName
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedCurrency
import com.umbrella.budgetapp.database.repositories.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrencyViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = CurrencyRepository()

    fun getCurrencyById(id: Long) = repos.getCurrencyById(id)

    fun getAllCurrencies() : LiveData<List<ExtendedCurrency>> = repos.getAllCurrencies().asLiveData()

    fun getBasicCurrencies() : LiveData<List<CurrencyAndName>> = repos.getBasicCurrencies().asLiveData()

    fun changePosition(id: Long, position: Int) = repos.changePosition(id, position)

    fun increasePositionOfIds(vararg ids: Long) = viewModelScope.launch(Dispatchers.IO) { repos.increasePositionOfIds(*ids) }

    fun decreasePositionOfIds(vararg ids: Long) = viewModelScope.launch(Dispatchers.IO) { repos.decreasePositionOfIds(*ids) }

    fun addCurrency(currency: Currency) = viewModelScope.launch(Dispatchers.IO) { repos.addCurrency(currency) }

    fun updateCurrency(currency: Currency) = viewModelScope.launch(Dispatchers.IO) { repos.updateCurrency(currency) }

    fun removeCurrency(currency: Currency) = viewModelScope.launch(Dispatchers.IO) { repos.removeCurrency(currency) }
}