package com.umbrella.budgetapp.database.viewmodels.submodels

import android.app.Application
import androidx.lifecycle.*
import com.umbrella.budgetapp.database.collections.Record
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedAccount
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedRecord
import com.umbrella.budgetapp.database.repositories.*
import java.math.BigDecimal
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val recordRepository = RecordRepository()
    private val accountRepository = AccountRepository()
    private val plannedPaymentRepository = PlannedPaymentRepository()
    private val debtsRepository = DebtRepository()
    private val shoppingListsRepository = ShoppingListRepository()

    val selectedAccounts = MutableLiveData<List<Long>>()

    val cashflowFilterPeriod = MutableLiveData<Pair<Long?, Long?>>()
    val cashflowPreviousFilterPeriod = MutableLiveData<Pair<Long?, Long?>>()

    val recordsFilterPeriod = MutableLiveData<Pair<Long?, Long?>>()

    val accounts : LiveData<List<ExtendedAccount>> = accountRepository.getAllAccountsSmall(4).asLiveData().distinctUntilChanged()

    val debts = debtsRepository.getFunctionDebt().asLiveData()

    val shoppingLists = shoppingListsRepository.getAllShoppingLists(3).asLiveData()

    val plannedPayments = plannedPaymentRepository.getAllPlannedPayments(1).asLiveData()

    val records : LiveData<List<Record>> = Transformations.switchMap(selectedAccounts) {
        recordRepository.getAllRecordsOfAccounts(*it.toLongArray()).asLiveData()
    }

    val topRecords : LiveData<List<ExtendedRecord>> = DoubleTrigger(selectedAccounts, recordsFilterPeriod).switchMap {
        recordRepository.getAllRecordsOfAccounts(
                accountIds = *it.first?.toLongArray() ?: longArrayOf(),
                limit = 5,
                startDate = it.second?.first ?: 0L,
                endDate = it.second?.second ?: Calendar.getInstance().timeInMillis)
                .asLiveData()
    }

    val balanceToday : LiveData<BigDecimal> = records.switchMap {
        liveData<BigDecimal> {
            emit(accounts.value?.filter { extAccount -> selectedAccounts.value?.contains(extAccount.account.id!!) ?: false }?.
                    sumByBigDecimal { acc -> acc.account.currentValue!! }?.add(
                        it.filter { record -> record.timestamp!! < Calendar.getInstance().timeInMillis }.
                            sumByBigDecimal { record -> record.amount!! })
                    ?: BigDecimal.ZERO
            )
        }
    }

    val balancePrevious : LiveData<BigDecimal> = records.switchMap {
        liveData<BigDecimal> {
            emit(accounts.value?.filter { extAccount -> selectedAccounts.value?.contains(extAccount.account.id!!) ?: false }?.
                    sumByBigDecimal { acc -> acc.account.currentValue!! }?.add(
                        it.filter { record -> record.timestamp!! < lastMonth }.
                            sumByBigDecimal { record -> record.amount!! })
                    ?: BigDecimal.ZERO
            )
        }
    }

    val cashflowCurrent : LiveData<Pair<BigDecimal, BigDecimal>> = DoubleTrigger(records, cashflowFilterPeriod).switchMap {
        val list = it.first?.
            filter { record -> it.second?.first?.let { begin -> record.timestamp!! >= begin } ?: run { record.timestamp!! != 0L } }?.
            filter { record -> it.second?.second?.let { end -> record.timestamp !! < end } ?: run { record.timestamp!! != 0L } }

        liveData {
            val first = list?.filter { record -> record.type == 0 }?.sumByBigDecimal { selection -> selection.amount!! }
            val second = list?.filter { record -> record.type == 1 }?.sumByBigDecimal { selection -> selection.amount!! }

            emit(Pair<BigDecimal, BigDecimal>(first ?: BigDecimal.ZERO, second ?: BigDecimal.ZERO))
        }
    }

    val cashflowPrevious : LiveData<BigDecimal> = DoubleTrigger(records, cashflowPreviousFilterPeriod).switchMap {
        liveData<BigDecimal> {
            emit(it.first?.
            filter { record -> it.second?.first?.let { begin -> record.timestamp!! >= begin } ?: run { record.timestamp!! != 0L } }?.
            filter { record -> it.second?.second?.let { end -> record.timestamp !! < end } ?: run { record.timestamp!! != 0L } }?.
            sumByBigDecimal { selection -> selection.amount!! } ?: BigDecimal.ZERO)
        }
    }

    private companion object {
        val lastMonth : Long = Calendar.getInstance().apply {
            set(Calendar.MONTH, get(Calendar.MONTH) - 1)
        }.timeInMillis
    }

    private inline fun <T> Iterable<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
        var sum = BigDecimal.ZERO
        for (element in this) {
            sum = sum.add(selector(element))
        }
        return sum
    }

    private class DoubleTrigger<A, B>(a: LiveData<A>, b: LiveData<B>) : MediatorLiveData<Pair<A?, B?>>() {
        init {
            addSource(a) { value = it to b.value }
            addSource(b) { value = a.value to it}
        }
    }
}