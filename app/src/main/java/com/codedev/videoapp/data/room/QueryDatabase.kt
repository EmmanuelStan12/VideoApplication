package com.codedev.videoapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AutoCompleteItem::class], version = 1)
abstract class QueryDatabase: RoomDatabase() {

    abstract val queryDao: QueryDao
}