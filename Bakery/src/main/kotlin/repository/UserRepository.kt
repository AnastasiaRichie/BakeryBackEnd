package org.example.repository

import org.example.db.Users
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserRepository {

    fun findByEmail(email: String) = transaction {
        Users.selectAll().where { Users.email eq email }.singleOrNull()
    }

    fun insertUser(firstName: String, lastName: String, email: String, hashedPassword: String) = transaction {
        Users.insertAndGetId {
            it[Users.firstName] = firstName
            it[Users.lastName] = lastName
            it[Users.email] = email
            it[Users.password] = hashedPassword
        }
    }

    fun updateUser(userId: Int, firstName: String?, lastName: String?, email: String?, passwordHash: String?) = transaction {
        Users.update({ Users.id eq userId }) { user ->
            firstName?.let { user[Users.firstName] = it }
            lastName?.let { user[Users.lastName] = it }
            email?.let { user[Users.email] = it }
            passwordHash?.let { user[Users.password] = it }
        }
        Users.select { Users.id eq userId }.single()
    }
}