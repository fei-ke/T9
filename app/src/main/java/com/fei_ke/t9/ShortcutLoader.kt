package com.fei_ke.t9

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.util.concurrent.Executors

object ShortcutLoader {
    private val shortcutDao = App.db.getShortcutDao()
    private val singleThread = Executors.newSingleThreadExecutor()
    val shortcutList = shortcutDao.getAllShortcut()
    private val receiver = object : BroadcastReceiver() {
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

    fun startLoad() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        App.instance.registerReceiver(receiver, intentFilter)

        singleThread.submit { loadInternal() }
    }

    private fun loadInternal(pkg: String? = null) {
        val packageManager = App.instance.packageManager
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