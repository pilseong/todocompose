package net.pilseong.todocompose.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.di.IoDispatcher
import net.pilseong.todocompose.util.Constants.DATE_ORDER_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.FAVORITE_ENABLED_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.NOTEBOOK_ID_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.PREFERENCE_NAME
import net.pilseong.todocompose.util.Constants.PRIORITY_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.RECENT_NOTEBOOK_FIRST_ID_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.RECENT_NOTEBOOK_SECOND_ID_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.STATE_PREFERENCE_KEY
import net.pilseong.todocompose.util.SortOption
import java.io.IOException
import javax.inject.Inject

// extension field
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    @ApplicationContext private val context: Context
) {
    private object PreferenceKeys {
        val sortState = stringPreferencesKey(name = PRIORITY_PREFERENCE_KEY)
        val dateOrderState = intPreferencesKey(name = DATE_ORDER_PREFERENCE_KEY)
        val favoriteState = stringPreferencesKey(name = FAVORITE_ENABLED_PREFERENCE_KEY)
        val notebookIdState = intPreferencesKey(name = NOTEBOOK_ID_PREFERENCE_KEY)
        val recentNoteFirst = intPreferencesKey(name = RECENT_NOTEBOOK_FIRST_ID_PREFERENCE_KEY)
        val recentNoteSecond = intPreferencesKey(name = RECENT_NOTEBOOK_SECOND_ID_PREFERENCE_KEY)
        val stateState = intPreferencesKey(name = STATE_PREFERENCE_KEY)
    }

    // data store 에 priority 정보를 저장 한다.
    suspend fun persistPrioritySortState(priority: Priority) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.i("PHILIP", "[DataStoreRepository]persistPrioritySortState $priority")
                preferences[PreferenceKeys.sortState] = priority.name
            }
        }
    }

    val readPrioritySortState: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Log.i(
                "PHILIP",
                "[DataStoreRepository]readPrioritySortState ${preferences[PreferenceKeys.sortState]}"
            )
            preferences[PreferenceKeys.sortState] ?: Priority.NONE.name
        }

    // favorite 정보를 저장 한다.
    suspend fun persistFavoriteEnabledState(favorite: Boolean) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.i("PHILIP", "[DataStoreRepository]persistDateEnabledState $favorite")
                preferences[PreferenceKeys.favoriteState] = favorite.toString()
            }
        }
    }

    val readFavoriteState: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Log.i(
                "PHILIP",
                "[DataStoreRepository]readFavoriteState ${preferences[PreferenceKeys.favoriteState]}"
            )
            preferences[PreferenceKeys.favoriteState] ?: false.toString()
        }

    suspend fun persistSelectedNotebookId(notebookId: Int) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.i("PHILIP", "[DataStoreRepository]persistSelectedNotebookId $notebookId")
                preferences[PreferenceKeys.notebookIdState] = notebookId
            }
        }
    }

    val readSelectedNotebookId: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Log.i(
                "PHILIP",
                "[DataStoreRepository]readSelectedNotebookId ${preferences[PreferenceKeys.notebookIdState]}"
            )
            preferences[PreferenceKeys.notebookIdState] ?: -1
        }


    suspend fun persistFirstRecentNotebookId(notebookId: Int) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.i("PHILIP", "[DataStoreRepository]persistFirstRecentNotebookId $notebookId")
                preferences[PreferenceKeys.recentNoteFirst] = notebookId
            }
        }
    }

    val readFirstRecentNotebookId: Flow<Int?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Log.i(
                "PHILIP",
                "[DataStoreRepository]readFirstRecentNotebookId ${preferences[PreferenceKeys.recentNoteFirst]}"
            )
            preferences[PreferenceKeys.recentNoteFirst] ?: null
        }


    suspend fun persistSecondRecentNotebookId(notebookId: Int) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.i("PHILIP", "[DataStoreRepository]persistSecondRecentNotebookId $notebookId")
                preferences[PreferenceKeys.recentNoteSecond] = notebookId
            }
        }
    }

    val readSecondRecentNotebookId: Flow<Int?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Log.i(
                "PHILIP",
                "[DataStoreRepository]readSecondRecentNotebookId ${preferences[PreferenceKeys.recentNoteSecond]}"
            )
            preferences[PreferenceKeys.recentNoteSecond] ?: null
        }


    suspend fun persistStateState(stateState: Int) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.i("PHILIP", "[DataStoreRepository]persistStateState $stateState")
                preferences[PreferenceKeys.stateState] = stateState
            }
        }
    }

    val readStateState: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Log.i(
                "PHILIP",
                "[DataStoreRepository]readStateState ${preferences[PreferenceKeys.stateState]}"
            )

            // 0b11111
            preferences[PreferenceKeys.stateState] ?: 31
        }

    suspend fun persistDateOrderState(dateOrderState: Int) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.i("PHILIP", "[DataStoreRepository]persistDateOrderState $dateOrderState")
                preferences[PreferenceKeys.dateOrderState] = dateOrderState
            }
        }
    }

    val readDateOrderState: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Log.i(
                "PHILIP",
                "[DataStoreRepository]readDateOrderState ${preferences[PreferenceKeys.dateOrderState]}"
            )
            val default = 0b11111
            default.toInt()
            preferences[PreferenceKeys.dateOrderState] ?: SortOption.UPDATED_AT_DESC.ordinal
        }
}