package org.bakery_tm.repositories

import org.bakery_tm.database.Users
import org.bakery_tm.models.ExtendedUserModel
import org.bakery_tm.models.UserModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {

    fun init() {
        transaction {
            SchemaUtils.create(Users)
        }
    }

    /**
     * Add new user (sign-up)
     * @param user user data
     */
    fun createUser(user: ExtendedUserModel, hashedPassword: String): Long = transaction {
        Users.insert {
            it[login] = user.login
            it[userName] = user.userName
            it[userSurname] = user.userSurname
            it[password] = hashedPassword
            it[dateOfBirth] = user.dateOfBirth
        }[Users.id]
    }

    /**
     * Is login exist
     * @param login user login
     */
    fun userExists(login: String): Boolean = transaction {
        Users.select { Users.login eq login }.any()
    }

    /**
     * Get user (sign-in)
     * @param user entered user data
     */
    fun getUserByLoginAndPassword(user: UserModel): ExtendedUserModel? = transaction {
        Users.select { (Users.login eq user.login) and (Users.password eq user.password) }
            .map { user ->
                ExtendedUserModel(
                    userId = user[Users.id],
                    login = user[Users.login],
                    userName = user[Users.userName],
                    userSurname = user[Users.userSurname],
                    password = user[Users.password],
                    dateOfBirth = user[Users.dateOfBirth]
                )
            }.singleOrNull()
    }

    /**
     * Get user
     * @param userId user id
     */
    fun getUserById(userId: Long): ExtendedUserModel? = transaction {
        Users.select { Users.id eq userId }
            .map { user ->
                ExtendedUserModel(
                    userId = user[Users.id],
                    login = user[Users.login],
                    userName = user[Users.userName],
                    userSurname = user[Users.userSurname],
                    password = user[Users.password],
                    dateOfBirth = user[Users.dateOfBirth]
                )
            }.singleOrNull()
    }

    /**
     * Update user's name
     * @param name user name
     */
    fun updateUserName(name: String) = transaction {
        Users.update {
            it[Users.userName] = name
        }
    }

    /**
     * Update user's surname
     * @param userSurname user surname
     */
    fun updateUserSurname(userSurname: String?) = transaction {
        Users.update {
            it[Users.userSurname] = userSurname
        }
    }

    /**
     * Update user's login
     * @param login user login
     */
    fun updateUserLogin(login: String) = transaction {
        Users.update {
            it[Users.login] = login
        }
    }
}