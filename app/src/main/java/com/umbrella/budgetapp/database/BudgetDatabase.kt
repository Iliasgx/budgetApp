package com.umbrella.budgetapp.database

import android.accounts.Account
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Account::class), version = 1, exportSchema = false)
public abstract class BudgetDatabase: RoomDatabase() {
    //abstract fun daoAccount(): DaoAccount

    companion object {
        private const val DATABASE_NAME: String = "budget_database"

        @Volatile
        private var INSTANCE: BudgetDatabase? = null

        fun getDatabase(context: Context): BudgetDatabase {
            val temp = INSTANCE

            if (temp != null) return temp

            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, BudgetDatabase::class.java, DATABASE_NAME).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}