package com.umbrella.budgetapp.Database.Collections;

public class ShoppingItem {
    public static final String COLLECTION = "shoppingItem";
    public static final String USER = "userID";
    public static final String NAME = "name";
    public static final String DEFAULT_AMOUNT = "defAmount";

    private String UserID;
    private String Name;
    private String DefAmount;

    public ShoppingItem() {
        //Default constructor needed.
    }

    public ShoppingItem(String userID, String name, String defAmount) {
        UserID = userID;
        Name = name;
        DefAmount = defAmount;
    }

    public String getUserID() {
        return UserID;
    }

    public String getName() {
        return Name;
    }

    public String getDefAmount() {
        return DefAmount;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setDefAmount(String defAmount) {
        DefAmount = defAmount;
    }
}
