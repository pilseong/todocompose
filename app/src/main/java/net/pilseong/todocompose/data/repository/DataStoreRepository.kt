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
import net.pilseong.todocompose.data.model.UserData
import net.pilseong.todocompose.di.IoDispatcher
import net.pilseong.todocompose.util.Constants.DATE_ORDER_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.FAVORITE_ENABLED_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.NOTE_SORTING_ORDER_ID_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.PREFERENCE_NAME
import net.pilseong.todocompose.util.Constants.PRIORITY_FILTER_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.PRIORITY_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.RECENT_NOTEBOOK_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.STATE_PREFERENCE_KEY
import net.pilseong.todocompose.util.NoteSortingOption
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
        val recentNoteIdsState = stringPreferencesKey(name = RECENT_NOTEBOOK_PREFERENCE_KEY)
        val priorityFilterState = intPreferencesKey(name = PRIORITY_FILTER_PREFERENCE_KEY)
        val stateState = intPreferencesKey(name = STATE_PREFERENCE_KEY)
        val noteSortingOrderState = intPreferencesKey(name = NOTE_SORTING_ORDER_ID_PREFERENCE_KEY)
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

    val userData: Flow<UserData> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Log.i("PHILIP", "[DataStoreRepository]reading $preferences")

            val noteIdsStr = preferences[PreferenceKeys.recentNoteIdsState]
            val noteIds = noteIdsStr?.split(",")

            UserData(
                prioritySortState = Priority.valueOf(
                    preferences[PreferenceKeys.sortState] ?: Priority.NONE.name
                ),
                dateOrderState = SortOption.values()[preferences[PreferenceKeys.dateOrderState]
                    ?: SortOption.UPDATED_AT_DESC.ordinal],
                notebookIdState = if (noteIds != null) noteIds[0].toInt() else -1,
                firstRecentNotebookId = if (noteIds != null && noteIds.size > 1) noteIds[1].toInt() else null,
                secondRecentNotebookId = if (noteIds != null && noteIds.size > 2) noteIds[2].toInt() else null,
                sortFavorite = (preferences[PreferenceKeys.favoriteState]
                    ?: false.toString()).toBoolean(),

                stateState = preferences[PreferenceKeys.stateState] ?: 63,
                stateNone = ((preferences[PreferenceKeys.stateState] ?: 63) and 1) == 1,
                stateWaiting = ((preferences[PreferenceKeys.stateState] ?: 63) and 2) == 2,
                stateSuspended = ((preferences[PreferenceKeys.stateState] ?: 63) and 4) == 4,
                stateActive = ((preferences[PreferenceKeys.stateState] ?: 63) and 8) == 8,
                stateCancelled = ((preferences[PreferenceKeys.stateState] ?: 63) and 16) == 16,
                stateCompleted = ((preferences[PreferenceKeys.stateState] ?: 63) and 32) == 32,

                priorityFilterState = preferences[PreferenceKeys.priorityFilterState] ?: 15,
                priorityNone = ((preferences[PreferenceKeys.priorityFilterState] ?: 15) and 1) == 1,
                priorityLow = ((preferences[PreferenceKeys.priorityFilterState] ?: 15) and 2) == 2,
                priorityMedium = ((preferences[PreferenceKeys.priorityFilterState] ?: 15) and 4) == 4,
                priorityHigh = ((preferences[PreferenceKeys.priorityFilterState] ?: 15) and 8) == 8,

                noteSortingOptionState = NoteSortingOption.values()[preferences[PreferenceKeys.noteSortingOrderState]
                    ?: NoteSortingOption.ACCESS_AT.ordinal],
            )
        }

    suspend fun persistRecentNoteIds(noteIds: List<String>) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.i("PHILIP", "[DataStoreRepository]persistRecentNoteIds $noteIds")
                preferences[PreferenceKeys.recentNoteIdsState] = noteIds.joinToString(",")
            }
        }
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

    suspend fun persistPriorityFilterState(priorityFilterState: Int) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.i("PHILIP", "[DataStoreRepository]persistPriorityFilterState $priorityFilterState")
                preferences[PreferenceKeys.priorityFilterState] = priorityFilterState
            }
        }
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


    suspend fun persistNoteSortingOrderState(noteSortingOption: NoteSortingOption) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.i(
                    "PHILIP",
                    "[DataStoreRepository]persistNoteSortingOrderState $noteSortingOption"
                )
                preferences[PreferenceKeys.noteSortingOrderState] = noteSortingOption.ordinal
            }
        }
    }
}