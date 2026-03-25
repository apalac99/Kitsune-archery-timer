package com.proyectotiro.archerytimer.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ArcheryPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()
    fun getInt(key: String, default: Int): Int = prefs.getInt(key, default)

    fun saveString(key: String, value: String) = prefs.edit().putString(key, value).apply()
    fun getString(key: String): String? = prefs.getString(key, null)

    // --- NUEVOS MÉTODOS PARA LISTAS COMPLEJAS (JSON) ---
    fun <T> saveList(key: String, list: List<T>) {
        val json = gson.toJson(list)
        prefs.edit().putString(key, json).apply()
    }

    fun <T> getList(key: String, typeToken: TypeToken<List<T>>): List<T> {
        val json = prefs.getString(key, null) ?: return emptyList()
        return gson.fromJson(json, typeToken.type)
    }

    fun getPrefs(): SharedPreferences = prefs
    fun remove(key: String) = prefs.edit().remove(key).apply()
}