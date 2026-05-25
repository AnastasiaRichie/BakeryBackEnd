package org.example.service

import org.example.db.UserType
import org.example.db.Users
import org.example.exceptions.EmailAlreadyExistsException
import org.example.exceptions.InvalidPasswordException
import org.example.exceptions.UserNotFoundException
import org.example.models.TokenResponse
import org.example.models.UpdateUserRequest
import org.example.models.UpdateUserResponse
import org.example.models.UserResponse
import org.example.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt

class UserService(private val repository: UserRepository, private val jwtService: JwtService) {

    fun register(firstName: String, lastName: String, email: String, password: String): TokenResponse {
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

        val exists = repository.findByEmail(email) != null
        if (exists) throw EmailAlreadyExistsException()

        val userId = repository.insertUser(firstName, lastName, email, hashedPassword, getUserType(email))
        return TokenResponse(jwtService.generateToken(userId.value), userId.value, getUserType(email))
    }

    fun login(email: String, password: String): UserResponse {
        val user = repository.findByEmail(email) ?: throw UserNotFoundException()
        val passwordMatches = BCrypt.checkpw(password, user[Users.password])
        if (!passwordMatches) throw InvalidPasswordException()
        return UserResponse(
            token = jwtService.generateToken(user[Users.id].value),
            userId = user[Users.id].value,
            userType = getUserType(email),
            email = user[Users.email],
            name = user[Users.firstName],
            lastName = user[Users.lastName]
        )
    }

    fun getUserByEmail(email: String): UserResponse {
        val user = repository.findByEmail(email) ?: throw UserNotFoundException()
        return UserResponse(
            token = jwtService.generateToken(user[Users.id].value),
            userId = user[Users.id].value,
            userType = getUserType(email),
            email = user[Users.email],
            name = user[Users.firstName],
            lastName = user[Users.lastName]
        )
    }

    fun updateUserPassword(email: String, password: String) {
        repository.findByEmail(email) ?: throw UserNotFoundException()
        repository.updateUserPassword(email, BCrypt.hashpw(password, BCrypt.gensalt()))
    }

    fun updateUser(user: UpdateUserRequest, userId: Int): UpdateUserResponse {

        if (user.email != null && repository.findByEmail(user.email) != null) {
            throw EmailAlreadyExistsException()
        }

        val passwordHash = user.password?.let {
            BCrypt.hashpw(it, BCrypt.gensalt())
        }

        val updatedUser = repository.updateUser(
            userId = userId,
            firstName = user.name,
            lastName = user.lastName,
            email = user.email,
            passwordHash = passwordHash
        )

        return UpdateUserResponse(
            id = userId,
            userType = getUserType(user.email.orEmpty()),
            email = updatedUser[Users.email],
            name = updatedUser[Users.firstName],
            lastName = updatedUser[Users.lastName],
        )
    }
}

private fun getUserType(email: String): UserType {
    return when {
        email.contains("adm@bkr.team") -> UserType.ADMIN
        email.contains("mngr@bkr.team") -> UserType.MANAGER
        else -> UserType.USER
    }
}