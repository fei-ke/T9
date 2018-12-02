package com.fei_ke.t9

import android.graphics.drawable.Drawable
import com.t9search.model.PinyinSearchUnit

class Shortcut(val pkgName: String, val className: String, val label: String, val icon: Drawable) {
    val searchUnit = PinyinSearchUnit(label)
}
