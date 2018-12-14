package com.fei_ke.t9

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.WorkerThread
import java.util.concurrent.Executors

object ShortcutLoader {
    private val app = App.instance
    private val shortcutDao = App.db.getShortcutDao()
    private val singleThread = Executors.newSingleThreadExecutor()
    val shortcutList = shortcutDao.getAllShortcut()

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pkgName = intent.dataString?.split(":")?.lastOrNull() ?: return
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED -> singleThread.submit {
                    loadInternal(pkgName)
                }
                Intent.ACTION_PACKAGE_REMOVED -> singleThread.submit {
                    shortcutDao.deleteByPackageName(pkgName)
                }
                Intent.ACTION_PACKAGE_CHANGED -> {
                    singleThread.submit {
                        shortcutDao.deleteByPackageName(pkgName)
                        loadInternal(pkgName)
                    }
                }
            }
        }
    }

    private val localeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            singleThread.submit { loadInternal() }
        }
    }

    fun init() {
        IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }.let {
            app.registerReceiver(packageReceiver, it)
        }

        IntentFilter().apply {
            addAction(Intent.ACTION_LOCALE_CHANGED)
        }.let {
            app.registerReceiver(localeReceiver, it)
        }

        singleThread.submit { loadInternal() }
    }

    @WorkerThread
    private fun loadInternal(pkg: String? = null) {
        val packageManager = app.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setPackage(pkg)

        val shortcutList = packageManager.queryIntentActivities(intent, 0)
            .map { info ->
                Shortcut(
                    info.activityInfo.packageName,
                    info.activityInfo.name,
                    info.loadLabel(packageManager).toString()
                )
            }
        shortcutDao.save(*shortcutList.toTypedArray())
    }

    fun notifyPackageRemoved(pkgName: String) {
        singleThread.submit { shortcutDao.deleteByPackageName(pkgName) }
    }
}