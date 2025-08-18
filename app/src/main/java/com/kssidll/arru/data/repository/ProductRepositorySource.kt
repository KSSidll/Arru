package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import com.kssidll.arru.domain.data.data.ProductPriceByShopByVariantByProducerByTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProductRepositorySource {
    companion object {
        sealed class InsertResult(
            val id: Long? = null,
            val error: Errors? = null
        ) {
            class Success(id: Long): InsertResult(id)
            class Error(error: Errors): InsertResult(error = error)

            fun isError(): Boolean = this is Error
            fun isNotError(): Boolean = isError().not()

            sealed class Errors
            data object InvalidName: Errors()
            data object DuplicateName: Errors()
            data object InvalidCategoryId: Errors()
            data object InvalidProducerId: Errors()
        }

        sealed class UpdateResult(
            val error: Errors? = null
        ) {
            data object Success: UpdateResult()
            class Error(error: Errors): UpdateResult(error = error)

            fun isError(): Boolean = this is Error
            fun isNotError(): Boolean = isError().not()

            sealed class Errors
            data object InvalidId: Errors()
            data object InvalidName: Errors()
            data object DuplicateName: Errors()
            data object InvalidCategoryId: Errors()
            data object InvalidProducerId: Errors()
        }

        sealed class MergeResult(
            val error: Errors? = null
        ) {
            data object Success: MergeResult()
            class Error(error: Errors): MergeResult(error = error)

            fun isError(): Boolean = this is Error
            fun isNotError(): Boolean = isError().not()

            sealed class Errors
            data object InvalidProduct: Errors()
            data object InvalidMergingInto: Errors()
        }

        sealed class DeleteResult(
            val error: Errors? = null
        ) {
            data object Success: DeleteResult()
            class Error(error: Errors): DeleteResult(error = error)

            fun isError(): Boolean = this is Error
            fun isNotError(): Boolean = isError().not()

            sealed class Errors
            data object InvalidId: Errors()
            data object DangerousDelete: Errors()
        }
    }

    // Create

    /**
     * Inserts [ProductEntity]
     * @param name name of the [ProductEntity] to insert
     * @param categoryId id of the [ProductCategoryEntity] of the [ProductEntity] to insert
     * @param producerId id of the [ProductProducerEntity] of the [ProductEntity] to insert
     * @return [InsertResult] with id of the newly inserted [ProductEntity] or an error if any
     */
    suspend fun insert(
        name: String,
        categoryId: Long,
        producerId: Long?
    ): InsertResult

    // Update

    /**
     * Updates [ProductEntity] with [id] id to provided [name], [categoryId] and [producerId]
     * @param id id to match [ProductEntity]
     * @param name name to update the matching [ProductEntity] to
     * @param categoryId id of the [ProductCategoryEntity] to update the matching [ProductEntity] to
     * @param producerId id of the [ProductProducerEntity] to update the matching [ProductEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        id: Long,
        name: String,
        categoryId: Long,
        producerId: Long?
    ): UpdateResult

    /**
     * Merges [entity] into [mergingInto]
     * @param entity [ProductEntity] to merge
     * @param mergingInto [ProductEntity] to merge the [entity] into
     * @return [MergeResult] with the result
     */
    suspend fun merge(
        entity: ProductEntity,
        mergingInto: ProductEntity,
    ): MergeResult

    // Delete

    /**
     * Deletes [ProductEntity] matching [id]
     * @param id id of the [ProductEntity] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        id: Long,
        force: Boolean
    ): DeleteResult

    // Read

    /**
     * @param id id of the [ProductEntity]
     * @return [ProductEntity] matching [id] id or null if none match
     */
    fun get(id: Long): Flow<ProductEntity?>

    /**
     * @param id id of the [ProductEntity]
     * @return float representing total spending for [ProductEntity] matching [id] id or null if none match
     */
    fun totalSpent(id: Long): Flow<Float?>

    /**
     * @param id id of the [ProductEntity]
     * @return [PagingData] of [Item] that is of [ProductEntity] [id]
     */
    fun itemsFor(id: Long): Flow<PagingData<Item>>

    /**
     * @param id id of the [ProductEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by day
     */
    fun totalSpentByDay(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by week
     */
    fun totalSpentByWeek(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by month
     */
    fun totalSpentByMonth(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by year
     */
    fun totalSpentByYear(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductEntity]
     * @return List of [ProductPriceByShopByVariantByProducerByTime] representing average spending partitioned by shop, variant, producer and day
     */
    fun averagePriceByShopByVariantByProducerByDay(id: Long): Flow<ImmutableList<ProductPriceByShopByVariantByProducerByTime>>












    /**
     * @return list of all [ProductEntity]
     */
    fun all(): Flow<ImmutableList<ProductEntity>>

    /**
     * @param entity [ProductEntity] to match the [ItemEntity] with
     * @return newest [ItemEntity] that matches [entity], null if none match
     */
    suspend fun newestItem(entity: ProductEntity): ItemEntity?
}