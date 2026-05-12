package org.bakery_tm.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.bakery_tm.routes.locationRoutes
import org.bakery_tm.routes.productsRoutes
import org.bakery_tm.routes.userRoutes

fun Application.configureRouting() {
    routing {
        locationRoutes()
        productsRoutes()
        userRoutes()
    }
}