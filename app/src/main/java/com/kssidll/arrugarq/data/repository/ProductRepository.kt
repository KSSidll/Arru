package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class ProductRepository(private val dao: ProductDao): ProductRepositorySource {
    override suspend fun getAll(): List<Product> {
        return dao.getAll()
    }

    override fun getAllFlow(): Flow<List<Product>> {
        return dao.getAllFlow()
    }

    override suspend fun get(id: Long): Product? {
        return dao.get(id)
    }

    override fun getFlow(id: Long): Flow<Product> {
        return dao.getFlow(id)
    }

    override suspend fun getByCategoryId(categoryId: Long): List<Product> {
        return dao.getByCategoryId(categoryId)
    }

    override fun getByCategoryIdFlow(categoryId: Long): Flow<List<Product>> {
        return dao.getByCategoryIdFlow(categoryId)
    }

    override suspend fun getByProducerId(producerId: Long): List<Product> {
        return dao.getByProducerId(producerId)
    }

    override fun getByProducerIdFlow(producerId: Long): Flow<List<Product>> {
        return dao.getByProducerIdFlow(producerId)
    }

    override suspend fun getByName(name: String): Product? {
        return dao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<Product> {
        return dao.getByNameFlow(name)
    }

    override suspend fun getByNameAndProducerId(
        name: String,
        producerId: Long?
    ): Product? {
        return dao.getByNameAndProducerId(
            name,
            producerId
        )
    }

    override suspend fun findLike(name: String): List<Product> {
        return dao.findLike(name)
    }

    override fun findLikeFlow(name: String): Flow<List<Product>> {
        return dao.findLikeFlow(name)
    }

    override suspend fun getAllWithAltNames(): List<ProductWithAltNames> {
        return dao.getAllWithAltNames()
    }

    override fun getAllWithAltNamesFlow(): Flow<List<ProductWithAltNames>> {
        return dao.getAllWithAltNamesFlow()
    }

    override suspend fun insert(product: Product): Long {
        return dao.insert(product)
    }

    override suspend fun addAltName(alternativeName: ProductAltName): Long {
        return dao.addAltName(alternativeName)
    }

    override suspend fun update(product: Product) {
        dao.update(product)
    }

    override suspend fun update(products: List<Product>) {
        dao.update(products)
    }

    override suspend fun updateAltName(alternativeName: ProductAltName) {
        dao.updateAltName(alternativeName)
    }

    override suspend fun delete(product: Product) {
        dao.delete(product)
    }

    override suspend fun delete(products: List<Product>) {
        dao.delete(products)
    }

    override suspend fun deleteAltName(alternativeName: ProductAltName) {
        dao.deleteAltName(alternativeName)
    }
}