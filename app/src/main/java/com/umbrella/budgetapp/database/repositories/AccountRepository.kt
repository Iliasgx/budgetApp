package com.umbrella.budgetapp.database.repositories

import androidx.annotation.IntRange
import com.umbrella.budgetapp.cache.App
import com.umbrella.budgetapp.database.BudgetDatabase
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedAccount
import com.umbrella.budgetapp.database.dao.DaoAccount
import kotlinx.coroutines.flow.Flow

class AccountRepository {

    private val daoAccount: DaoAccount

    init {
        val db = BudgetDatabase.getDatabase(App.app!!)
        daoAccount = db.daoAccount()
    }

    fun getAccountBasics() : Flow<List<Account>> = daoAccount.getAccountBasics()

    fun getAllAccountsSmall(@IntRange(from = 1) limit: Int) : Flow<List<ExtendedAccount>> = daoAccount.getAllAccountsSmall(limit)

    fun getAllAccounts() : Flow<List<ExtendedAccount>> = daoAccount.getAllAccounts()

    fun getAccountById(id: Long) : ExtendedAccount = daoAccount.getAccountById(id)
    
    fun changePosition(id: Long, position: Int) = daoAccount.changePosition(id, position)

    suspend fun increasePositionOfIds(vararg ids: Long) = daoAccount.increasePositionOfIds(*ids)

    suspend fun decreasePositionOfIds(vararg ids: Long) = daoAccount.decreasePositionOfIds(*ids)

    suspend fun addAccount(account: Account) = daoAccount.add(account)

    suspend fun updateAccount(account: Account) = daoAccount.update(account)

    suspend fun removeAccount(account: Account) = daoAccount.remove(account)
}