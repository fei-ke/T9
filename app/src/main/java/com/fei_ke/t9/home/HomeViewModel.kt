package com.fei_ke.t9.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fei_ke.t9.ShortcutLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        private const val CLEAR_FILTER_DELAY = 5000L
    }

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState(emptyList(), ""))
    val uiState: StateFlow<HomeUiState> = _uiState

    private var clearFilterJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            ShortcutLoader.shortcutList.collect { list ->
                _uiState.update {
                    it.copy(appList = list)
                }
            }
        }
    }

    fun appendFilter(filter: String) {
        _uiState.update {
            it.copy(filter = it.filter + filter)
        }
    }

    fun deleteFilter() {
        _uiState.update {
            it.copy(filter = it.filter.dropLast(1))
        }
    }

    fun clearFilter() {
        _uiState.update {
            it.copy(filter = "")
        }
    }

    fun scheduleClearFilter() {
        if (_uiState.value.filter.isEmpty()) return

        clearFilterJob = viewModelScope.launch {
            delay(CLEAR_FILTER_DELAY)
            clearFilter()
        }
    }

    fun cancelClearFilterJob() {
        clearFilterJob?.cancel()
    }
}