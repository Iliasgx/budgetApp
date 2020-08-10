package com.umbrella.budgetapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.umbrella.budgetapp.database.collections.*
import com.umbrella.budgetapp.database.collections.subcollections.*
import com.umbrella.budgetapp.database.dao.*
import com.umbrella.budgetapp.database.typeconverters.BigDecimalTypeConverter

@Database(
        entities = [
            Account::class,
            Category::class,
            Country::class,
            Currency::class,
            Debt::class,
            Goal::class,
            PlannedPayment::class,
            Record::class,
            ShoppingList::class,
            Store::class,
            Template::class,
            User::class],
        views = [
            CurrencyAndName::class,
            ExtendedCurrency::class,
            ExtendedTemplate::class,
            ExtendedStore::class,
            ExtendedShoppingList::class,
            ExtendedDebt::class,
            ExtendedPlannedPayment::class,
            ExtendedRecord::class,
            ExtendedAccount::class
        ],
        version = 5,
        exportSchema = false)
@TypeConverters(
        BigDecimalTypeConverter::class
)
public abstract class BudgetDatabase: RoomDatabase() {

    abstract fun daoAccount() : DaoAccount
    abstract fun daoCategory() : DaoCategory
    abstract fun daoCountry() : DaoCountry
    abstract fun daoCurrency() : DaoCurrency
    abstract fun daoDebt() : DaoDebt
    abstract fun daoGoal() : DaoGoal
    abstract fun daoPlannedPayment(): DaoPlannedPayment
    abstract fun daoRecord() : DaoRecord
    abstract fun daoShoppingList() : DaoShoppingList
    abstract fun daoStore() : DaoStore
    abstract fun daoTemplate() : DaoTemplate
    abstract fun daoUser() : DaoUser

    companion object {
        private const val DATABASE_NAME: String = "budget_database"

        @Volatile
        private var INSTANCE: BudgetDatabase? = null

        fun getDatabase(context: Context): BudgetDatabase {
            val temp = INSTANCE

            if (temp != null) return temp

            synchronized(this) {
                val instance = Room
                        .databaseBuilder(context.applicationContext, BudgetDatabase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}