package com.kssidll.arrugarq.data.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arrugarq.data.data.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM item ORDER BY id ASC")
    suspend fun getAll(): List<Item>

    @Query("SELECT * FROM item ORDER BY id ASC")
    fun getAllFlow(): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE id == :id")
    suspend fun get(id: Long): Item?

    @Query("SELECT * FROM item WHERE id == :id")
    fun getFlow(id: Long): Flow<Item>

    @Query("SELECT * FROM item ORDER BY id DESC LIMIT 1")
    suspend fun getLast(): Item?

    @Query("SELECT * FROM item ORDER BY id DESC LIMIT 1")
    fun getLastFlow(): Flow<Item>

    @Query("SELECT * FROM item WHERE productId == :productId")
    suspend fun getByProductId(productId: Long): List<Item>

    @Query("SELECT * FROM item WHERE productId == :productId")
    fun getByProductIdFlow(productId: Long): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE productId == :productId ORDER BY id DESC LIMIT 1")
    suspend fun getLastByProductId(productId: Long): Item?

    @Query("SELECT * FROM item WHERE productId == :productId ORDER BY id DESC LIMIT 1")
    fun getLastByProductIdFlow(productId: Long): Flow<Item?>

    @Query("SELECT * FROM item WHERE variantId == :variantId")
    suspend fun getByVariant(variantId: Long): List<Item>

    @Query("SELECT * FROM item WHERE variantId == :variantId")
    fun getByVariantFlow(variantId: Long): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE shopId == :shopId")
    suspend fun getByShopId(shopId: Long): List<Item>

    @Query("SELECT * FROM item WHERE shopId == :shopId")
    fun getByShopIdFlow(shopId: Long): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE date > :date")
    suspend fun getNewerThan(date: Long): List<Item>

    @Query("SELECT * FROM item WHERE date > :date")
    fun getNewerThanFlow(date: Long): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE date < :date")
    suspend fun getOlderThan(date: Long): List<Item>

    @Query("SELECT * FROM item WHERE date < :date")
    fun getOlderThanFlow(date: Long): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE date > :lowerBoundDate AND date < :higherBoundDate")
    suspend fun getBetweenDates(lowerBoundDate: Long, higherBoundDate: Long): List<Item>

    @Query("SELECT * FROM item WHERE date > :lowerBoundDate AND date < :higherBoundDate")
    fun getBetweenDatesFlow(lowerBoundDate: Long, higherBoundDate: Long): Flow<List<Item>>

    @Insert
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete (item: Item)
}