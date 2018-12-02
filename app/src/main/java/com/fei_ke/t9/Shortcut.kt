package com.fei_ke.t9

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.t9search.model.PinyinSearchUnit
import com.t9search.util.PinyinUtil

@Entity(tableName = "Shortcut", primaryKeys = ["pkgName", "className"])
class Shortcut(
    @ColumnInfo val pkgName: String,
    @ColumnInfo val className: String,
    @ColumnInfo val label: String
) {
    companion object {
        val IGNORE_CHA = Regex("[_\\-\\s\\.]")
    }

    @Ignore
    val searchUnit = PinyinSearchUnit(label.replace(IGNORE_CHA, ""))

    init {
        PinyinUtil.parse(searchUnit)
    }
}
