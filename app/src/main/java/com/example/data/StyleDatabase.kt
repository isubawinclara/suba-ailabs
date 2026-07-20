package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UserProfile::class, WardrobeItem::class, SavedLook::class],
    version = 1,
    exportSchema = false
)
abstract class StyleDatabase : RoomDatabase() {
    abstract fun styleDao(): StyleDao

    companion object {
        @Volatile
        private var INSTANCE: StyleDatabase? = null

        fun getDatabase(context: Context): StyleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StyleDatabase::class.java,
                    "style_sense_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
