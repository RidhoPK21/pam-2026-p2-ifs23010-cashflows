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
    // Muat variabel environment dari file .env agar variabel ${APP_PORT} di application.yaml terbaca
    try {
        val dotenv = dotenv()
        dotenv.entries().forEach { entry ->
            System.setProperty(entry.key, entry.value)
        }
    } catch (e: Exception) {
        // Tetap lanjutkan jika .env tidak ada (mungkin menggunakan environment system langsung)
    }

    io.ktor.server.netty.EngineMain.main(args)
}

// 1. Definisikan modul Koin untuk Dependency Injection
val appModule = module {
    single { CashFlowRepository() }
    single { CashFlowService(get()) }
    single { CashFlowController(get()) }
}

fun Application.module() {
    // 2. Install Plugin Koin agar Service dan Controller bisa di-inject
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    // 3. Panggil konfigurasi standar
    configureSerialization() // Untuk memproses JSON (Content Negotiation)
    configureHTTP()          // Untuk pengaturan CORS dan Header

    // Tambahkan StatusPages jika Anda ingin menangani error global (Opsional)
    // configureStatusPages()

    configureRouting()       // Menghubungkan endpoint ke Controller
}