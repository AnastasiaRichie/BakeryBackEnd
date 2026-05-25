package org.example.service

import org.example.models.OrderRequest
import org.example.models.OrderResponse
import org.example.repository.OrderRepository

class OrderService(private val repository: OrderRepository) {

    fun createOrder(order: OrderRequest): Long {
        return repository.createOrder(order)
    }

    fun getOrders(userId: Int): List<OrderResponse> {
        return repository.getOrdersByUser(userId)
    }

    fun getOrders(): List<OrderResponse> {
        return repository.getOrders()
    }

    fun getOrdersByEmail(email: String): List<OrderResponse> {
        return repository.getOrdersByEmail(email)
    }

    suspend fun markOrderReceived(orderId: Long) {
        repository.markOrderReceivedAndNotify(orderId)
    }

    fun getOrderById(userId: Int, orderId: Long): OrderResponse? {
        return repository.getOrderById(userId, orderId)
    }


    fun reorder(orderId: Long, userId: Int): Long {
        return repository.reorder(orderId, userId)
    }
}