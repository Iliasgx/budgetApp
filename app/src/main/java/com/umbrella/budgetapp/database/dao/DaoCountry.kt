package com.umbrella.budgetapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import com.umbrella.budgetapp.database.collections.Country
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoCountry : Base<Country> {

    /**
     * Retrieves all countries.
     *
     * @return The list of currencies in a Flow.
     */
    @Query("SELECT * FROM countries ORDER BY name")
    fun getAllCountries() : Flow<List<Country>>

    /**
     * Find Country by ID.
     *
     * @param id: The ID corresponding with the Country.
     * @return The Country represented by the ID.
     */
    @Query("SELECT * FROM countries WHERE country_id = :id")
    fun getCountryById(id: Long) : Country
}