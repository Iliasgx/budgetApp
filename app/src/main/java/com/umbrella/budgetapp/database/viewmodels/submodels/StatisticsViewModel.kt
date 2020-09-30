package com.umbrella.budgetapp.database.viewmodels.submodels

import android.app.Application
import androidx.lifecycle.*
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedRecord
import com.umbrella.budgetapp.database.defaults.DefaultCountries
import com.umbrella.budgetapp.database.repositories.AccountRepository
import com.umbrella.budgetapp.database.repositories.RecordRepository
import com.umbrella.budgetapp.extensions.DoubleTrigger
import com.umbrella.budgetapp.extensions.sumByBigDecimal
import com.umbrella.budgetapp.ui.dialogs.FilterDialog.Filter
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val accountRepository = AccountRepository()
    private val recordRepository = RecordRepository()

    var filter = MutableLiveData<Filter>()

    private val accounts = accountRepository.getStatisticalAccounts().asLiveData().distinctUntilChanged()

    private val records : LiveData<List<ExtendedRecord>> = filter.switchMap {
        recordRepository.getStatisticalRecords(it.current().second, it.current().third).asLiveData()
    }

    private val previousRecords = filter.switchMap {
        recordRepository.getStatisticalRecords(it.previous().second, it.previous().third).asLiveData()
    }

    val balanceAccounts : LiveData<List<Account>> = accounts.switchMap {
        liveData {
            emit(it.sortedByDescending { account -> account.currentValue!! })
        }
    }

    val balanceCurrencies : LiveData<List<Triple<String, String, BigDecimal>>> = records.switchMap {
        liveData {
            val tempList = mutableListOf<Triple<String, String, BigDecimal>>()
            val countries = DefaultCountries()

            it.sortedBy { extRecord -> extRecord.category.id }
                .groupBy { extRecord -> extRecord.currencyId }
                .values.forEach {
                    val currency = countries.getCountryById(it.first().countryRef!!)

                    tempList.add(Triple(
                            currency.name,
                            currency.symbol,
                            it.sumByBigDecimal { extRecord -> extRecord.record.amount!! }
                    ))
                }

            emit(tempList.toList())
        }
    }

    val cashflowTotal : LiveData<Pair<BigDecimal, BigDecimal>> = records.switchMap {
        liveData {
            emit(Pair(
                it.filter { extRecord -> extRecord.record.type == 0 }.sumByBigDecimal { extRecord -> extRecord.record.amount!! },
                it.filter { extRecord -> extRecord.record.type == 1 }.sumByBigDecimal { extRecord -> extRecord.record.amount!! }
            ))
        }
    }

    val cashFlowTable : LiveData<Map<String, Pair<BigDecimal, BigDecimal>>> = records.switchMap {
        liveData {
            val incomeRcd = it.filter { extRecord -> extRecord.record.type == 0 }
            val expenseRcd = it.filter { extRecord -> extRecord.record.type == 1 }

            val sumIncome = incomeRcd.sumByBigDecimal { extRecord -> extRecord.record.amount!! }
            val sumExpense = expenseRcd.sumByBigDecimal { extRecord -> extRecord.record.amount!! }

            val cal = GregorianCalendar.getInstance()
            val avgDayIncomeDays = incomeRcd.groupBy { extRecord ->
                cal.timeInMillis = extRecord.record.timestamp!!
                cal.get(Calendar.DAY_OF_YEAR)
            }.count()
            val avgDayExpenseDays = expenseRcd.groupBy { extRecord ->
                cal.timeInMillis = extRecord.record.timestamp!!
                cal.get(Calendar.DAY_OF_YEAR)
            }.count()

            emit(mapOf(
                    Pair("count", Pair(incomeRcd.count().toBigDecimal(), expenseRcd.count().toBigDecimal())),
                    Pair("avgDay", Pair(sumIncome.divide(avgDayIncomeDays), sumExpense.divide(avgDayExpenseDays))),
                    Pair("avgRecord", Pair(sumIncome.divide(incomeRcd.count()), sumExpense.divide(expenseRcd.count()))),
                    Pair("total", Pair(sumIncome, sumExpense))
            ))
        }
    }

    val topExpenses = records.switchMap {
        liveData { emit(it.sortedByDescending { extRecord -> extRecord.record.amount }.subList(0,4)) }
    }

    val topCategories : LiveData<List<Triple<String, BigDecimal, Int>>> = records.switchMap {
        liveData {
            emit(it
                .groupBy { extRecord -> extRecord.category }
                .map { (category, list) ->
                    Triple(
                        category.name!!,
                        list.sumByBigDecimal { rcd -> rcd.record.amount!! },
                        category.color!!
                    )
                }
            )
        }
    }

    val categoryBook : LiveData<List<Triple<Int, Category, Pair<BigDecimal, BigDecimal>>>> = DoubleTrigger(records, previousRecords).distinctUntilChanged().switchMap {
        liveData {
            val list = mutableListOf<Triple<Int, Category, Pair<BigDecimal, BigDecimal>>>()

            val current = it.first?.groupBy { ext -> ext.category }?.map { (category, records) ->
                Triple(
                        records.first().record.type!!,
                        category,
                        records.sumByBigDecimal { ext -> ext.record.amount!! }
                )
            }

            val previous = it.second?.groupBy { ext -> ext.category }?.map { (category, records) ->
                Triple(
                        records.first().record.type!!,
                        category,
                        records.sumByBigDecimal { ext -> ext.record.amount!! }
                )
            }

            current?.forEachIndexed { index, triple ->
                list.add(Triple(triple.first, triple.second, Pair(triple.third, previous?.get(index)?.third ?: BigDecimal.ZERO)))
            }

            // Sort by type {income, expense} and then by {sum}
            list.sortedWith(compareBy({it.first}, {it.third.first}))
            emit(list.toList())
        }
    }

    private fun BigDecimal.divide(divisor: Int) = divide(divisor.toBigDecimal(), 2, RoundingMode.HALF_UP)
}