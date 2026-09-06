package vn.edu.student.fooddelivery.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionManager(context: Context) {

    private val appContext = context.applicationContext

    private val currentUserIdKey = stringPreferencesKey("current_user_id")

    val currentUserIdFlow: Flow<String?> =
        appContext.dataStore.data.map { prefs -> prefs[currentUserIdKey] }

    suspend fun setCurrentUserId(userId: String) {
        appContext.dataStore.edit { prefs -> prefs[currentUserIdKey] = userId }
    }

    suspend fun clearCurrentUser() {
        appContext.dataStore.edit { prefs -> prefs.remove(currentUserIdKey) }
    }
}
