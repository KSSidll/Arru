package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arru.data.data.ProductVariantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductVariantEntityDao {
    // Create

    @Insert suspend fun insert(entity: ProductVariantEntity): Long

    // Update

    @Update suspend fun update(entity: ProductVariantEntity)

    @Update suspend fun update(entity: List<ProductVariantEntity>)

    // Delete

    @Delete suspend fun delete(entity: ProductVariantEntity)

    @Delete suspend fun delete(entity: List<ProductVariantEntity>)

    // Read

    @Query(
        "SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.id = :id"
    )
    fun get(id: Long): Flow<ProductVariantEntity?>

    @Query(
        "SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE (:includeGlobal AND ProductVariantEntity.productEntityId IS NULL) OR ProductVariantEntity.productEntityId = :productId"
    )
    fun byProduct(productId: Long, includeGlobal: Boolean): Flow<List<ProductVariantEntity>>

    @Query(
        "SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.name = :name"
    )
    fun byName(name: String): Flow<List<ProductVariantEntity>>

    @Query(
        "SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ((:includeGlobal AND ProductVariantEntity.productEntityId IS NULL) OR (ProductVariantEntity.productEntityId = :productId)) AND ProductVariantEntity.name = :name"
    )
    fun byProductAndName(
        productId: Long?,
        name: String,
        includeGlobal: Boolean,
    ): Flow<ProductVariantEntity?>
}
