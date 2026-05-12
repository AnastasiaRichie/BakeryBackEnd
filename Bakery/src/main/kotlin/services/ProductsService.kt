package org.bakery_tm.services

import org.bakery_tm.models.ExtendedProductModel
import org.bakery_tm.models.ProductModel
import org.bakery_tm.repositories.ProductsRepository

class ProductsService(private val productsRepository: ProductsRepository) {

    /**
     * Add new
     * @param product product data
     */
    fun addNewProduct(product: ExtendedProductModel): Long = productsRepository.addNewProduct(product)

    /**
     * Get products
     */
    fun getProducts(): List<ProductModel> = productsRepository.getProducts()

    /**
     * Get product details
     * @param productId product id
     */
    fun getProductById(productId: Long): ExtendedProductModel? = productsRepository.getProductById(productId)
}