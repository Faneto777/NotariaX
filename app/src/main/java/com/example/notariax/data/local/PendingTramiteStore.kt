package com.example.notariax.data.local

import android.content.Context

class PendingTramiteStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    fun save(tramiteId: Long) {
        preferences.edit().putLong(KEY_TRAMITE_ID, tramiteId).apply()
    }

    fun get(): Long? = if (preferences.contains(KEY_TRAMITE_ID)) {
        preferences.getLong(KEY_TRAMITE_ID, 0L)
    } else {
        null
    }

    fun clear() {
        preferences.edit().remove(KEY_TRAMITE_ID).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "notariax_backend"
        const val KEY_TRAMITE_ID = "tramite_pendiente_id"
    }
}