package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.MerchantEntity
import com.example.data.model.BusinessType
import kotlinx.coroutines.flow.Flow

@Dao
interface MerchantDao {
    @Query("SELECT * FROM merchants ORDER BY distanceKm ASC")
    fun getAllMerchants(): Flow<List<MerchantEntity>>

    @Query("SELECT * FROM merchants WHERE id = :merchantId LIMIT 1")
    fun getMerchantById(merchantId: String): Flow<MerchantEntity?>

    @Query("SELECT * FROM merchants WHERE businessType = :type ORDER BY distanceKm ASC")
    fun getMerchantsByType(type: BusinessType): Flow<List<MerchantEntity>>

    @Query("SELECT * FROM merchants WHERE userId = :userId LIMIT 1")
    fun getMerchantByUserId(userId: String): Flow<MerchantEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchant(merchant: MerchantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchants(merchants: List<MerchantEntity>)

    @Update
    suspend fun updateMerchant(merchant: MerchantEntity)
}
