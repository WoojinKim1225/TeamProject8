package com.example.teamproject8.WJKfile.RoomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface NavigationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertItem(navigationEntity: NavigationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertItems(navigationEntityItems: List<NavigationEntity>)

    @Query("SELECT * FROM navigation_table ORDER BY id ASC")
    fun GetAllItems(): Flow<List<NavigationEntity>>

    @Query("SELECT * FROM navigation_table WHERE id = :itemId")
    suspend fun GetItemById(itemId: Int): NavigationEntity?

    @Query("SELECT * FROM navigation_table WHERE route = :route")
    suspend fun GetItemByRoute(route: String): NavigationEntity?

    @Update
    suspend fun UpdateItem(navigationEntity: NavigationEntity): Int

    @Delete
    suspend fun DeleteItem(navigationEntity: NavigationEntity): Int

    @Query("DELETE FROM navigation_table WHERE id = :itemId")
    suspend fun DeleteItemById(itemId: Int): Int

    @Query("DELETE FROM navigation_table")
    suspend fun DeleteAllItems()
}