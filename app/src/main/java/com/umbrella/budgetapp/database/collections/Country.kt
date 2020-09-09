package com.umbrella.budgetapp.database.collections

import java.math.BigDecimal

data class Country(
        val id: Long,
        val name: String,
        val symbol: String,
        val defaultRate: BigDecimal
)
