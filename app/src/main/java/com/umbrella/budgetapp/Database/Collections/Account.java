package com.umbrella.budgetapp.Database.Collections;

import com.google.firebase.firestore.DocumentReference;

public class Account {
    public static final String COLLECTION = "account";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String CURRENT_VALUE = "currentValue";
    public static final String CURRENCY = "currencyID";
    public static final String COLOR = "color";
    public static final String EXCLUDE_FROM_STATS = "excludeStats";
    public static final String POSITION = "position";

    private String Name;
    private DocumentReference CurrencyID;
    private int Type;
    private int Color;
    private int Position;
    private String CurrentValue;
    private boolean ExcludeStats = false;

    public Account() {
        //Empty constructor needed.
    }

    public Account(String name, int type, int color, String currentValue, int position, DocumentReference currencyID, boolean excludeStats) {
        Name = name;
        Type = type;
        Color = color;
        CurrentValue = currentValue;
        Position = position;
        CurrencyID = currencyID;
        ExcludeStats = excludeStats;
    }

    public String getName() {
        return Name;
    }

    public DocumentReference getCurrencyID() {
        return CurrencyID;
    }

    public int getType() {
        return Type;
    }

    public int getColor() {
        return Color;
    }

    public int getPosition() {
        return Position;
    }

    public String getCurrentValue() {
        return CurrentValue;
    }

    public boolean isExcludeStats() {
        return ExcludeStats;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setCurrencyID(DocumentReference currencyID) {
        CurrencyID = currencyID;
    }

    public void setType(int type) {
        Type = type;
    }

    public void setColor(int color) {
        Color = color;
    }

    public void setPosition(int position) {
        Position = position;
    }

    public void setCurrentValue(String currentValue) {
        CurrentValue = currentValue;
    }

    public void setExcludeStats(boolean excludeStats) {
        ExcludeStats = excludeStats;
    }
}
