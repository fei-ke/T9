package com.fei_ke.t9

import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.widget.ImageView
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

object IconLoader {
    private val cache = HashMap<String, Drawable>()
    private val iconLoaderPool = Executors.newCachedThreadPool() as ThreadPoolExecutor

    fun load(imageView: ImageView, pkgName: String, className: String) {
        val cacheKey = getTag(pkgName, className)

        if (cache.containsKey(cacheKey)) {
            imageView.setImageDrawable(cache[cacheKey])
        } else {
            imageView.setImageDrawable(null)
            iconLoaderPool.execute(LoadIconTask(imageView, pkgName, className))
        }
    }

    private fun getTag(pkgName: String, className: String) = "$pkgName:$className"

    private class LoadIconTask(imageView: ImageView, val pkgName: String, val className: String) : Runnable {
        private val imageViewRef = WeakReference(imageView)

        init {
            imageView.setTag(R.id.key_load_icon, getTag(pkgName, className))
        }

        override fun run() {
            val icon = try {
                App.instance.packageManager.getActivityIcon(ComponentName(pkgName, className))
            } catch (e: PackageManager.NameNotFoundException) {
                ShortcutLoader.notifyPackageRemoved(pkgName)
                null
            } ?: return

            val tagOrCacheKey = getTag(pkgName, className)
            cache[tagOrCacheKey] = icon
            imageViewRef.get()?.apply {
                post {
                    if (getTag(R.id.key_load_icon) == tagOrCacheKey) {
                        alpha = 0f
                        setImageDrawable(icon)
                        animate().alpha(1f).setDuration(400).start()
                    }
                }
            }
        }
    }
}