package org.bakery_tm.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bakery_tm.auth.generateJwtToken
import org.bakery_tm.models.AuthResponse
import org.bakery_tm.models.ExtendedUserModel
import org.bakery_tm.models.UserModel
import org.bakery_tm.services.UserService
import org.bakery_tm.utils.hashPassword
import org.bakery_tm.utils.verifyHash
import org.koin.ktor.ext.inject

fun Route.userRoutes() {

    val userService by inject<UserService>()

    post("/register") {
        val userModel = call.receive<ExtendedUserModel>()

        if (userService.userExists(userModel.login)) {
            throw Exception("User already exists")
        }

        val hashedPassword = hashPassword(userModel.password)
        val userId = userService.createUser(
            userModel, hashedPassword
        )

        call.respond(HttpStatusCode.Created, mapOf("id" to userId))
    }

    get("/user") {
        val user = userService.getUser(
            UserModel(
                "", ""
            )
        )
        call.respond(user ?: throw IllegalArgumentException("No needed user"))
    }

    post("/user") {
        val users = userService.getUser(
            UserModel(
                "", ""
            )
        )
        call.respond(users ?: throw IllegalArgumentException("Password is required"))
    }

    post("/auth/login") {
        val loginRequest = call.receive<UserModel>()
        val user = userService.getUser(loginRequest)
            ?: throw NotFoundException("User not found")

        if (!verifyHash(loginRequest.password, user.password)) {
            throw NotFoundException("UnauthorizedException Invalid credentials")
        }

        val token = generateJwtToken(user.userId)
        call.respond(AuthResponse(token))
    }

    authenticate("auth-jwt") {
        get("/user/me") {
            val principal = call.principal<JWTPrincipal>()
                ?: throw Exception("ForbiddenException Invalid token")

            val userId = principal.payload.getClaim("id").asLong()
            val user = userService.getUserById(userId) ?: throw NotFoundException("User not found")

            call.respond(user)
        }
    }

}