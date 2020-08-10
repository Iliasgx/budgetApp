package com.umbrella.budgetapp.database.typeconverters

import androidx.room.TypeConverter

class FrequencyTypeConverter {

    @TypeConverter
    fun fromMap(map: MutableMap<String, String?>): String {
        val builder = StringBuilder("")
        map.forEach { (k, v) ->
            builder.append(k).append("$").append(v).append(",")
        }
        return builder.toString()
    }

    @TypeConverter
    fun toMap(value: String = ""): MutableMap<String, String?> {
        val map : MutableMap<String, String?> = mutableMapOf()

        val subs = value.split(",").toTypedArray()

        subs.forEach { str ->
            val stream = str.split("$")
            map[stream[0]] = stream[1]
        }
        return map
    }
}
