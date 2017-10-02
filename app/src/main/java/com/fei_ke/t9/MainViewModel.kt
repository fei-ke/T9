package com.fei_ke.t9

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.os.AsyncTask
import android.text.TextUtils
import com.fei_ke.common.model.ListData
import com.t9search.util.PinyinUtil
import com.t9search.util.T9Util
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val allAppList = ArrayList<App>()

    private val appList = MutableLiveData<ListData<App>>()

    private var keywords: String? = null

    init {
        val loadTask = object : AsyncTask<Unit, List<App>, Unit>() {
            override fun doInBackground(vararg params: Unit) {
                val packageManager = application.packageManager
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)

                val activities = packageManager.queryIntentActivities(intent, 0)

                val tempList = ArrayList<App>()

                activities.forEachIndexed { index, info ->
                    val app = App(info.activityInfo.packageName,
                            info.activityInfo.name,
                            info.loadLabel(packageManager).toString(),
                            info.loadIcon(packageManager))

                    PinyinUtil.parse(app.searchUnit)
                    tempList.add(app)
                    if ((index != 0 && index.rem(18) == 0) || index == activities.size - 1) {
                        publishProgress(ArrayList(tempList))
                        tempList.clear()
                    }
                }
            }

            override fun onProgressUpdate(vararg values: List<App>) {
                super.onProgressUpdate(*values)
                var list = values[0]
                allAppList.addAll(list)

                if (!TextUtils.isEmpty(keywords)) {
                    appList.postValue(ListData(list, true))
                } else {
                    query(keywords)
                }

            }

        }

        loadTask.execute()
    }

    fun appList() = appList

    fun query(keywords: String?) {
        if (TextUtils.isEmpty(keywords)) {
            appList.postValue(ListData(allAppList))
            return
        }
        val searchedList = allAppList.filter {
            T9Util.match(it.searchUnit, keywords)
        }.sortedBy {
            it.label.indexOf(it.searchUnit.matchKeyword.toString())
        }


        appList.postValue(ListData(searchedList))
    }

}
