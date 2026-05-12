package org.bakery_tm.database

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = long("user_id").autoIncrement()
    val login = varchar("login", 50).uniqueIndex()
    val userName = varchar("user_name", 50)
    val userSurname = varchar("user_surname", 50).nullable()
    val password = varchar("password", 50)
    val dateOfBirth = varchar("date_of_birth", 100)
    override val primaryKey = PrimaryKey(id)
}