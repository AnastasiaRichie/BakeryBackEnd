package org.example.service

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.WsSessionManager
import org.example.db.UserType
import org.example.models.OrderWsEvent
import org.example.models.OrderWsEventType
import org.example.models.OrderRequest
import org.example.models.OrderResponse
import org.example.repository.OrderRepository
import org.example.repository.UserRepository

class OrderService(
    private val repository: OrderRepository,
    private val userRepository: UserRepository,
) {

    suspend fun createOrder(order: OrderRequest): Long {
        val orderId = repository.createOrder(order)
        notifyAdminsAboutOrder(orderId, OrderWsEventType.ORDER_CREATED)
        return orderId
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


    suspend fun reorder(orderId: Long, userId: Int): Long {
        val newOrderId = repository.reorder(orderId, userId)
        notifyAdminsAboutOrder(newOrderId, OrderWsEventType.ORDER_CREATED)
        return newOrderId
    }

    private suspend fun notifyAdminsAboutOrder(orderId: Long, type: OrderWsEventType) {
        val order = repository.getOrderByIdForManager(orderId) ?: return
        val message = Json.encodeToString(OrderWsEvent(type = type, order = order))
        userRepository.getUserIdsByType(UserType.MANAGER).forEach { managerUserId ->
            WsSessionManager.notifyUser(managerUserId, message)
        }
    }
}