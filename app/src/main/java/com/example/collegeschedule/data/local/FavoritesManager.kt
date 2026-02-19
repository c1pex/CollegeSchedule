package com.example.collegeschedule.data.local

import android.content.Context

class FavoritesManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)

    private val KEY = "favorite_groups"

    fun getFavorites(): Set<String> {
        return prefs.getStringSet(KEY, emptySet())?.toSet() ?: emptySet()
    }

    fun isFavorite(group: String): Boolean {
        return getFavorites().contains(group)
    }

    fun addFavorite(group: String) {
        val newSet = getFavorites().toMutableSet()
        newSet.add(group)

        prefs.edit()
            .remove(KEY) // 🔥 сбрасываем старое
            .putStringSet(KEY, newSet)
            .apply()
    }

    fun removeFavorite(group: String) {
        val newSet = getFavorites().toMutableSet()
        newSet.remove(group)

        prefs.edit()
            .remove(KEY) // 🔥 сбрасываем старое
            .putStringSet(KEY, newSet)
            .apply()
    }
}
