package com.umbrella.budgetapp.Database.Collections;

public class Currency {
    public static final String COLLECTION = "currency";
    public static final String COUNTRY = "countryID";
    public static final String RATE = "usedRate";
    public static final String POSITION = "position";

    private String CountryID;
    private String UsedRate;
    private int Position;

    public Currency() {
        //Empty constructor needed.
    }

    public Currency(String countryID, String usedRate, int position) {
        CountryID = countryID;
        UsedRate = usedRate;
        Position = position;
    }

    public String getCountryID() {
        return CountryID;
    }

    public String getUsedRate() {
        return UsedRate;
    }

    public int getPosition() {
        return Position;
    }

    public void setCountryID(String countryID) {
        CountryID = countryID;
    }

    public void setUsedRate(String usedRate) {
        UsedRate = usedRate;
    }

    public void setPosition(int position) {
        Position = position;
    }
}
