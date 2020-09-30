package com.umbrella.budgetapp.cache

import com.umbrella.budgetapp.database.collections.Country
import com.umbrella.budgetapp.database.collections.User
import com.umbrella.budgetapp.database.defaults.DefaultCountries

object Memory {
    var loggedUser = User()

    var lastUsedCountry: Country = DefaultCountries().getCountryById(0L)

    var homeCashFlowFilter: Int = 0

    var homeRecordsFilter: Int = 0

    var statisticsFilter: Int = 0

    /*
        var lastUsedCategoryRef: Long? = 0,

        var lastUsedStoreRef: Long? = 0,

        var prefHomeCashFlowFilter: Int? = null,

        var prefHomeRecordsFilter: Int? = null,

        var prefRecordsFilter: Int? = null,

        var prefPlannedPaymentsSorting: Int? = null,
     */
}