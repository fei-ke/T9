package com.fei_ke.t9

import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.widget.ImageView
import java.lang.ref.WeakReference

object IconLoader {
    private val cache = HashMap<String, Drawable>()

    fun load(imageView: ImageView, pkgName: String, className: String) {
        val cacheKey = getTag(pkgName, className)

        if (cache.containsKey(cacheKey)) {
            imageView.setImageDrawable(cache[cacheKey])
        } else {
            imageView.setImageDrawable(null)
            LoadIconTask(imageView, pkgName, className).execute()
        }
    }

    private fun getTag(pkgName: String, className: String) = "$pkgName:$className"

    private class LoadIconTask(imageView: ImageView, val pkgName: String, val className: String) :
        AsyncTask<Unit, Int, Drawable?>() {

        private val imageViewRef = WeakReference(imageView)

        init {
            imageView.setTag(R.id.key_load_icon, getTag(pkgName, className))
        }

        override fun doInBackground(vararg params: Unit?): Drawable? {
            return try {
                App.instance.packageManager.getActivityIcon(
                    ComponentName(pkgName, className)
                )
            } catch (e: PackageManager.NameNotFoundException) {
                ShortcutLoader.notifyPackageRemoved(pkgName)
                null
            }

        }

        override fun onPostExecute(result: Drawable?) {
            result ?: return

            val tagOrCacheKey = getTag(pkgName, className)
            cache[tagOrCacheKey] = result

            imageViewRef.get()
                ?.takeIf { it.getTag(R.id.key_load_icon) == tagOrCacheKey }
                ?.apply { setImageDrawable(result) }
        }
    }
}