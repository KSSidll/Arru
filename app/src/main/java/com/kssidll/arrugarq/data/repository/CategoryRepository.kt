package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.paging.*
import com.kssidll.arrugarq.data.repository.CategoryRepositorySource.Companion.AltInsertResult
import com.kssidll.arrugarq.data.repository.CategoryRepositorySource.Companion.InsertResult
import kotlinx.coroutines.flow.*

class CategoryRepository(private val dao: CategoryDao): CategoryRepositorySource {
    // Create

    override suspend fun insert(name: String): InsertResult {
        val category = ProductCategory(name)

        if (category.validName()
                .not()
        ) {
            return InsertResult.Error(InsertResult.InvalidName)
        }

        val other = dao.byName(category.name)

        if (other != null) {
            return InsertResult.Error(InsertResult.DuplicateName)
        }

        return InsertResult.Success(dao.insert(category))
    }

    override suspend fun insertAltName(
        category: ProductCategory,
        alternativeName: String
    ): AltInsertResult {
        if (dao.get(category.id) != category) {
            return AltInsertResult.Error(AltInsertResult.InvalidId)
        }

        val categoryAltName = ProductCategoryAltName(
            category = category,
            name = alternativeName,
        )

        if (categoryAltName.validName()
                .not()
        ) {
            return AltInsertResult.Error(AltInsertResult.InvalidName)
        }

        val others = dao.altNames(category.id)

        if (categoryAltName.name in others.map { it.name }) {
            return AltInsertResult.Error(AltInsertResult.DuplicateName)
        }

        return AltInsertResult.Success(dao.insertAltName(categoryAltName))
    }

    // Update

    override suspend fun update(productCategory: ProductCategory) {
        dao.update(productCategory)
    }

    override suspend fun update(productCategories: List<ProductCategory>) {
        dao.update(productCategories)
    }

    override suspend fun updateAltName(alternativeName: ProductCategoryAltName) {
        dao.updateAltName(alternativeName)
    }

    // Delete

    override suspend fun delete(productCategory: ProductCategory) {
        dao.delete(productCategory)
    }

    override suspend fun delete(productCategories: List<ProductCategory>) {
        dao.delete(productCategories)
    }

    override suspend fun deleteAltName(alternativeName: ProductCategoryAltName) {
        dao.updateAltName(alternativeName)
    }

    override suspend fun deleteAltName(alternativeNames: List<ProductCategoryAltName>) {
        dao.deleteAltName(alternativeNames)
    }

    // Read

    override suspend fun get(categoryId: Long): ProductCategory? {
        return dao.get(categoryId)
    }

    override fun totalSpentFlow(category: ProductCategory): Flow<Long> {
        return dao.totalSpentFlow(category.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByDayFlow(category: ProductCategory): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByDayFlow(category.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByWeekFlow(category: ProductCategory): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByWeekFlow(category.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByMonthFlow(category: ProductCategory): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByMonthFlow(category.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByYearFlow(category: ProductCategory): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByYearFlow(category.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun fullItemsPagedFlow(category: ProductCategory): Flow<PagingData<FullItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FullItemPagingSource(
                    query = { start, loadSize ->
                        dao.fullItems(
                            category.id,
                            loadSize,
                            start
                        )
                    }
                )
            }
        )
            .flow
    }

    override fun totalSpentByCategoryFlow(): Flow<List<ItemSpentByCategory>> {
        return dao.totalSpentByCategoryFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByCategoryByMonthFlow(
        year: Int,
        month: Int
    ): Flow<List<ItemSpentByCategory>> {
        val date: String = buildString {
            append(year)
            append("-")

            val monthStr: String = if (month < 10) {
                "0$month"
            } else {
                month.toString()
            }
            append(monthStr)
        }

        return dao.totalSpentByCategoryByMonthFlow(date)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun allFlow(): Flow<List<ProductCategory>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun allWithAltNamesFlow(): Flow<List<ProductCategoryWithAltNames>> {
        return dao.allWithAltNamesFlow()
            .cancellable()
            .distinctUntilChanged()
    }
}