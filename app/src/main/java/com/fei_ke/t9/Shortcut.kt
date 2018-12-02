package com.fei_ke.t9

import com.t9search.model.PinyinSearchUnit

class Shortcut(val pkgName: String, val className: String, val label: String) {
    companion object {
        val IGNORE_CHA = Regex("[_\\-\\s\\.]")
    }

    val searchUnit = PinyinSearchUnit(label.replace(IGNORE_CHA, ""))
}
