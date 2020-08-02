package com.umbrella.budgetapp.Customs;

public interface UpdateListeners {
    /**
     * Enum for type of screen presented.
     */
    enum Type {
        CREATE,
        UPDATE
    }

    /**
     * Void for loading data into the fields
     */
    Type onLoad(Type type);

    /**
     * Void for updating data of fields.
     */
    void onUpdate();

    /**
     * Void for deleting data.
     */
    void onDelete();
}
