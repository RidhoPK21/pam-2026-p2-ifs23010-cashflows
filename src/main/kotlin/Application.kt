package org.delcom

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import org.delcom.controllers.CashFlowController
import org.delcom.repositories.CashFlowRepository
import org.delcom.services.CashFlowService
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    // Muat variabel environment
    try {
        val dotenv = dotenv()
        dotenv.entries().forEach { entry ->
            System.setProperty(entry.key, entry.value)
        }
    } catch (e: Exception) {
        // Lanjut jika .env tidak ditemukan
    }

    io.ktor.server.netty.EngineMain.main(args)
}

// 1. Definisikan modul Koin
val appModule = module {
    single { CashFlowRepository() }
    single { CashFlowService(get()) }
    single { CashFlowController(get()) }
}

fun Application.module() {
    // 2. Install Plugin Koin
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    // 3. Konfigurasi Aplikasi
    configureSerialization()
    configureHTTP()

    // PENTING: Aktifkan StatusPages agar error 500 merespon dengan JSON
    configureStatusPages()

    configureRouting()
}