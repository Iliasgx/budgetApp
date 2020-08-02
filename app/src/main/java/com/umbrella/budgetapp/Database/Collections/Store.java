package com.umbrella.budgetapp.Database.Collections;

import com.google.firebase.firestore.DocumentReference;

public class Store {
    public static final String COLLECTION = "store";
    public static final String USER = "userID";
    public static final String NAME = "name";
    public static final String CURRENCY = "currencyID";
    public static final String CATEGORY = "categoryID";
    public static final String NOTE = "note";

    private String UserID;
    private String Name;
    private String Note;
    private DocumentReference CurrencyID;
    private DocumentReference CategoryID;

    public Store() {
        //Empty constructor needed.
    }

    public Store(String userID, String name, DocumentReference currencyID, DocumentReference categoryID, String note) {
        UserID = userID;
        Name = name;
        CurrencyID = currencyID;
        CategoryID = categoryID;
        Note = note;
    }

    public String getUserID() {
        return UserID;
    }

    public String getName() {
        return Name;
    }

    public DocumentReference getCurrencyID() {
        return CurrencyID;
    }

    public DocumentReference getCategoryID() {
        return CategoryID;
    }

    public String getNote() {
        return Note;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setNote(String note) {
        Note = note;
    }

    public void setCurrencyID(DocumentReference currencyID) {
        CurrencyID = currencyID;
    }

    public void setCategoryID(DocumentReference categoryID) {
        CategoryID = categoryID;
    }
}
