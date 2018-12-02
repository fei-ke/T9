package com.fei_ke.t9

import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fei_ke.common.model.ListData
import com.t9search.util.T9Util
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: App) : AndroidViewModel(application) {
    private val allAppList = ArrayList<Shortcut>()

    private val appList = MutableLiveData<ListData<Shortcut>>()

    private var keywords: String? = null

    private val searchedCache = WeakHashMap<String?, List<Shortcut>>()

    init {
        val ignored = ShortcutLoader.shortcutList
            .subscribeOn(Schedulers.io())
            .subscribe {
                allAppList.clear()
                allAppList.addAll(it)
                searchedCache.clear()
                query(keywords)
            }
    }

    fun appList() = appList

    @Synchronized
    fun query(keywords: String?) {
        this.keywords = keywords

        if (TextUtils.isEmpty(keywords)) {
            appList.postValue(ListData(allAppList))
            searchedCache.put(keywords, allAppList)
            return
        }
        val toSearchList = if (keywords!!.length > 1) {
            searchedCache[keywords.substring(0, keywords.length - 1)] ?: allAppList
        } else {
            allAppList
        }

        val searchedList = toSearchList.filter {
            T9Util.match(it.searchUnit, keywords)
        }.sortedBy {
            it.label.indexOf(it.searchUnit.matchKeyword.toString())
        }

        appList.postValue(ListData(searchedList))

        searchedCache.put(keywords, searchedList)
    }

}
