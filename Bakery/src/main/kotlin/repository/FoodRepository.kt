package org.example.repository

import org.example.db.Products
import org.example.models.ProductModel
import org.example.models.toProductModel
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ProductRepository {

    fun getProducts(): List<ProductModel> = transaction {
        Products.selectAll().map { row -> row.toProductModel() }
    }
}

