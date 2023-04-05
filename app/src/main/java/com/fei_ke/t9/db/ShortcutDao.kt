package com.fei_ke.t9.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fei_ke.t9.Shortcut
import kotlinx.coroutines.flow.Flow

@Dao
interface ShortcutDao {
    @Query("SELECT * FROM Shortcut")
    fun getAllShortcut(): Flow<List<Shortcut>>

    @Query("DELETE  FROM Shortcut WHERE pkgName = :pkgName")
    fun deleteByPackageName(pkgName: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg shortcut: Shortcut)
}