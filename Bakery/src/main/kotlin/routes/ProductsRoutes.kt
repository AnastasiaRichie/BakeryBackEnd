package org.bakery_tm.routes

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bakery_tm.models.ExtendedProductModel
import org.bakery_tm.services.ProductsService
import org.koin.ktor.ext.inject

fun Route.productsRoutes() {

    val productsService by inject<ProductsService>()

    get("/products") {
        val products = productsService.getProducts()
        call.respond(products)
    }

    get("/products/{id}") {
        val productId = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Invalid product ID")
        val product = productsService.getProductById(productId) ?: throw NotFoundException("Product not found")
        call.respond(product)
    }

    post("/product") {
        val product = call.receive<ExtendedProductModel>()
        val createdProduct = productsService.addNewProduct(product)
        call.respond(createdProduct)
    }

}