package com.umbrella.budgetapp.Database.Collections;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class Debt {
    public static final String COLLECTION = "debt";
    public static final String USER = "userID";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String CATEGORY = "categoryID";
    public static final String ACCOUNT = "accountID";
    public static final String AMOUNT  ="amount";
    public static final String CURRENCY = "currencyID";
    public static final String DATE = "date";
    public static final String DEBT_TYPE = "debtType";

    private String UserID;
    private String Name;
    private String Description;
    private DocumentReference CategoryID;
    private DocumentReference AccountID;
    private DocumentReference CurrencyID;
    private String Amount;
    private Timestamp Date;
    private int DebtType;

    public Debt() {
        //Empty constructor needed.
    }

    public Debt(String userID, String name, String description, DocumentReference categoryID, DocumentReference accountID, String amount, DocumentReference currencyID, Timestamp date, int debtType) {
        UserID = userID;
        Name = name;
        Description = description;
        CategoryID = categoryID;
        AccountID = accountID;
        CurrencyID = currencyID;
        Amount = amount;
        Date = date;
        DebtType = debtType;
    }

    public enum DebtType {
        LENT,
        BORROWED
    }

    public String getUserID() {
        return UserID;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public DocumentReference getCategoryID() {
        return CategoryID;
    }

    public DocumentReference getAccountID() {
        return AccountID;
    }

    public DocumentReference getCurrencyID() {
        return CurrencyID;
    }

    public String getAmount() {
        return Amount;
    }

    public Timestamp getDate() {
        return Date;
    }

    public int getDebtType() {
        return DebtType;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setCategoryID(DocumentReference categoryID) {
        CategoryID = categoryID;
    }

    public void setAccountID(DocumentReference accountID) {
        AccountID = accountID;
    }

    public void setCurrencyID(DocumentReference currencyID) {
        CurrencyID = currencyID;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public void setDate(Timestamp date) {
        Date = date;
    }

    public void setDebtType(int debtType) {
        DebtType = debtType;
    }
}
