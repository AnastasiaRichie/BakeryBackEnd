package org.example.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.db.OrderState
import org.example.db.ProductType
import org.example.db.Products
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class RegisterRequest(val name: String, val lastName: String, val email: String, val password: String)
@Serializable
data class LoginRequest(val email: String, val password: String)
@Serializable
data class TokenResponse(val token: String, val userId: Int)
@Serializable
data class UserResponse(
    val token: String,
    val userId: Int,
    val email: String,
    val name: String,
    val lastName: String
)
@Serializable
data class UpdateUserRequest(
    val name: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val password: String? = null
)
@Serializable
data class UpdateUserResponse(
    val id: Int,
    val name: String,
    val lastName: String,
    val email: String
)
@Serializable
data class ErrorResponse(val message: String)

@Serializable
data class ProductModel(
    val productId: Long,
    val name: String,
    val weight: String,
    val description: String,
    val fullDescription: String,
    val allergens: List<String>,
    val price: String,
    val productImageName: String,
    val productType: ProductType,
)

fun ResultRow.toProductModel() = ProductModel(
    productId = this[Products.id].value,
    name = this[Products.name],
    weight = "${this[Products.weightGrams]} гр.",
    description = this[Products.description],
    fullDescription = this[Products.fullDescription],
    allergens = Json.decodeFromString<List<String>>(this[Products.allergens]),
    price = "%.2f".format(this[Products.priceCents] / 100.0),
    productImageName = this[Products.imageName],
    productType = this[Products.productType]
)

@Serializable
data class OrderRequest(
    val userId: Int,
    val address: Address,
    val date: Long,
    val orderState: OrderState,
    val items: List<OrderRequestItem>
)

@Serializable
data class OrderRequestItem(
    val quantity: Int,
    val productId: Long
)

@Serializable
data class OrderResponse(
    val orderId: Long,
    val userId: Int,
    val address: Address,
    val orderState: OrderState,
    val date: Long,
    val items: List<OrderResponseItem>
)

@Serializable
data class OrderResponseItem(
    val id: Long,
    val quantity: Int,
    val product: ProductModel
)

@Serializable
data class Address(
    val addressId: Int,
    val city: String,
    val address: String
)

@Serializable
data class OrderIdModel(val orderId: Long)
