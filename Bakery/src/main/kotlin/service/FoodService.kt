package org.example.service

import org.example.models.ProductModel
import org.example.repository.ProductRepository

class ProductService(private val repository: ProductRepository) {

    fun getProducts(): List<ProductModel> { return repository.getProducts() }
}