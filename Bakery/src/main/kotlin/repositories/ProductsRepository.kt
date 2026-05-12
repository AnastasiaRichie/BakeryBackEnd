package org.bakery_tm.repositories

import org.bakery_tm.database.Products
import org.bakery_tm.models.ExtendedProductModel
import org.bakery_tm.models.ProductModel
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ProductsRepository {

    fun init() {
        transaction {
            SchemaUtils.create(Products)
        }
    }

    /**
     * Add new
     * @param product product data
     */
    fun addNewProduct(product: ExtendedProductModel): Long = transaction {
        Products.insert {
            it[productName] = product.productName
            it[description] = product.description
            it[fullDescription] = product.fullDescription
            it[hasAllergens] = product.hasAllergens
            it[allergens] = product.allergens
            it[allergens] = product.allergens
        }[Products.id]
    }

    /**
     * Get products
     */
    fun getProducts(): List<ProductModel> = transaction {
        Products.selectAll()
            .map { product ->
                ProductModel(
                    productId = product[Products.id],
                    productName = product[Products.productName],
                    description = product[Products.description],
                    icon = product[Products.icon],
                )
            }
    }

    /**
     * Get product details
     * @param productId product id
     */
    fun getProductById(productId: Long): ExtendedProductModel? = transaction {
        Products.select { Products.id eq productId }
            .map { product ->
                ExtendedProductModel(
                    productId = product[Products.id],
                    productName = product[Products.productName],
                    description = product[Products.description],
                    fullDescription = product[Products.fullDescription],
                    hasAllergens = product[Products.hasAllergens],
                    allergens = product[Products.allergens],
                    icon = product[Products.icon],                )
            }.singleOrNull()
    }
}
