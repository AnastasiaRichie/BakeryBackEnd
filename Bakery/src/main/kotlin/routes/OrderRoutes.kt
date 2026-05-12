package org.example.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.example.models.OrderIdModel
import org.example.models.OrderRequest
import org.example.service.OrderService

fun Route.orderRoutes(orderService: OrderService) {

    authenticate("auth-jwt") {
        get("/orders") {
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
            val orders = orderService.getOrders(userId)
            println("orders = $orders")
            call.respond(orders)
        }

        get("/orders/{id}") {
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
            val orderId = call.parameters["id"]!!.toLong()
            val order = orderService.getOrderById(userId, orderId) ?: throw Exception("Order with id $orderId not found")
            call.respond(order)
        }

        post("/orders/{id}/reorder") {
            val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")!!.asInt()
            val orderId = call.parameters["id"]!!.toLong()
            val newOrderId = orderService.reorder(orderId, userId)
            call.respond(HttpStatusCode.Created, OrderIdModel(newOrderId))
        }

        post("/order") {
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
            val order = call.receive<OrderRequest>()
            val orderId = orderService.createOrder(order.copy(userId = userId))
            call.respond(OrderIdModel(orderId))
        }
    }

    get("/ordersByEmail") {
        val email = call.request.queryParameters["email"]
        val orders = orderService.getOrdersByEmail(email.orEmpty())
        call.respond(orders)
    }

    post("/orders/{id}/received") {
        val orderId = call.parameters["id"]!!.toLong()
        orderService.markOrderReceived(orderId)
        call.respond(HttpStatusCode.OK)
    }
}

