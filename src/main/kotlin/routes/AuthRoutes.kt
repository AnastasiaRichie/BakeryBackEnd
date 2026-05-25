package org.example.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import org.example.exceptions.EmailAlreadyExistsException
import org.example.exceptions.InvalidPasswordException
import org.example.exceptions.UserNotFoundException
import org.example.models.*
import org.example.service.UserService

fun Route.authRoutes(userService: UserService) {

    post("/register") {
        try {
            val request = call.receive<RegisterRequest>()
            val tokenResponse = userService.register(request.name, request.lastName, request.email, request.password)
            call.respond(tokenResponse)
        } catch (e: EmailAlreadyExistsException) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message.orEmpty()))
        }
    }

    post("/login") {
        try {
            val request = call.receive<LoginRequest>()
            val tokenResponse = userService.login(request.email, request.password)
            call.respond(tokenResponse)
        } catch (e: UserNotFoundException) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message.orEmpty()))
        } catch (e: InvalidPasswordException) {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse(e.message.orEmpty()))
        }
    }

    post("/get-user-by-email") {
        try {
            val request = call.receive<EmailRequest>()
            val user = userService.getUserByEmail(request.email)
            call.respond(user)
        } catch (e: UserNotFoundException) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message.orEmpty()))
        }
    }

    patch("/update-user-pass") {
        try {
            val request = call.receive<UpdateUserPassRequest>()
            userService.updateUserPassword(request.email, request.password)
            call.respond(HttpStatusCode.OK)
        } catch (e: UserNotFoundException) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message.orEmpty()))
        }
    }


    authenticate("auth-jwt") {
        patch("/update-user") {
            try {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                val request = call.receive<UpdateUserRequest>()
                val userResponse = userService.updateUser(request, userId)
                call.respond(userResponse)
            } catch (e: UserNotFoundException) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message.orEmpty()))
            } catch (e: EmailAlreadyExistsException) {
                call.respond(ErrorResponse(e.message.orEmpty()))
            }
        }
    }
}