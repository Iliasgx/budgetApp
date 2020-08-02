package com.umbrella.budgetapp.Database.Collections;

import com.google.firebase.firestore.DocumentReference;

public class ShoppingListItem {
    public static final String COLLECTION = "shoppingListItem";
    public static final String SHOPPING_ITEM = "shoppingItemID";
    public static final String NUMBER = "number";
    public static final String CHECKED = "checked";
    public static final String POSITION = "position";

    private DocumentReference ShoppingItemID;
    private int Number;
    private int Position;
    private boolean Checked;

    public ShoppingListItem() {
        //Empty constructor needed.
    }

    public ShoppingListItem(DocumentReference shoppingItemID, int number, boolean checked, int position) {
        ShoppingItemID = shoppingItemID;
        Number = number;
        Checked = checked;
        Position = position;
    }

    public DocumentReference getShoppingItemID() {
        return ShoppingItemID;
    }

    public int getNumber() {
        return Number;
    }

    public boolean isChecked() {
        return Checked;
    }

    public int getPosition() {
        return Position;
    }

    public void setShoppingItemID(DocumentReference shoppingItemID) {
        ShoppingItemID = shoppingItemID;
    }

    public void setNumber(int number) {
        Number = number;
    }

    public void setPosition(int position) {
        Position = position;
    }

    public void setChecked(boolean checked) {
        Checked = checked;
    }
}
