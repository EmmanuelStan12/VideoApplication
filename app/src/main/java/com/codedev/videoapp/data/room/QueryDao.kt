package com.codedev.videoapp.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QueryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AutoCompleteItem)

    @Query("SELECT * FROM autocompleteitem")
    fun getAllQueries(): Flow<List<AutoCompleteItem>>

    @Query("SELECT * FROM autocompleteitem WHERE text LIKE :query")
    suspend fun searchQuery(query: String): Flow<List<AutoCompleteItem>>

    @Query("SELECT * FROM autocompleteitem WHERE id = :id")
    suspend fun getQuery(id: Int): AutoCompleteItem

    @Delete
    suspend fun deleteQuery(item: AutoCompleteItem)
}