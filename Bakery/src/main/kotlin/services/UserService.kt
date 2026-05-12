package org.bakery_tm.services

import org.bakery_tm.models.ExtendedUserModel
import org.bakery_tm.models.UserModel
import org.bakery_tm.repositories.UserRepository

class UserService(private val userRepository: UserRepository) {

    fun getUser(user: UserModel) = userRepository.getUserByLoginAndPassword(user)

    fun getUserById(userId: Long) = userRepository.getUserById(userId)

    fun createUser(user: ExtendedUserModel, hashedPassword: String) {
        userRepository.createUser(user, hashedPassword)
    }

    fun userExists(login: String): Boolean = userRepository.userExists(login)
}