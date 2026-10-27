package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [GameProfileEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameProfileDao(): GameProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zavrangoffz_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDefaultProfiles(database.gameProfileDao())
                    }
                }
            }
        }

        suspend fun populateDefaultProfiles(dao: GameProfileDao) {
            val defaults = listOf(
                GameProfileEntity(
                    profileName = "One-Tap Full Red",
                    playstyleTag = "Headshot Precision",
                    general = 195,
                    redDot = 188,
                    scope2x = 175,
                    scope4x = 160,
                    scopeSniper = 120,
                    freeLook = 150,
                    dpi = 720,
                    fireButtonSize = 52,
                    isFavorite = true
                ),
                GameProfileEntity(
                    profileName = "Rush SMG & Shotgun",
                    playstyleTag = "Aggressive Close-Range",
                    general = 200,
                    redDot = 196,
                    scope2x = 182,
                    scope4x = 155,
                    scopeSniper = 98,
                    freeLook = 180,
                    dpi = 840,
                    fireButtonSize = 60,
                    isFavorite = false
                ),
                GameProfileEntity(
                    profileName = "Long Range AWM Sniper",
                    playstyleTag = "Sniper Support",
                    general = 165,
                    redDot = 145,
                    scope2x = 155,
                    scope4x = 140,
                    scopeSniper = 135,
                    freeLook = 130,
                    dpi = 560,
                    fireButtonSize = 48,
                    isFavorite = false
                ),
                GameProfileEntity(
                    profileName = "Galaxy M15 90Hz Balanced",
                    playstyleTag = "AMOLED 90Hz Tuned",
                    general = 180,
                    redDot = 168,
                    scope2x = 162,
                    scope4x = 150,
                    scopeSniper = 110,
                    freeLook = 140,
                    dpi = 600,
                    fireButtonSize = 55,
                    isFavorite = true
                )
            )
            dao.insertProfiles(defaults)
        }
    }
}
