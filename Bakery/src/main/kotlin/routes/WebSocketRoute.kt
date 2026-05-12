package org.example.routes

import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket
import org.example.WsSessionManager
import kotlin.text.toInt

fun Route.orderWebSockets() {
    webSocket("/orders/{userId}") {
        val userId = call.parameters["userId"]!!.toInt()
        WsSessionManager.registerWsSession(userId, this)
        try {
            for (frame in incoming) { }
        } finally {
            WsSessionManager.removeWsSession(userId, this)
        }
    }
}