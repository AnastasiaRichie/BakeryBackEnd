package org.example.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.service.ProductService

fun Route.productRoutes(productService: ProductService) {

    get("/products") {
        val notes = productService.getProducts()
        call.respond(notes)
    }
}
