package org.example.repository

import org.example.db.Products
import org.example.models.ProductModel
import org.example.models.toProductModel
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class ProductRepository {

    fun getProducts(): List<ProductModel> = transaction {
        Products.selectAll().map { row -> row.toProductModel() }.filter { it.isActive }
    }

    fun removeProduct(productId: Long) = transaction {
        Products.update({ Products.id eq productId }) {
            it[isActive] = false
        }
        getProducts()
    }
}

