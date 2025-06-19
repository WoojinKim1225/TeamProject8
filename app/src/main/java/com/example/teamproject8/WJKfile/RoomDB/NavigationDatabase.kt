package com.example.teamproject8.WJKfile.RoomDB

import android.content.Context
import androidx.room.Room
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(
    entities = [NavigationEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(NavigationTypeConverters::class)

abstract class NavigationDatabase : RoomDatabase() {
    abstract fun getItemDao(): NavigationDao

    companion object {
        @Volatile
        private var DBInstance: NavigationDatabase? = null
        fun getDBInstance(context: Context): NavigationDatabase {
            return DBInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                NavigationDatabase::class.java,
                                "itemdb"
                            ).fallbackToDestructiveMigration(false).build()
                DBInstance = instance
                instance
            }
        }
    }
}