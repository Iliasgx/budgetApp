package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.Country
import com.umbrella.budgetapp.database.repositories.CountryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CountryViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = CountryRepository()

    fun getCountryById(id: Long) = repos.getCountryById(id)

    fun getAllCountries() : LiveData<List<Country>> = repos.getAllCountries().asLiveData()

    fun addCountry(country: Country) = viewModelScope.launch(Dispatchers.IO) { repos.addCountry(country) }
}