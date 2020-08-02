package com.umbrella.budgetapp.Database.Collections;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class ShoppingList {
    public static final String COLLECTION = "shoppingList";
    public static final String USER = "userID";
    public static final String NAME = "name";
    public static final String CATEGORY = "categoryID";
    public static final String STORE = "storeID";
    public static final String REMINDER = "reminder";

    private String UserID;
    private String Name;
    private DocumentReference CategoryID;
    private DocumentReference StoreID;
    private Timestamp Reminder;

    public ShoppingList() {
        //Empty constructor needed.
    }

    public ShoppingList(String userID, String name, DocumentReference categoryID, DocumentReference storeID, Timestamp reminder) {
        UserID = userID;
        Name = name;
        CategoryID = categoryID;
        StoreID = storeID;
        Reminder = reminder;
    }

    public String getUserID() {
        return UserID;
    }

    public String getName() {
        return Name;
    }

    public DocumentReference getCategoryID() {
        return CategoryID;
    }

    public DocumentReference getStoreID() {
        return StoreID;
    }

    public Timestamp getReminder() {
        return Reminder;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setCategoryID(DocumentReference categoryID) {
        CategoryID = categoryID;
    }

    public void setStoreID(DocumentReference storeID) {
        StoreID = storeID;
    }

    public void setReminder(Timestamp reminder) {
        Reminder = reminder;
    }
}
