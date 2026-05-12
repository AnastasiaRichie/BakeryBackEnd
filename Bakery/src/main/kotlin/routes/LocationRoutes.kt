package org.bakery_tm.routes

import io.ktor.server.routing.*
import org.bakery_tm.services.UserService
import org.koin.ktor.ext.inject

fun Route.locationRoutes() {
    val userService by inject<UserService>()

    //authenticate {
        get("/bakery_locations") {
            //TODO("Add locations")
        }
    //}
}