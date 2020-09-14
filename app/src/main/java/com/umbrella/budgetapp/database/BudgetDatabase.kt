package com.umbrella.budgetapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.*
import com.umbrella.budgetapp.database.collections.subcollections.*
import com.umbrella.budgetapp.database.dao.*
import com.umbrella.budgetapp.database.defaults.DefaultCategories
import com.umbrella.budgetapp.database.defaults.DefaultCountries
import com.umbrella.budgetapp.database.typeconverters.BigDecimalTypeConverter
import com.umbrella.budgetapp.database.typeconverters.GoalStatusTypeConverter
import com.umbrella.budgetapp.enums.DebtType
import com.umbrella.budgetapp.enums.GoalStatus
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigDecimal

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
        BigDecimalTypeConverter::class,
        GoalStatusTypeConverter::class
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
                                GlobalScope.launch {
                                    INSTANCE?.daoCategory()?.add(*DefaultCategories(context).getCategories())

                                    val ctr = DefaultCountries().getCountryById(0L)
                                    INSTANCE?.daoCurrency()?.add(Currency(countryRef = ctr.id, usedRate = ctr.defaultRate))

                                    addTestData()
                                }
                            }
                        })
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                return instance
            }
        }

        private fun addTestData() {
            GlobalScope.launch {
                INSTANCE?.daoUser()?.add(
                        User(
                                firstName = "Ilias",
                                lastName = "g",
                                email = "iliasg@telenet.be",
                                birthday = 958483204000L
                        )
                )

                INSTANCE?.daoAccount()?.add(
                        Account(
                                name = "AccountA",
                                type = 0,
                                color = 3,
                                currentValue = BigDecimal(33),
                                position = 0,
                                currencyRef = 0L,
                                excludeStats = false
                        ),
                        Account(
                                name = "AccountB",
                                type = 1,
                                color = 7,
                                currentValue = BigDecimal(13),
                                position = 1,
                                currencyRef = 0L,
                                excludeStats = false
                        )
                )

                INSTANCE?.daoStore()?.add(
                        Store(
                                name = "storeA",
                                note = "ThisIsANoteee",
                                currencyRef = 1L,
                                categoryRef = 4L
                        )
                )

                INSTANCE?.daoTemplate()?.add(
                        Template(
                                name = "TemplateA",
                                accountRef = 0L,
                                categoryRef = 3L,
                                currencyRef = 0L,
                                storeRef = 0L,
                                payee = "Ilias",
                                note = "Helluw",
                                type = 0,
                                paymentType = 0,
                                position = 0,
                                amount = BigDecimal(83)
                        )
                )

                INSTANCE?.daoGoal()?.add(
                        Goal(
                                name = "Goal1",
                                note = "Videos",
                                status = GoalStatus.ACTIVE,
                                color = 3,
                                icon = R.drawable.google_icon,
                                targetAmount = BigDecimal(300),
                                savedAmount = BigDecimal(10),
                                lastAmount = BigDecimal(1),
                                desiredDate = System.nanoTime() + 30000,
                                startDate = System.nanoTime()
                        )
                )

                INSTANCE?.daoDebt()?.add(
                        Debt(
                                name = "DebtFirst",
                                description = "Hallo",
                                debtType = DebtType.LENT,
                                amount = BigDecimal(10),
                                categoryRef = 3L,
                                accountRef = 1L,
                                currencyRef = 1L,
                                timestamp = System.nanoTime()
                        )
                )

                INSTANCE?.daoShoppingList()?.add(
                        ShoppingList(
                                name = "MyFirst",
                                categoryRef = 3L,
                                storeRef = 1L,
                                items = mutableListOf(
                                        ShoppingListItem(0, "Appels", 3, BigDecimal(2.80), false),
                                        ShoppingListItem(1, "Peren", 1, BigDecimal(0), false),
                                        ShoppingListItem(2, "Konijn", 1, BigDecimal(6.20), true)
                                )
                        )
                )
            }
        }
    }
}
