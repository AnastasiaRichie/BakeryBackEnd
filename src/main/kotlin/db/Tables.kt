package org.example.db

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Users : IntIdTable() {
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 60)
    val userType = enumerationByName(name = "user_type", length = 50, klass = UserType::class)
}

object CoffeeShopAddresses : IntIdTable() {
    val city = varchar("city", 100)
    val address = varchar("address", 255)

    init {
        uniqueIndex(city, address)
    }
}

object Products : LongIdTable() {
    val name = varchar("name", 150)
    val weightGrams = integer("weight_grams")
    val description = varchar("description", 255)
    val fullDescription = text("full_description")
    val priceCents = integer("price_cents")
    val imageName = varchar("image_name", 255)
    val productType = enumerationByName(name = "product_type", length = 50, klass = ProductType::class)
    val allergens = text("allergens_json")
    val isActive = bool("is_active").default(true)
}

@Serializable
enum class ProductType {
    FLOUR,
    DRINK
}

@Serializable
enum class UserType {
    USER,
    MANAGER,
    ADMIN
}

object Orders : LongIdTable("orders") {

    val userOwnerId = reference("user_owner_id", Users)
    val date = long("date")

    val address = reference("coffee_shop_id", CoffeeShopAddresses)
    val orderState = enumerationByName(name = "order_state", length = 20, klass = OrderState::class)
}

object OrderItems : LongIdTable("order_items") {

    val orderItemId = reference("order_id", Orders, onDelete = ReferenceOption.CASCADE)
    val productItemId = reference("product_id", Products, onDelete = ReferenceOption.CASCADE)
    val quantity = integer("quantity")
}

@Serializable
enum class OrderState {
    ORDERED,
    RECEIVED,
}