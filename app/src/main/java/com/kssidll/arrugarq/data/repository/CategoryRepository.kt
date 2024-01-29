package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class CategoryRepository(private val dao: CategoryDao): CategoryRepositorySource {
    // Create

    override suspend fun insert(productCategory: ProductCategory): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertAltName(
        category: ProductCategory,
        alternativeName: String
    ): Long {
        TODO("Not yet implemented")
    }

    // Update

    override suspend fun update(productCategory: ProductCategory) {
        TODO("Not yet implemented")
    }

    override suspend fun update(productCategories: List<ProductCategory>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAltName(
        id: Long,
        alternativeName: String
    ) {
        TODO("Not yet implemented")
    }

    // Delete

    override suspend fun delete(productCategory: ProductCategory) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(productCategories: List<ProductCategory>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAltName(alternativeName: ProductCategoryAltName) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAltName(alternativeNames: List<ProductCategoryAltName>) {
        TODO("Not yet implemented")
    }

    // Read

    override suspend fun get(categoryId: Long): ProductCategory? {
        TODO("Not yet implemented")
    }

    override fun totalSpentFlow(category: ProductCategory): Flow<Float> {
        TODO("Not yet implemented")
    }

    override fun totalSpentByDayFlow(category: ProductCategory): Flow<List<ItemSpentByTime>> {
        TODO("Not yet implemented")
    }

    override fun totalSpentByWeekFlow(category: ProductCategory): Flow<List<ItemSpentByTime>> {
        TODO("Not yet implemented")
    }

    override fun totalSpentByMonthFlow(category: ProductCategory): Flow<List<ItemSpentByTime>> {
        TODO("Not yet implemented")
    }

    override fun totalSpentByYearFlow(category: ProductCategory): Flow<List<ItemSpentByTime>> {
        TODO("Not yet implemented")
    }

    override suspend fun fullItems(
        category: ProductCategory,
        count: Int,
        offset: Int
    ): List<FullItem> {
        TODO("Not yet implemented")
    }

    override fun totalSpentByCategoryFlow(): Flow<List<ItemSpentByCategory>> {
        TODO("Not yet implemented")
    }

    override fun totalSpentByCategoryByMonthFlow(
        year: Int,
        month: Int
    ): Flow<List<ItemSpentByCategory>> {
        TODO("Not yet implemented")
    }

    override fun allFlow(): Flow<List<ProductCategory>> {
        TODO("Not yet implemented")
    }

    override fun allWithAltNamesFlow(): Flow<List<ProductCategoryWithAltNames>> {
        TODO("Not yet implemented")
    }
}