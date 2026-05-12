package org.bakery_tm.models

data class ExtendedProductModel(
    val productId: Long? = null,
    val productName: String,
    val description: String,
    val fullDescription: String,
    val icon: String,
    val hasAllergens: Boolean,
    val allergens: String,
)