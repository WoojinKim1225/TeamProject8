package com.example.teamproject8.WJKfile.ViewModel

import com.example.teamproject8.WJKfile.RoomDB.*

class NavigationRepo(private val db:NavigationDatabase) {
    val dao = db.getItemDao()

    suspend fun InsertItem(itemEntity: NavigationEntity) {
        dao.InsertItem(itemEntity)
    }

    suspend fun UpdateItem(itemEntity: NavigationEntity) {
        dao.UpdateItem(itemEntity)
    }

    suspend fun DeleteItem(itemEntity: NavigationEntity) {
        dao.DeleteItem(itemEntity)
    }

    fun getAllItems() = dao.GetAllItems()
}