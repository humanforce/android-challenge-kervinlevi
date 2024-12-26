package com.humanforce.humanforceandroidengineeringchallenge.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Created by kervinlevi on 26/12/24
 */
@Dao
interface FavoriteLocationDao {
    @Query("SELECT * FROM favoritelocationentity")
    fun getFavoriteLocations(): Flow<List<FavoriteLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteLocationEntity): Long

    @Query("DELETE FROM favoritelocationentity")
    suspend fun clear()
}
