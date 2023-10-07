package com.fei_ke.t9.home

import com.fei_ke.t9.Shortcut
import com.t9search.util.T9Util

data class HomeUiState(
    val appList: List<Shortcut>,
    val filter: String
) {
    val filteredList: List<Shortcut> = filterAppList(appList, filter)

    private fun filterAppList(appList: List<Shortcut>, filter: String): List<Shortcut> {
        if (filter.isEmpty()) {
            return appList
        }
        return appList.filter {
            T9Util.match(it.searchUnit, filter)
        }.sortedWith(
            compareBy(
                { it.label.indexOf(it.searchUnit.matchKeyword.toString()) },
                { if (it.label.isEmpty()) 0 else 1 - it.searchUnit.matchKeyword.length / it.label.length }
            )
        )
    }
}