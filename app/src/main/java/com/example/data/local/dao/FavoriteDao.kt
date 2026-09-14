package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT merchantId FROM favorites WHERE userId = :userId")
    fun getFavoriteMerchantIds(userId: String): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND merchantId = :merchantId)")
    fun isFavorite(userId: String, merchantId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND merchantId = :merchantId")
    suspend fun removeFavorite(userId: String, merchantId: String)
}
