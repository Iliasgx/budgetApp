package com.umbrella.budgetapp.database.defaults

import com.umbrella.budgetapp.database.collections.Country
import java.math.BigDecimal

object DefaultCountries {
    val defaultCountry = Country(-1, "EUR", "€", BigDecimal(1))
}

/*
public class DefaultCountries {
    private List<Country> countries;

    public List<Country> getDefaultCountries(Activity activity) {
        if (countries == null || countries.isEmpty()){
            setCountry((MainActivity)activity);
        }
        return countries;
    }

    private void setCountry(MainActivity mainActivity){
        final String[] names = mainActivity.getResources().getStringArray(R.array.countries);
        // 1 EUR = ...
        countries = new ArrayList<>(Arrays.asList(
                new Country("USD", names[0],  "$",  "1.1388"),
                new Country("JPY", names[1],  "¥",  "121.97"),
                new Country("EUR", names[2],  "€",  "1"),
                new Country("BGN", names[3],  "Lv", "1.9558"),
                new Country("CZK", names[4],  "Kč", "25.557"),
                new Country("DKK", names[5],  "kr", "7.4654"),
                new Country("GBP", names[6],  "£",  "0.89485"),
                new Country("HUF", names[7],  "Ft", "324.49"),
                new Country("PLN", names[8],  "zł", "4.2563"),
                new Country("RON", names[9],  "L",  "4.7184"),
                new Country("SEK", names[10], "kr", "10.5320"),
                new Country("CHF", names[11], "CHF","1.1108"),
                new Country("ISK", names[12], "kr", "141.50"),
                new Country("NOK", names[13], "kr", "9.6855"),
                new Country("HRK", names[14], "kn", "7.3945"),
                new Country("RUB", names[15], "R",  "71.4389"),
                new Country("TRY", names[16], "₺",  "6.5708"),
                new Country("AUD", names[17], "$A", "1.6341"),
                new Country("BRL", names[18], "R$", "4.3669"),
                new Country("CAD", names[19], "C$", "1.5001"),
                new Country("CNY", names[20], "¥",  "7.8347"),
                new Country("HKD", names[21], "HK$","8.8915"),
                new Country("IDR", names[22], "Rp", "16116.30"),
                new Country("ILS", names[23], "NIS","4.1013"),
                new Country("INR", names[24], "₹",  "78.9725"),
                new Country("KRW", names[25], "₩",  "1314.27"),
                new Country("MXN", names[26], "¢",  "21.9042"),
                new Country("MYR", names[27], "RM", "4.7119"),
                new Country("NZD", names[28], "NZ$","1.7140"),
                new Country("PHP", names[29], "₱",  "58.477"),
                new Country("SGD", names[30], "S$", "1.5411"),
                new Country("THB", names[31], "฿",  "34.973"),
                new Country("ZAR", names[32], "R",  "16.2881")
        ));
    }
}*/