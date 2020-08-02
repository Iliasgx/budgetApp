package com.umbrella.budgetapp.Database.Collections;

public class Country {
    public static final String COLLECTION = "country";
    public static final String CODE = "code";
    public static final String NAME = "name";
    public static final String SYMBOL = "symbol";
    public static final String RATE = "defaultRate";

    private String Code;
    private String Name;
    private String Symbol;
    private String DefaultRate;

    public Country() {
        //Empty constructor needed
    }

    public Country(String code, String name, String symbol, String defaultRate) {
        Code = code;
        Name = name;
        Symbol = symbol;
        DefaultRate = defaultRate;
    }

    public String getCode() {
        return Code;
    }

    public String getName() {
        return Name;
    }

    public String getSymbol() {
        return Symbol;
    }

    public String getDefaultRate() {
        return DefaultRate;
    }

    //No setters because this class can't be changed.
}
