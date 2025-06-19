package com.example.teamproject8.WJKfile.RoomDB

import android.content.Context
import androidx.room.Room
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.teamproject8.WJKfile.RoomDB.Converters.LatLngConverter
import com.example.teamproject8.WJKfile.RoomDB.Converters.LatLngListConverter
import com.example.teamproject8.WJKfile.RoomDB.Converters.LocalDateTimeConverter

@Database(
    entities = [NavigationEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(
    LatLngConverter::class,
    LatLngListConverter::class,
    LocalDateTimeConverter::class
)
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