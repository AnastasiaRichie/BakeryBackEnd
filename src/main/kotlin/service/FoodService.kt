package org.example.service

import org.example.models.ProductModel
import org.example.repository.ProductRepository

class ProductService(private val repository: ProductRepository) {

    fun getProducts(): List<ProductModel> { return repository.getProducts() }

    fun removeProduct(productId: Long): List<ProductModel> { return repository.removeProduct(productId) }

    fun returnBackProduct(productId: Long): List<ProductModel> { return repository.returnBackProduct(productId) }

    fun getUnavailable(): List<Long> { return repository.getUnavailable() }
}