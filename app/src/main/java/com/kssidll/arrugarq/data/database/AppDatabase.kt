package com.kssidll.arrugarq.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kssidll.arrugarq.data.dao.ItemDao
import com.kssidll.arrugarq.data.data.Item

@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getItemDao(): ItemDao
}