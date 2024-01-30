package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class CategoryRepository(private val dao: CategoryDao): CategoryRepositorySource {
    // Create

    override suspend fun insert(productCategory: ProductCategory): Long {
        return dao.insert(productCategory)
    }

    override suspend fun insertAltName(
        category: ProductCategory,
        alternativeName: String
    ): Long {
        return dao.insertAltName(
            ProductCategoryAltName(
                category = category,
                name = alternativeName,
            )
        )
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

    override suspend fun fullItems(
        category: ProductCategory,
        count: Int,
        offset: Int
    ): List<FullItem> {
        return dao.fullItems(
            category.id,
            count,
            offset
        )
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