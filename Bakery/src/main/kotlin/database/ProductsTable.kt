package org.bakery_tm.database

import org.jetbrains.exposed.sql.Table

object Products : Table("products") {
    val id = long("product_id").autoIncrement()
    val productName = varchar("product_name", 50)
    val description = varchar("description", 100)
    val fullDescription = varchar("full_description", 100)
    val hasAllergens = bool("has_allergens")
    val allergens = varchar("allergens", 100)
    val icon = varchar("icon", 128)
    override val primaryKey = PrimaryKey(id)
}