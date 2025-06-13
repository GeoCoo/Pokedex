package com.android.session

import android.content.Context
import android.content.SharedPreferences
import com.android.api.ResourceProvider
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit


@Singleton
class SessionManager @Inject constructor(resourceProvider: ResourceProvider) {
    private val prefs: SharedPreferences = resourceProvider.provideContext()
        .getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)

    private val TOKEN_MAP_KEY = "token_user_map"
    private val CURRENT_TOKEN_KEY = "current_token"

    fun login(token: String): String {
        val userId = UUID.randomUUID().toString()
        prefs.edit { putString(CURRENT_TOKEN_KEY, token) }

        val map = JSONObject(prefs.getString(TOKEN_MAP_KEY, "{}") ?: "{}")
        map.put(token, userId)
        prefs.edit { putString(TOKEN_MAP_KEY, map.toString()) }
        return token
    }

    fun getCurrentToken(): String? {
        return prefs.getString(CURRENT_TOKEN_KEY, null)
    }

    fun getCurrentUserId(): String? {
        val token = getCurrentToken() ?: return null
        val map = JSONObject(prefs.getString(TOKEN_MAP_KEY, "{}") ?: "{}")
        return map.optString(token, null)
    }

    fun logout() {
        prefs.edit { remove(CURRENT_TOKEN_KEY) }
    }

    fun clearAllSessions() {
        prefs.edit { clear() }
    }
}