package org.example.service

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
        if (exists) throw IllegalArgumentException("Пользователь с такой почтой уже существует")

        val userId = repository.insertUser(firstName, lastName, email, hashedPassword)
        return TokenResponse(jwtService.generateToken(userId.value), userId.value)
    }

    fun login(email: String, password: String): UserResponse {
        val user = repository.findByEmail(email) ?: throw UserNotFoundException()
        val passwordMatches = BCrypt.checkpw(password, user[Users.password])
        if (!passwordMatches) throw InvalidPasswordException()
        return UserResponse(
            token = jwtService.generateToken(user[Users.id].value),
            userId = user[Users.id].value,
            email = user[Users.email],
            name = user[Users.firstName],
            lastName = user[Users.lastName]
        )
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
            email = updatedUser[Users.email],
            name = updatedUser[Users.firstName],
            lastName = updatedUser[Users.lastName],
        )
    }
}