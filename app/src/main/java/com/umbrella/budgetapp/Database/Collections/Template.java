package com.umbrella.budgetapp.Database.Collections;

import com.google.firebase.firestore.DocumentReference;

public class Template {
    public static final String COLLECTION = "template";
    public static final String NAME = "name";
    public static final String ACCOUNT = "accountID";
    public static final String CATEGORY = "categoryID";
    public static final String TYPE = "type";
    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currencyID";
    public static final String STORE = "storeID";
    public static final String PAYMENY_TYPE = "paymentType";
    public static final String PAYEE = "payee";
    public static final String NOTE = "note";
    public static final String POSITION = "position";

    private String Name;
    private DocumentReference AccountID;
    private DocumentReference CategoryID;
    private DocumentReference CurrencyID;
    private DocumentReference StoreID;
    private String Payee;
    private String Note;
    private int Type;
    private int PaymentType;
    private int Position;
    private String Amount;

    public Template() {
        //Empty constructor needed.
    }

    public Template(String name, DocumentReference accountID, DocumentReference categoryID, DocumentReference storeID, int type, String amount, DocumentReference currencyID, int paymentType, String payee, String note, int position) {
        Name = name;
        AccountID = accountID;
        CategoryID = categoryID;
        Type = type;
        Amount = amount;
        CurrencyID = currencyID;
        StoreID = storeID;
        PaymentType = paymentType;
        Payee = payee;
        Note = note;
        Position = position;
    }

    public String getName() {
        return Name;
    }

    public DocumentReference getAccountID() {
        return AccountID;
    }

    public DocumentReference getCategoryID() {
        return CategoryID;
    }

    public int getType() {
        return Type;
    }

    public String getAmount() {
        return Amount;
    }

    public DocumentReference getCurrencyID() {
        return CurrencyID;
    }

    public DocumentReference getStoreID() {
        return StoreID;
    }

    public int getPaymentType() {
        return PaymentType;
    }

    public String getPayee() {
        return Payee;
    }

    public String getNote() {
        return Note;
    }

    public int getPosition() {
        return Position;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setAccountID(DocumentReference accountID) {
        AccountID = accountID;
    }

    public void setCategoryID(DocumentReference categoryID) {
        CategoryID = categoryID;
    }

    public void setCurrencyID(DocumentReference currencyID) {
        CurrencyID = currencyID;
    }

    public void setStoreID(DocumentReference storeID) {
        StoreID = storeID;
    }

    public void setPayee(String payee) {
        Payee = payee;
    }

    public void setNote(String note) {
        Note = note;
    }

    public void setType(int type) {
        Type = type;
    }

    public void setPaymentType(int paymentType) {
        PaymentType = paymentType;
    }

    public void setPosition(int position) {
        Position = position;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }
}
