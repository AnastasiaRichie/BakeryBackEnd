package org.example.di

import org.example.models.JwtConfig
import org.example.repository.OrderRepository
import org.example.repository.ProductRepository
import org.example.repository.UserRepository
import org.example.service.JwtService
import org.example.service.OrderService
import org.example.service.ProductService
import org.example.service.UserService
import org.koin.dsl.module

fun appModule(jwtConfig: JwtConfig) = module {
    single { jwtConfig }
    single { JwtService(get()) }
    single { UserRepository() }
    single { OrderRepository() }
    single { ProductRepository() }
    single { UserService(get(), get()) }
    single { OrderService(get()) }
    single { ProductService(get()) }
}