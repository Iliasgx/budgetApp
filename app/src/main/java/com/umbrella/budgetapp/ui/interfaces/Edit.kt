package com.umbrella.budgetapp.ui.interfaces

interface Edit {

    /**
     * Enum identifier if the current screen was called for a new item or to edit one.
     */
    enum class Type {
        NEW,
        EDIT
    }

    /**
     * Check if id is 0 to identify the correct type.
     *
     * @param id: The ID of the item that has to be checked.
     *
     * @return The Type following the identification of the ID.
     */
    fun checkType(id: Long) : Type { return if (id == 0L) Type.NEW else Type.EDIT }

    /**
     * Check if the edited item is still the same as the base item or it has changes.
     *
     * @param baseItem The item that was first initiated.
     * @param editItem The edited item that needs to be checked.
     *
     * @return If editItem is edited and no longer the same as baseItem.
     */
    fun hasChanges(baseItem: Any, editItem: Any) : Boolean { return editItem !=  baseItem }

    /**
     * Populate necessary views and implement the default data.
     */
    fun initData()

    /**
     * Set all necessary listeners for the edit screen.
     * Function called after initData to make sure that listeners are not called when initializing data in the views.
     */
    fun setListeners()

    /**
     * Check if all requirements are met before saving the data.
     */
    fun checkData()

    /**
     * Save all data after the requirements are checked.
     */
    fun saveData()
}