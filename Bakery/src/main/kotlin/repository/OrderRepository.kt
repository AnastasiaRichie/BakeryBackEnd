package org.example.repository

import io.ktor.server.plugins.NotFoundException
import org.example.WsSessionManager
import org.example.db.*
import org.example.models.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class OrderRepository {

    fun createOrder(order: OrderRequest): Long = transaction {
        val orderId = Orders.insertAndGetId { row ->
            row[userOwnerId] = order.userId
            row[date] = order.date
            row[address] = order.address.addressId
            row[orderState] = order.orderState
//            row[orderState] = OrderState.ORDERED
        }.value
        order.items.forEach { item ->
            OrderItems.insert { row ->
                row[orderItemId] = orderId
                row[productItemId] = item.productId
                row[quantity] = item.quantity
            }
        }
        orderId
    }

    fun getOrdersByUser(userId: Int): List<OrderResponse> = transaction {
        (Orders innerJoin CoffeeShopAddresses)
            .selectAll()
            .where { Orders.userOwnerId eq userId }
            .map { row ->
                val orderId = row[Orders.id].value
                val items = getItemsForOrder(orderId)
                val address = Address(
                    addressId = row[CoffeeShopAddresses.id].value,
                    city = row[CoffeeShopAddresses.city],
                    address = row[CoffeeShopAddresses.address]
                )
                OrderResponse(
                    orderId = orderId,
                    userId = row[Orders.userOwnerId].value,
                    date = row[Orders.date],
                    address = address,
                    orderState = row[Orders.orderState],
                    items = items
                )
            }
    }

    fun getOrdersByEmail(email: String): List<OrderResponse> = transaction {
        (Orders innerJoin Users innerJoin CoffeeShopAddresses)
            .selectAll()
            .where { (Users.email eq email) and (Orders.orderState eq OrderState.ORDERED) }
            .map { row ->
                val orderId = row[Orders.id].value
                val items = getItemsForOrder(orderId)
                val address = Address(
                    addressId = row[CoffeeShopAddresses.id].value,
                    city = row[CoffeeShopAddresses.city],
                    address = row[CoffeeShopAddresses.address]
                )
                OrderResponse(
                    orderId = orderId,
                    userId = row[Orders.userOwnerId].value,
                    date = row[Orders.date],
                    orderState = row[Orders.orderState],
                    address = address,
                    items = items
                )
            }
    }

    fun getOrderById(userId: Int, orderId: Long): OrderResponse? = transaction {
        (Orders innerJoin CoffeeShopAddresses)
            .selectAll()
            .where { (Orders.userOwnerId eq userId) and (Orders.id eq orderId) }
            .singleOrNull()?.toOrderResponse(getItemsForOrder(orderId))
    }

    fun reorder(orderId: Long, userId: Int): Long {
        return transaction {
            val oldOrder = Orders.selectAll().where { (Orders.id eq orderId) and (Orders.userOwnerId eq userId) }.singleOrNull()
                ?: throw NotFoundException("Заказ не найден")
            val newOrderId = Orders.insertAndGetId {
                it[userOwnerId] = userId
                it[address] = oldOrder[Orders.address]
                it[date] = System.currentTimeMillis()
                it[orderState] = OrderState.ORDERED
            }
            val oldItems = OrderItems.selectAll().where { OrderItems.orderItemId eq orderId }.toList()
            oldItems.forEach { item ->
                OrderItems.insert {
                    it[orderItemId] = newOrderId
                    it[productItemId] = item[OrderItems.productItemId]
                    it[quantity] = item[OrderItems.quantity]
                }
            }
            newOrderId.value
        }
    }

    private fun markOrderReceived(orderId: Long) = transaction {
        Orders.update(where = { Orders.id eq orderId }) {
            it[orderState] = OrderState.RECEIVED
        }
    }

    suspend fun markOrderReceivedAndNotify(orderId: Long) {
        markOrderReceived(orderId)
        val userId = getOrderOwnerId(orderId)
        userId?.let { WsSessionManager.notifyUser(it, """{"type":"ORDER_RECEIVED","orderId":$orderId}""") }
    }

    private fun getOrderOwnerId(orderId: Long): Int? = transaction {
        Orders
            .selectAll()
            .where { Orders.id eq orderId }
            .singleOrNull()
            ?.get(Orders.userOwnerId)
            ?.value
    }

    private fun getItemsForOrder(orderId: Long): List<OrderResponseItem> {
        return (OrderItems innerJoin Products)
            .selectAll()
            .where { OrderItems.orderItemId eq orderId }
            .map { row -> OrderResponseItem(id = row[OrderItems.id].value, quantity = row[OrderItems.quantity], product = row.toProductModel()) }
    }

    private fun ResultRow.toOrderResponse(items: List<OrderResponseItem>) = OrderResponse(
        orderId = this[Orders.id].value,
        userId = this[Orders.userOwnerId].value,
        date = this[Orders.date],
        address = Address(
            addressId = this[CoffeeShopAddresses.id].value,
            city = this[CoffeeShopAddresses.city],
            address = this[CoffeeShopAddresses.address]
        ),
        orderState = this[Orders.orderState],
        items = items,
    )
}