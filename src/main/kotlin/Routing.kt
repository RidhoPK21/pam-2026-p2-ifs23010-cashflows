package org.delcom

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.controllers.CashFlowController
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val cashFlowController by inject<CashFlowController>()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        route("/cash-flows") {
            // 1. Rute Statis & Setup (Dahulukan ini)
            post("/setup") {
                cashFlowController.setupData(call)
            }

            // 2. Rute Metadata (Harus di atas rute {id})
            get("/types") {
                cashFlowController.getTypes(call)
            }
            get("/sources") {
                cashFlowController.getSources(call)
            }
            get("/labels") {
                cashFlowController.getLabels(call)
            }

            // 3. Rute Koleksi (Get All & Create)
            get {
                cashFlowController.getAll(call)
            }
            post {
                cashFlowController.create(call)
            }

            // 4. Rute Dinamis berdasarkan ID (Letakkan paling bawah)
            get("/{id}") {
                cashFlowController.getById(call)
            }
            put("/{id}") {
                cashFlowController.update(call)
            }
            delete("/{id}") {
                cashFlowController.delete(call)
            }
        }
    }
}