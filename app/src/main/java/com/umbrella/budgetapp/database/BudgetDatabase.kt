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
import com.umbrella.budgetapp.enums.PayType
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
                                lastName = "Goormans",
                                email = "iliasg@telenet.be",
                                birthday = 958428000000L, // 16/05/2000
                                gender = 0
                        )
                )

                INSTANCE?.daoAccount()?.add(
                        Account(
                                name = "ING",
                                type = 2,
                                color = 7,
                                initialValue = BigDecimal(1000),
                                currentValue = BigDecimal(720),
                                position = 0,
                                currencyRef = 1L,
                                excludeStats = false
                        ),
                        Account(
                                name = "KBC",
                                type = 4,
                                color = 2,
                                initialValue = BigDecimal(372.82),
                                currentValue = BigDecimal(73.20),
                                position = 1,
                                currencyRef = 1L,
                                excludeStats = false
                        )
                )

                INSTANCE?.daoCurrency()?.add(
                        Currency(
                                countryRef = 0L,
                                usedRate = BigDecimal.ONE,
                                position = 0
                        ),
                        Currency(
                                countryRef = 4L,
                                usedRate = BigDecimal(1.2734),
                                position = 1
                        )
                )

                INSTANCE?.daoDebt()?.add(
                        Debt(
                                name = "Margaux Louyest",
                                description = "payment of McDonalds",
                                debtType = DebtType.LENT,
                                amount = BigDecimal(13.47),
                                categoryRef = 3L,
                                accountRef = 1L,
                                currencyRef = 1L,
                                timestamp = 1584467002000L // 17/03/2020 18:43:22
                        ),
                        Debt(
                                name = "Lukas",
                                debtType = DebtType.BORROWED,
                                amount = BigDecimal(832),
                                categoryRef = 1L,
                                accountRef = 2L,
                                currencyRef = 2L,
                                timestamp = 1591874368000L // 11/06/2020 13:19:28
                        )
                )

                INSTANCE?.daoGoal()?.add(
                        Goal(
                                name = "Drivers license",
                                note = "Need it now :)",
                                status = GoalStatus.ACTIVE,
                                color = 3,
                                icon = R.drawable.car,
                                targetAmount = BigDecimal(1200),
                                savedAmount = BigDecimal(150),
                                lastAmount = BigDecimal.TEN,
                                desiredDate = 1613084400000L, // 12/02/2021
                                startDate = 1584486000000L // 18/03/2020
                        ),
                        Goal(
                                name = "No idea",
                                status = GoalStatus.PAUSED,
                                color = 5,
                                icon = R.drawable.diploma,
                                targetAmount = BigDecimal(45.22),
                                savedAmount = BigDecimal(1.93),
                                lastAmount = BigDecimal(1.10),
                                desiredDate = 1605567600000L, // 17/11/2020
                                startDate = 1597874400000L // 20/08/2020
                        )
                )

                INSTANCE?.daoPlannedPayment()?.add(
                        PlannedPayment(
                                name = "Phone bill",
                                payee = "Ilias",
                                accountRef = 2L,
                                categoryRef = 11L,
                                currencyRef = 1L,
                                startDate = 1581894000000L, // 17/02/2020
                                type = PayType.EXPENSE,
                                amount = BigDecimal(150)
                        ),
                        PlannedPayment(
                                name = "Income",
                                note = "First part",
                                accountRef = 1L,
                                categoryRef = 7L,
                                currencyRef = 2L,
                                startDate = 1560117600000L, // 10/06/2019
                                type = PayType.INCOME,
                                amount = BigDecimal(1603.22)
                        )
                )

                INSTANCE?.daoShoppingList()?.add(
                        ShoppingList(
                                name = "Lidl monday",
                                categoryRef = 2L,
                                storeRef = 3L,
                                items =  mutableListOf(
                                        ShoppingListItem(0, "Banana", 1, BigDecimal(3.74), checked = false),
                                        ShoppingListItem(1, "Cookies", 2, BigDecimal(1.24), checked = true),
                                        ShoppingListItem(2, "Milk", 6, BigDecimal(1.09), checked = false)
                                )
                        ),
                        ShoppingList(
                                name = "MediaMarket",
                                categoryRef = 3L,
                                storeRef = 1L,
                                items = mutableListOf(
                                        ShoppingListItem(0, "TV", 1, BigDecimal(974), checked = false),
                                        ShoppingListItem(1, "Speaker Box", 1, BigDecimal(283.99), checked = false),
                                        ShoppingListItem(2, "DVD Box", 2, BigDecimal(32.20), checked = true),
                                        ShoppingListItem(3, "RedBull", 1, BigDecimal(2.78), checked = false)
                                )
                        )
                )

                INSTANCE?.daoStore()?.add(
                        Store(
                                name = "MediaMarket",
                                note = "Far now :(",
                                currencyRef = 1L,
                                categoryRef = 15L
                        ),
                        Store(
                                name = "Colruyt",
                                currencyRef = 1L,
                                categoryRef = 2L
                        ),
                        Store(
                                name = "Lidl",
                                currencyRef = 2L,
                                categoryRef = 4L
                        )
                )

                INSTANCE?.daoTemplate()?.add(
                        Template(
                                name = "Bread",
                                accountRef = 1L,
                                categoryRef = 3L,
                                currencyRef = 1L,
                                storeRef = 1L,
                                type = 1,
                                paymentType = 3,
                                position = 0,
                                amount = BigDecimal(2.30)
                        ),
                        Template(
                                name = "Campus Card",
                                accountRef = 2L,
                                categoryRef = 14L,
                                currencyRef = 1L,
                                storeRef = 2L,
                                type = 1,
                                paymentType = 1,
                                position = 1,
                                amount = BigDecimal(10)
                        )
                )

                INSTANCE?.daoRecord()?.add(
                        Record(
                                description = "Foodzzz",
                                categoryRef = 3L,
                                accountRef = 1L,
                                storeRef = 1L,
                                currencyRef = 1L,
                                type = 0,
                                paymentType = 2,
                                amount = BigDecimal(3.47),
                                timestamp = 1586501123000 // 10/04/2020 08:45:23
                        ),
                        Record(
                                description = "Nothings",
                                payee = "Myself",
                                categoryRef = 2L,
                                accountRef = 1L,
                                storeRef = 1L,
                                currencyRef = 1L,
                                type = 1,
                                paymentType = 3,
                                amount = BigDecimal(-2.34),
                                timestamp = 1586877791000 // 14/04/2020 17:23:11
                        ),
                        Record(
                                description = "Item 3",
                                categoryRef = 18L,
                                accountRef = 2L,
                                storeRef = 1L,
                                currencyRef = 1L,
                                type = 1,
                                paymentType = 2,
                                amount = BigDecimal(-10),
                                timestamp = 1591087528000L // 02/06/2020 10:45:28
                        ),
                        Record(
                                description = "Recording",
                                categoryRef = 11L,
                                accountRef = 2L,
                                storeRef = 1L,
                                currencyRef = 1L,
                                type = 0,
                                paymentType = 2,
                                amount = BigDecimal(5.40),
                                timestamp = 1598433834000L // 26/08/2020 11:23:54
                        ),
                        Record(
                                description = "Morning",
                                categoryRef = 2L,
                                accountRef = 1L,
                                storeRef = 2L,
                                currencyRef = 1L,
                                type = 1,
                                paymentType = 2,
                                amount = BigDecimal(-18.20),
                                timestamp = 1599322930000L // 05/09/2020 18:22:10
                        ),
                        Record(
                                description = "Schnitsels",
                                categoryRef = 1L,
                                accountRef = 1L,
                                storeRef = 1L,
                                currencyRef = 1L,
                                type = 1,
                                paymentType = 2,
                                amount = BigDecimal(-4),
                                timestamp = 1559448433000L // 02/06/2019 06:07:13
                        ),
                        Record(
                                description = "Drinks now",
                                categoryRef = 5L,
                                accountRef = 1L,
                                storeRef = 2L,
                                currencyRef = 1L,
                                type = 1,
                                paymentType = 4,
                                amount = BigDecimal(-3.97),
                                timestamp = 1568963640000L // 20/09/2019 09:14:00
                        ),
                        Record(
                                description = "YouKnowWhat",
                                categoryRef = 1L,
                                accountRef = 2L,
                                storeRef = 1L,
                                currencyRef = 1L,
                                type = 1,
                                paymentType = 1,
                                amount = BigDecimal(-2.10),
                                timestamp = 1595515469000L // 23/07/2020 16:44:29
                        )
                )
            }
        }
    }
}
