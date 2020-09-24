package com.umbrella.budgetapp.database.repositories

import androidx.annotation.IntRange
import com.umbrella.budgetapp.cache.App
import com.umbrella.budgetapp.database.BudgetDatabase
import com.umbrella.budgetapp.database.collections.Record
import com.umbrella.budgetapp.database.dao.DaoRecord

class RecordRepository {

    private val daoRecord: DaoRecord

    init {
        val db = BudgetDatabase.getDatabase(App.app!!)
        daoRecord = db.daoRecord()
    }

    fun getAllRecords() = daoRecord.getAllRecords()

    fun getAllRecordsOfAccounts(vararg accountIds: Long) = daoRecord.getAllRecordsOfAccounts(*accountIds)

    fun getAllRecordsOfAccounts(vararg accountIds: Long, @IntRange(from = 1) limit: Int, startDate: Long, endDate: Long) = daoRecord.getAllRecordsOfAccounts(accountIds = *accountIds, limit = limit, startDate = startDate, endDate = endDate)

    fun getAllRecordsOfAccounts(vararg accountIds: Long, @IntRange(from = 1) limit: Int, offset: Int) = daoRecord.getAllRecordsOfAccounts(accountIds = *accountIds, limit = limit, offset = offset)

    fun getRecordById(id: Long) = daoRecord.getRecordById(id)

    suspend fun addRecord(record: Record) = daoRecord.add(record)

    suspend fun updateRecord(record: Record) = daoRecord.update(record)

    suspend fun removeRecord(record: Record) = daoRecord.remove(record)
}