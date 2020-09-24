package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.annotation.IntRange
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.PlannedPayment
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedPayments
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedPlannedPayment
import com.umbrella.budgetapp.database.repositories.PlannedPaymentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlannedPaymentViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = PlannedPaymentRepository()

    fun getPlannedPaymentById(id: Long) : LiveData<ExtendedPlannedPayment> =  repos.getPlannedPaymentById(id).asLiveData()

    fun getAllPlannedPayments() = repos.getAllPlannedPayments().asLiveData()

    fun getAllPlannedPayments(@IntRange(from = 1) limit: Int) : LiveData<List<ExtendedPayments>> = repos.getAllPlannedPayments(limit).asLiveData()

    fun addPlannedPayment(plannedPayment: PlannedPayment) = viewModelScope.launch(Dispatchers.IO) { repos.addPlannedPayment(plannedPayment) }

    fun updatePlannedPayment(plannedPayment: PlannedPayment) = viewModelScope.launch(Dispatchers.IO) { repos.updatePlannedPayment(plannedPayment) }

    fun removePlannedPayment(plannedPayment: PlannedPayment) = viewModelScope.launch(Dispatchers.IO) { repos.removePlannedPayment(plannedPayment) }
}