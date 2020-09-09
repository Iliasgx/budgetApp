package com.umbrella.budgetapp.database.defaults

import com.umbrella.budgetapp.database.collections.Country
import com.umbrella.budgetapp.extensions.orElse
import java.math.BigDecimal

class DefaultCountries {
    private val countries = listOf(
            Country(0L, "USD","$",   BigDecimal(1.1388)),
            Country(1L, "JPY","¥",   BigDecimal(121.97)),
            Country(2L, "EUR","€",   BigDecimal(1)),
            Country(3L, "BGN","Lv",  BigDecimal(1.9558)),
            Country(4L, "CZK","Kč",  BigDecimal(25.557)),
            Country(5L, "DKK","kr",  BigDecimal(7.4654)),
            Country(6L, "GBP","£",   BigDecimal(0.89485)),
            Country(7L, "HUF","Ft",  BigDecimal(324.49)),
            Country(8L, "PLN","zł",  BigDecimal(4.2563)),
            Country(9L, "RON","L",   BigDecimal(4.7184)),
            Country(10L,"SEK","kr",  BigDecimal(10.5320)),
            Country(11L,"CHF","CHF", BigDecimal(1.1108)),
            Country(12L,"ISK","kr",  BigDecimal(141.50)),
            Country(13L,"NOK","kr",  BigDecimal(9.6855)),
            Country(14L,"HRK","kn",  BigDecimal(7.3945)),
            Country(15L,"RUB","R",   BigDecimal(71.4389)),
            Country(16L,"TRY","₺",   BigDecimal(6.5708)),
            Country(17L,"AUD","$'A'",BigDecimal(1.6341)),
            Country(18L,"BRL","R$",  BigDecimal(4.3669)),
            Country(19L,"CAD","C$",  BigDecimal(1.5001)),
            Country(20L,"CNY","¥",   BigDecimal(7.8347)),
            Country(21L,"HKD","HK$", BigDecimal(8.8915)),
            Country(22L,"IDR","Rp",  BigDecimal(16116.30)),
            Country(23L,"ILS","NIS", BigDecimal(4.1013)),
            Country(24L,"INR","₹",   BigDecimal(78.9725)),
            Country(25L,"KRW","₩",   BigDecimal(1314.27)),
            Country(26L,"MXN","¢",   BigDecimal(21.9042)),
            Country(27L,"MYR","RM",  BigDecimal(4.7119)),
            Country(28L,"NZD","NZ$", BigDecimal(1.7140)),
            Country(29L,"PHP","₱",   BigDecimal(58.477)),
            Country(30L,"SGD","S$",  BigDecimal(1.5411)),
            Country(31L,"THB","฿",   BigDecimal(34.973)),
            Country(32L,"ZAR","R",   BigDecimal(16.2881))
    )

    fun getCountryById(id: Long?) : Country = countries.find { country -> country.id == id }.orElse(countries[0])
}


