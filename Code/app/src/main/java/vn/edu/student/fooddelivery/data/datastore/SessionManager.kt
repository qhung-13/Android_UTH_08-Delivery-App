package vn.edu.student.fooddelivery.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {

    private val currentUserIdKey = stringPreferencesKey("current_user_id")

    val currentUserIdFlow: Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[currentUserIdKey] }

    suspend fun setCurrentUserId(userId: String) {
        context.dataStore.edit { prefs -> prefs[currentUserIdKey] = userId }
    }

    suspend fun clearCurrentUser() {
        context.dataStore.edit { prefs -> prefs.remove(currentUserIdKey) }
    }
}
