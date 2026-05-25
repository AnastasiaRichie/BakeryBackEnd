package org.example.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.service.ProductService

fun Route.productRoutes(productService: ProductService) {

    get("/products") {
        val products = productService.getProducts()
        call.respond(products)
    }

    post("product/{id}") {
        val productId = call.parameters["id"]!!.toLong()
        call.respond(productService.removeProduct(productId))
    }

    post("product/{id}/add") {
        val productId = call.parameters["id"]!!.toLong()
        call.respond(productService.returnBackProduct(productId))
    }

    get("products-unavailable") {
        call.respond(productService.getUnavailable())
    }
}
