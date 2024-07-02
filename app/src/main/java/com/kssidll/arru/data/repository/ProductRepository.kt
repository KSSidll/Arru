package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ProductDao
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.paging.FullItemPagingSource
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.AltInsertResult
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.AltUpdateResult
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
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

        if (other != null && other.id != product.id) {
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

        if (alternativeName.name in others.map { it.name } && alternativeName.id !in others.map { it.id }) {
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
        val variants = dao.variants(product.id)
        val mergingIntoVariantsNames = dao.variants(mergingInto.id)
            .map { it.name }

        val newVariants = variants.filterNot { it.name in mergingIntoVariantsNames }
        val duplicateVariants = variants.filter { it.name in mergingIntoVariantsNames }

        // update new variants
        newVariants.forEach { it.productId = mergingInto.id }
        dao.updateVariants(newVariants)

        items.forEach {
            it.productId = mergingInto.id

            // update id in case it's part of the duplicate variants
            if (it.variantId != null && it.variantId in duplicateVariants.map { variant -> variant.id }) {
                it.variantId = dao.variantByName(
                    it.productId,
                    dao.variantById(it.variantId!!)!!.name
                )!!.id
            }
        }
        dao.updateItems(items)

        dao.deleteAltName(dao.altNames(product.id))
        dao.deleteVariants(duplicateVariants)
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

        if (!force && (variants.isNotEmpty() || altNames.isNotEmpty() || items.isNotEmpty())) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
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

    override fun getFlow(productId: Long): Flow<Data<Product?>> {
        return dao.getFlow(productId)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<Product?>() }
    }

    override fun totalSpentFlow(product: Product): Flow<Data<Float?>> {
        return dao.totalSpentFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
            .map {
                Data.Loaded(
                    it?.toFloat()
                        ?.div(ItemEntity.PRICE_DIVISOR * ItemEntity.QUANTITY_DIVISOR)
                )
            }
            .onStart { Data.Loading<Long>() }
    }

    override fun totalSpentByDayFlow(product: Product): Flow<Data<List<ItemSpentByTime>>> {
        return dao.totalSpentByDayFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<ItemSpentByTime>>() }
    }

    override fun totalSpentByWeekFlow(product: Product): Flow<Data<List<ItemSpentByTime>>> {
        return dao.totalSpentByWeekFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<ItemSpentByTime>>() }
    }

    override fun totalSpentByMonthFlow(product: Product): Flow<Data<List<ItemSpentByTime>>> {
        return dao.totalSpentByMonthFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<ItemSpentByTime>>() }
    }

    override fun totalSpentByYearFlow(product: Product): Flow<Data<List<ItemSpentByTime>>> {
        return dao.totalSpentByYearFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<ItemSpentByTime>>() }
    }

    override fun fullItemsPagedFlow(product: Product): Flow<PagingData<Item>> {
        return Pager(
            config = PagingConfig(pageSize = 3),
            initialKey = 0,
            pagingSourceFactory = {
                FullItemPagingSource(
                    query = { start, loadSize ->
                        dao.fullItems(
                            product.id,
                            loadSize,
                            start
                        )
                    },
                    itemsBefore = {
                        dao.countItemsBefore(
                            it,
                            product.id
                        )
                    },
                    itemsAfter = {
                        dao.countItemsAfter(
                            it,
                            product.id
                        )
                    },
                )
            }
        )
            .flow
    }

    override suspend fun newestItem(product: Product): ItemEntity? {
        return dao.newestItem(product.id)
    }

    override fun allWithAltNamesFlow(): Flow<Data<List<ProductWithAltNames>>> {
        return dao.allWithAltNamesFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<ProductCategoryWithAltNames>>() }
    }

    override fun averagePriceByVariantByShopByMonthFlow(product: Product): Flow<Data<List<ProductPriceByShopByTime>>> {
        return dao.averagePriceByVariantByShopByMonthFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<ProductPriceByShopByTime>>() }
    }

    override fun allFlow(): Flow<Data<List<Product>>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<Product>>() }
    }
}