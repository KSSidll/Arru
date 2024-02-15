package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.paging.*
import com.kssidll.arrugarq.data.repository.ProductRepositorySource.Companion.AltInsertResult
import com.kssidll.arrugarq.data.repository.ProductRepositorySource.Companion.AltUpdateResult
import com.kssidll.arrugarq.data.repository.ProductRepositorySource.Companion.DeleteResult
import com.kssidll.arrugarq.data.repository.ProductRepositorySource.Companion.InsertResult
import com.kssidll.arrugarq.data.repository.ProductRepositorySource.Companion.MergeResult
import com.kssidll.arrugarq.data.repository.ProductRepositorySource.Companion.UpdateResult
import kotlinx.coroutines.flow.*

class ProductRepository(private val dao: ProductDao): ProductRepositorySource {
    // Create

    override suspend fun insert(
        name: String,
        categoryId: Long,
        producerId: Long?
    ): InsertResult {
        val product = Product(
            categoryId,
            producerId,
            name
        )

        if (categoryId == Product.INVALID_CATEGORY_ID || dao.categoryById(categoryId) == null) {
            return InsertResult.Error(InsertResult.InvalidCategoryId)
        }

        if (producerId != null && dao.producerById(producerId) == null) {
            return InsertResult.Error(InsertResult.InvalidProducerId)
        }

        if (product.validName()
                .not()
        ) {
            return InsertResult.Error(InsertResult.InvalidName)
        }

        val other = dao.byName(product.name)

        if (other != null) {
            return InsertResult.Error(InsertResult.DuplicateName)
        }

        return InsertResult.Success(dao.insert(product))
    }

    override suspend fun insertAltName(
        product: Product,
        alternativeName: String
    ): AltInsertResult {
        if (dao.get(product.id) != product) {
            return AltInsertResult.Error(AltInsertResult.InvalidId)
        }

        val productAltName = ProductAltName(
            product = product,
            name = alternativeName,
        )

        if (productAltName.validName()
                .not()
        ) {
            return AltInsertResult.Error(AltInsertResult.InvalidName)
        }

        val others = dao.altNames(product.id)

        if (productAltName.name in others.map { it.name }) {
            return AltInsertResult.Error(AltInsertResult.DuplicateName)
        }

        return AltInsertResult.Success(dao.insertAltName(productAltName))
    }

    // Update

    override suspend fun update(
        productId: Long,
        name: String,
        categoryId: Long,
        producerId: Long?
    ): UpdateResult {
        if (dao.get(productId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidId)
        }

        if (dao.categoryById(categoryId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidCategoryId)
        }

        if (producerId != null && dao.producerById(producerId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidProducerId)
        }

        val product = Product(
            id = productId,
            name = name.trim(),
            categoryId = categoryId,
            producerId = producerId
        )

        if (product.validName()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidName)
        }

        val other = dao.byName(product.name)

        if (other != null) {
            if (other.id == product.id) {
                return UpdateResult.Success
            }

            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(product)

        return UpdateResult.Success
    }

    override suspend fun updateAltName(
        alternativeNameId: Long,
        productId: Long,
        name: String
    ): AltUpdateResult {
        if (dao.getAltName(alternativeNameId) == null) {
            return AltUpdateResult.Error(AltUpdateResult.InvalidId)
        }

        if (dao.get(productId) == null) {
            return AltUpdateResult.Error(AltUpdateResult.InvalidProductId)
        }

        val alternativeName = ProductAltName(
            id = alternativeNameId,
            productId = productId,
            name = name.trim()
        )

        if (alternativeName.validName()
                .not()
        ) {
            return AltUpdateResult.Error(AltUpdateResult.InvalidName)
        }

        val others = dao.altNames(productId)

        if (alternativeName.name in others.map { it.name }) {
            if (alternativeName.id in others.map { it.id }) {
                return AltUpdateResult.Success
            }

            return AltUpdateResult.Error(AltUpdateResult.DuplicateName)
        }

        dao.updateAltName(alternativeName)

        return AltUpdateResult.Success
    }

    override suspend fun merge(
        product: Product,
        mergingInto: Product
    ): MergeResult {
        if (dao.get(product.id) == null) {
            return MergeResult.Error(MergeResult.InvalidProduct)
        }

        if (dao.get(mergingInto.id) == null) {
            return MergeResult.Error(MergeResult.InvalidMergingInto)
        }

        val items = dao.getItems(product.id)
        items.forEach { it.productId = mergingInto.id }
        dao.updateItems(items)

        dao.deleteAltName(dao.altNames(product.id))
        dao.delete(product)

        return MergeResult.Success
    }

    // Delete

    override suspend fun delete(
        productId: Long,
        force: Boolean
    ): DeleteResult {
        val product = dao.get(productId) ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val variants = dao.variants(productId)
        val altNames = dao.altNames(productId)
        val items = dao.getItems(productId)
        val transactionBasketItems = dao.getTransactionBasketItems(productId)

        if (!force && (variants.isNotEmpty() || altNames.isNotEmpty() || items.isNotEmpty())) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteTransactionBasketItems(transactionBasketItems)
            dao.deleteItems(items)
            dao.deleteAltName(altNames)
            dao.deleteVariants(variants)
            dao.delete(product)
        }

        return DeleteResult.Success
    }

    override suspend fun deleteAltName(alternativeNameId: Long): DeleteResult {
        val altName =
            dao.getAltName(alternativeNameId) ?: return DeleteResult.Error(DeleteResult.InvalidId)

        dao.deleteAltName(altName)

        return DeleteResult.Success
    }

    // Read

    override suspend fun get(productId: Long): Product? {
        return dao.get(productId)
    }

    override fun totalSpentFlow(product: Product): Flow<Long> {
        return dao.totalSpentFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByDayFlow(product: Product): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByDayFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByWeekFlow(product: Product): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByWeekFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByMonthFlow(product: Product): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByMonthFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByYearFlow(product: Product): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByYearFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun fullItemsPagedFlow(product: Product): Flow<PagingData<FullItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FullItemPagingSource(
                    query = { start, loadSize ->
                        dao.fullItems(
                            product.id,
                            loadSize,
                            start
                        )
                    }
                )
            }
        )
            .flow
    }

    override suspend fun newestItem(product: Product): Item? {
        return dao.newestItem(product.id)
    }

    override fun allWithAltNamesFlow(): Flow<List<ProductWithAltNames>> {
        return dao.allWithAltNamesFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun averagePriceByVariantByShopByMonthFlow(product: Product): Flow<List<ProductPriceByShopByTime>> {
        return dao.averagePriceByVariantByShopByMonthFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun allFlow(): Flow<List<Product>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
    }
}