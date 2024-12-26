package com.humanforce.humanforceandroidengineeringchallenge.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by kervinlevi on 26/12/24
 */
@Database(entities = [FavoriteLocationEntity::class], version = 1)
abstract class WeatherAppDb: RoomDatabase() {

    abstract fun favoriteLocationDao(): FavoriteLocationDao
}
