package org.bakery_tm.models

data class ExtendedUserModel(
    val userId: Long,
    val login: String,
    val password: String,
    val userName: String,
    val userSurname: String? = null,
    val dateOfBirth: String,
)