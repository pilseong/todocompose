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
import net.pilseong.todocompose.data.model.ui.DateRangeFilterOption
import net.pilseong.todocompose.data.model.ui.MemoDateSortingOption
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.di.IoDispatcher
import net.pilseong.todocompose.util.Constants.DATE_ORDER_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.FAVORITE_ENABLED_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.NOTE_SORTING_ORDER_ID_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.PREFERENCE_NAME
import net.pilseong.todocompose.util.Constants.PRIORITY_FILTER_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.PRIORITY_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.RECENT_NOTEBOOK_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.SEARCH_RANGE_ALL_ENABLED_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.STATE_LINE_ORDER_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.STATE_PREFERENCE_KEY
import net.pilseong.todocompose.data.model.ui.NoteSortingOption
import net.pilseong.todocompose.data.model.ui.SortOption
import net.pilseong.todocompose.util.Constants.DATE_RANGE_FILTER_ID_PREFERENCE_KEY
import net.pilseong.todocompose.util.Constants.MEMO_DATE_SORTING_PREFERENCE_KEY
import net.pilseong.todocompose.util.StateEntity
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
        val prioritySortState = stringPreferencesKey(name = PRIORITY_PREFERENCE_KEY)
        val dateOrderState = stringPreferencesKey(name = DATE_ORDER_PREFERENCE_KEY)
        val memoDateSortingState = stringPreferencesKey(name = MEMO_DATE_SORTING_PREFERENCE_KEY)
        val favoriteState = stringPreferencesKey(name = FAVORITE_ENABLED_PREFERENCE_KEY)
        val searchRangeAll = stringPreferencesKey(name = SEARCH_RANGE_ALL_ENABLED_PREFERENCE_KEY)
        val recentNoteIdsState = stringPreferencesKey(name = RECENT_NOTEBOOK_PREFERENCE_KEY)
        val statusLineOrderState = stringPreferencesKey(name = STATE_LINE_ORDER_PREFERENCE_KEY)
        val priorityFilterState = intPreferencesKey(name = PRIORITY_FILTER_PREFERENCE_KEY)
        val stateState = intPreferencesKey(name = STATE_PREFERENCE_KEY)
        val noteSortingOrderState = intPreferencesKey(name = NOTE_SORTING_ORDER_ID_PREFERENCE_KEY)
        val dateRangeFilterOptionState = intPreferencesKey(name = DATE_RANGE_FILTER_ID_PREFERENCE_KEY)
    }

    suspend fun persistMemoDateSortingState(memoDateSortingOption: MemoDateSortingOption) {
        withContext(ioDispatcher) {
            context.dataStore.edit { preferences ->
                Log.d("PHILIP", "[DataStoreRepository]persistMemoDateSortingState $memoDateSortingOption")
                preferences[PreferenceKeys.memoDateSortingState] = memoDateSortingOption.name
            }
        }
    }

    // data store 에 priority 정보를 저장 한다.
    suspend fun persistPrioritySortState(priority: Priority) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.d("PHILIP", "[DataStoreRepository]persistPrioritySortState $priority")
                preferences[PreferenceKeys.prioritySortState] = priority.name
            }
        }
    }

    suspend fun persistDateRangeFilterState(dateRangeFilterOption: DateRangeFilterOption) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.d("PHILIP", "[DataStoreRepository]persistDateRangeFilterState $dateRangeFilterOption")
                preferences[PreferenceKeys.dateRangeFilterOptionState] = dateRangeFilterOption.ordinal
            }
        }
    }

    // favorite 정보를 저장 한다.
    suspend fun persistFavoriteEnabledState(favorite: Boolean) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
                Log.d("PHILIP", "[DataStoreRepository]persistDateEnabledState $favorite")
                preferences[PreferenceKeys.favoriteState] = favorite.toString()
            }
        }
    }

    suspend fun persistSearchRangeAllState(searchRangeAll: Boolean) {
        withContext(ioDispatcher) {
            context.dataStore.edit { preferences ->
                Log.d("PHILIP", "[DataStoreRepository]persistSearchRangeAllEnabledState $searchRangeAll")
                preferences[PreferenceKeys.searchRangeAll] = searchRangeAll.toString()
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
//            Log.d("PHILIP", "[DataStoreRepository]reading $preferences")

            val noteIdsStr = preferences[PreferenceKeys.recentNoteIdsState]
            val noteIds = noteIdsStr?.split(",")

            val statusLineString = preferences[PreferenceKeys.statusLineOrderState]
                ?.split(",")
                ?.map { StateEntity.values()[it.toInt()] }
                ?.toMutableList() ?: mutableListOf(
                StateEntity.NOTE_FILTER,
                StateEntity.PRIORITY_FILTER,
                StateEntity.STATE_FILTER,
                StateEntity.FAVORITE_FILTER,
                StateEntity.PRIORITY_ORDER,
                StateEntity.SORTING_ORDER,
                StateEntity.DATE_BASE_ORDER,
                StateEntity.DATE_RANGE_FILTER,
            )

            UserData(
                prioritySortState = Priority.valueOf(
                    preferences[PreferenceKeys.prioritySortState] ?: Priority.NONE.name
                ),
                memoDateSortingState = MemoDateSortingOption.valueOf(
                  preferences[PreferenceKeys.memoDateSortingState] ?: MemoDateSortingOption.UPDATED_AT.name
                ),
                dateOrderState = SortOption.valueOf(
                    preferences[PreferenceKeys.dateOrderState] ?: SortOption.DESC.name
                ),
                notebookIdState = if (noteIds != null) noteIds[0].toLong() else -1L,
                firstRecentNotebookId = if (noteIds != null && noteIds.size > 1) noteIds[1].toLong() else null,
                secondRecentNotebookId = if (noteIds != null && noteIds.size > 2) noteIds[2].toLong() else null,
                sortFavorite = (preferences[PreferenceKeys.favoriteState]
                    ?: false.toString()).toBoolean(),
                searchRangeAll = (preferences[PreferenceKeys.searchRangeAll]
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
                priorityMedium = ((preferences[PreferenceKeys.priorityFilterState]
                    ?: 15) and 4) == 4,
                priorityHigh = ((preferences[PreferenceKeys.priorityFilterState] ?: 15) and 8) == 8,

                noteSortingOptionState = NoteSortingOption.values()[preferences[PreferenceKeys.noteSortingOrderState]
                    ?: NoteSortingOption.ACCESS_AT.ordinal],
                dateRangeFilterOption = DateRangeFilterOption.values()[preferences[PreferenceKeys.dateRangeFilterOptionState]
                    ?: DateRangeFilterOption.ALL.ordinal],
                statusLineOrderState = statusLineString
            )
        }

    suspend fun persistRecentNoteIds(noteIds: List<String>) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
//                Log.d("PHILIP", "[DataStoreRepository]persistRecentNoteIds $noteIds")
                preferences[PreferenceKeys.recentNoteIdsState] = noteIds.joinToString(",")
            }
        }
    }

    suspend fun persistStatusLineOrderState(stateOrder: List<StateEntity>) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
//                Log.d("PHILIP", "[DataStoreRepository]persistStatusLineOrderState $stateOrder")
                preferences[PreferenceKeys.statusLineOrderState] =
                    stateOrder.joinToString(",") { it.ordinal.toString() }
            }
        }
    }

    suspend fun persistStateState(stateState: Int) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
//                Log.d("PHILIP", "[DataStoreRepository]persistStateState $stateState")
                preferences[PreferenceKeys.stateState] = stateState
            }
        }
    }

    suspend fun persistPriorityFilterState(priorityFilterState: Int) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
//                Log.d(
//                    "PHILIP",
//                    "[DataStoreRepository]persistPriorityFilterState $priorityFilterState"
//                )
                preferences[PreferenceKeys.priorityFilterState] = priorityFilterState
            }
        }
    }

    suspend fun persistDateOrderState(dateOrderState: SortOption) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
//                Log.d("PHILIP", "[DataStoreRepository]persistDateOrderState $dateOrderState")
                preferences[PreferenceKeys.dateOrderState] = dateOrderState.name
            }
        }
    }


    suspend fun persistNoteSortingOrderState(noteSortingOption: NoteSortingOption) {
        withContext(ioDispatcher) {
            // preferences 는 data store 안에 있는 모든 데이터 를 가지고 있다.
            context.dataStore.edit { preferences ->
//                Log.d(
//                    "PHILIP",
//                    "[DataStoreRepository]persistNoteSortingOrderState $noteSortingOption"
//                )
                preferences[PreferenceKeys.noteSortingOrderState] = noteSortingOption.ordinal
            }
        }
    }
}