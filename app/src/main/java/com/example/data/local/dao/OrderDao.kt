package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.OrderEntity
import com.example.data.model.OrderStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY reservedAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY reservedAt DESC")
    fun getOrdersByCustomer(customerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE merchantId = :merchantId ORDER BY reservedAt DESC")
    fun getOrdersByMerchant(merchantId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderDirect(orderId: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE pickupPin = :pin OR qrPayload = :qr LIMIT 1")
    suspend fun findOrderByPinOrQr(pin: String, qr: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status, completedAt = :completedAt WHERE id = :orderId")
    suspend fun markOrderCompleted(orderId: String, status: OrderStatus = OrderStatus.COMPLETED, completedAt: Long = System.currentTimeMillis()): Int

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Int

    @Query("UPDATE orders SET rating = :rating, reviewText = :reviewText, reviewTags = :reviewTags WHERE id = :orderId")
    suspend fun updateOrderFeedback(orderId: String, rating: Int, reviewText: String, reviewTags: String): Int
}
