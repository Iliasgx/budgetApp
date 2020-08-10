package com.umbrella.budgetapp.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.umbrella.budgetapp.database.collections.ShoppingListItem

class ShoppingListItemListTypeConverter {

    @TypeConverter
    fun fromShoppingListItemList(list: MutableList<ShoppingListItem>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toShoppingListItemList(value: String): MutableList<ShoppingListItem> {
        val type = object : TypeToken<MutableList<ShoppingListItem>>() {}.type

        return Gson().fromJson<MutableList<ShoppingListItem>>(value, type)
    }
}