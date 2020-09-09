package com.umbrella.budgetapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.umbrella.budgetapp.database.collections.*
import com.umbrella.budgetapp.database.collections.subcollections.*
import com.umbrella.budgetapp.database.dao.*
import com.umbrella.budgetapp.database.typeconverters.BigDecimalTypeConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
        entities = [
            Account::class,
            Category::class,
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
            ExtendedTemplate::class,
            TemplateAndCategory::class,
            ExtendedStore::class,
            ExtendedShoppingList::class,
            ExtendedDebt::class,
            ExtendedPlannedPayment::class,
            ExtendedPayments::class,
            ExtendedRecord::class,
            ExtendedAccount::class
        ],
        version = 1,
        exportSchema = false)
@TypeConverters(
        BigDecimalTypeConverter::class
)
abstract class BudgetDatabase: RoomDatabase() {

    abstract fun daoAccount() : DaoAccount
    abstract fun daoCategory() : DaoCategory
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
                /*val instance = Room
                        .databaseBuilder(context.applicationContext, BudgetDatabase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()*/

                val instance = Room
                        .inMemoryDatabaseBuilder(context.applicationContext, BudgetDatabase::class.java)
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)

                            }
                        })
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class DatabaseCallBack(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {

                }
            }
        }
    }
}