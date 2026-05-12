package org.bakery_tm.di

import io.ktor.server.application.*
import org.bakery_tm.repositories.ProductsRepository
import org.bakery_tm.repositories.UserRepository
import org.bakery_tm.services.ProductsService
import org.bakery_tm.services.UserService
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val appModule = module {
    single { UserRepository() }
    single { ProductsRepository() }
    single { UserService(get()) }
    single { ProductsService(get()) }
}

fun Application.configureKoin() {
    install(Koin) {
        modules(appModule)
    }
}