package org.example.exceptions

sealed class AuthException(message: String) : RuntimeException(message)

class UserNotFoundException : AuthException("Пользователя с таким email не существует")

class InvalidPasswordException : AuthException("Проверьте корректность введенных данных")

class EmailAlreadyExistsException : AuthException("Пользователь с таким email уже существует")