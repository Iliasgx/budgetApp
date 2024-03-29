package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.Record
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedRecord
import com.umbrella.budgetapp.database.repositories.RecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = RecordRepository()

    fun getRecordById(id: Long) = repos.getRecordById(id).asLiveData()

    fun getAllRecords() : LiveData<List<ExtendedRecord>> = repos.getAllRecords().asLiveData()

    fun addRecord(record: Record) = viewModelScope.launch(Dispatchers.IO) { repos.addRecord(record) }

    fun updateRecord(record: Record) = viewModelScope.launch(Dispatchers.IO) { repos.updateRecord(record) }

    fun removeRecord(record: Record) = viewModelScope.launch(Dispatchers.IO) { repos.removeRecord(record) }
}