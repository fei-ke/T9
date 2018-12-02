package com.fei_ke.t9.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fei_ke.t9.Shortcut

@Database(entities = [Shortcut::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getShortcutDao(): ShortcutDao
}