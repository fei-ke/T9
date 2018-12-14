package com.fei_ke.t9

import android.app.Application
import androidx.room.Room
import com.fei_ke.t9.db.AppDataBase

class App : Application() {
    companion object {
        lateinit var instance: App
        lateinit var db: AppDataBase
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        db = Room.databaseBuilder(this, AppDataBase::class.java, "database.db")
            .build()

        ShortcutLoader.init()
    }
}
