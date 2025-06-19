package com.example.teamproject8.WJKfile.RoomDB.Logs

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.teamproject8.WJKfile.RoomDB.Converters.LatLngConverter
import com.example.teamproject8.WJKfile.RoomDB.Converters.LatLngListConverter
import com.example.teamproject8.WJKfile.RoomDB.Converters.LocalDateTimeConverter

@Database(
    entities = [LogsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    LatLngConverter::class,
    LatLngListConverter::class,
    LocalDateTimeConverter::class
)
abstract class LogsDatabase : RoomDatabase() {
    abstract fun getItemDao(): LogsDao

    companion object {
        @Volatile
        private var DBInstance: LogsDatabase? = null
        fun getDBInstance(context: Context): LogsDatabase {
            return DBInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LogsDatabase::class.java,
                    "logsDB"
                ).fallbackToDestructiveMigration(false).build()
                DBInstance = instance
                instance
            }
        }
    }
}