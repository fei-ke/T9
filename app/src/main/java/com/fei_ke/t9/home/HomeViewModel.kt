package com.fei_ke.t9.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fei_ke.t9.ShortcutLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState(emptyList(), ""))
    val uiState: StateFlow<HomeUiState> = _uiState

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
}