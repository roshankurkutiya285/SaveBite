package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FoodPackageEntity
import com.example.data.model.PackageCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodPackageDao {
    @Query("SELECT * FROM food_packages WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActivePackages(): Flow<List<FoodPackageEntity>>

    @Query("SELECT * FROM food_packages WHERE id = :packageId LIMIT 1")
    fun getPackageById(packageId: String): Flow<FoodPackageEntity?>

    @Query("SELECT * FROM food_packages WHERE merchantId = :merchantId ORDER BY createdAt DESC")
    fun getPackagesByMerchant(merchantId: String): Flow<List<FoodPackageEntity>>

    @Query("SELECT * FROM food_packages WHERE category = :category AND isActive = 1")
    fun getPackagesByCategory(category: PackageCategory): Flow<List<FoodPackageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(pkg: FoodPackageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackages(packages: List<FoodPackageEntity>)

    @Update
    suspend fun updatePackage(pkg: FoodPackageEntity)

    @Query("UPDATE food_packages SET quantityAvailable = quantityAvailable - :quantity WHERE id = :packageId AND quantityAvailable >= :quantity")
    suspend fun decrementStock(packageId: String, quantity: Int): Int

    @Query("UPDATE food_packages SET quantityAvailable = quantityAvailable + :quantity WHERE id = :packageId")
    suspend fun incrementStock(packageId: String, quantity: Int): Int
}
