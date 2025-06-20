package com.example.teamproject8.WJKfile.RoomDB.Logs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface LogsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertItem(logsEntity: LogsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertItems(logsEntityItems: List<LogsEntity>)

    @Query("SELECT * FROM logs_table ORDER BY id ASC")
    fun GetAllItems(): Flow<List<LogsEntity>>

    @Query("SELECT * FROM logs_table WHERE id = :itemId")
    suspend fun GetItemById(itemId: Int): LogsEntity?

    @Query("SELECT * FROM logs_table WHERE arrivalTime BETWEEN :startDate AND :endDate ORDER BY arrivalTime ASC")
    suspend fun getItemsByBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<LogsEntity>

    /*@Query("SELECT * FROM logs_table WHERE arrivalTime.year = :targetYear AND arrivalTime.month = :month ORDER BY arrivalTime ASC")
    suspend fun getItemsByDate(targetYear: Int, targetMonth: Int, targetDay: Int): List<LogsEntity>*/

    @Update
    suspend fun UpdateItem(logsEntity: LogsEntity): Int

    @Delete
    suspend fun DeleteItem(logsEntity: LogsEntity): Int

    @Query("DELETE FROM logs_table WHERE id = :itemId")
    suspend fun DeleteItemById(itemId: Int): Int

    @Query("DELETE FROM logs_table")
    suspend fun DeleteAllItems()
}