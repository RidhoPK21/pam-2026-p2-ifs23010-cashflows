package org.delcom.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.entities.CashFlow
import org.delcom.helpers.loadInitialData
import org.delcom.services.CashFlowQuery
import org.delcom.services.CashFlowService
import java.time.OffsetDateTime
import java.util.*

class CashFlowController(private val cashFlowService: CashFlowService) {

    suspend fun setupData(call: ApplicationCall) {
        val all = cashFlowService.getAllCashFlows(CashFlowQuery())
        all.forEach { cashFlowService.remove(it.id) }
        loadInitialData().forEach {
            cashFlowService.createRawCashFlow(it.id, it.type, it.source, it.label, it.amount, it.createdAt, it.updatedAt, it.description)
        }
        call.respond(HttpStatusCode.OK, DataResponse<Any?>(
            status = "success",
            message = "Berhasil memuat data awal",
            data = null
        ))
    }

    suspend fun getAll(call: ApplicationCall) {
        val p = call.request.queryParameters
        val query = CashFlowQuery(
            type = p["type"], source = p["source"], labels = p["labels"],
            gteAmount = p["gteAmount"]?.toDoubleOrNull(), lteAmount = p["lteAmount"]?.toDoubleOrNull(),
            search = p["search"], startDate = p["startDate"], endDate = p["endDate"]
        )
        val list = cashFlowService.getAllCashFlows(query)
        call.respond(HttpStatusCode.OK, DataResponse(
            status = "success",
            message = "Berhasil mengambil daftar catatan keuangan",
            data = mapOf("cashFlows" to list, "total" to list.size) // Properti 'total' wajib
        ))
    }

    suspend fun create(call: ApplicationCall) {
        val req = call.receiveNullable<Map<String, Any?>>() ?: emptyMap()
        val errors = mutableMapOf<String, String>()

        // Sesuaikan pesan error jika field kosong
        val fields = listOf("type", "source", "label", "amount", "description")
        fields.forEach { if (req[it]?.toString().isNullOrBlank()) errors[it] = "Is required" }

        val amount = req["amount"]?.toString()?.toDoubleOrNull() ?: 0.0
        if (amount <= 0 && !errors.containsKey("amount")) errors["amount"] = "Must be > 0"

        if (errors.isNotEmpty()) {
            return call.respond(HttpStatusCode.BadRequest, DataResponse(
                status = "fail",
                message = "Data yang dikirimkan tidak valid!",
                data = errors
            ))
        }

        val id = UUID.randomUUID().toString()
        val now = OffsetDateTime.now().toString()
        val cf = CashFlow(id, req["type"].toString(), req["source"].toString(), req["label"].toString(), amount, req["description"].toString(), now, now)
        cashFlowService.create(cf)

        call.respond(HttpStatusCode.OK, DataResponse(
            status = "success",
            message = "Berhasil menambahkan data catatan keuangan",
            data = mapOf("cashFlowId" to id)
        ))
    }

    suspend fun getById(call: ApplicationCall) {
        val id = call.parameters["id"] ?: ""
        val cf = cashFlowService.findById(id) ?: return call.respond(HttpStatusCode.NotFound,
            DataResponse<Any?>("fail", "Data catatan keuangan tidak tersedia!", null))

        call.respond(HttpStatusCode.OK, DataResponse(
            status = "success",
            message = "Berhasil mengambil data catatan keuangan",
            data = mapOf("cashFlow" to cf)
        ))
    }

    suspend fun update(call: ApplicationCall) {
        val id = call.parameters["id"] ?: ""
        val existing = cashFlowService.findById(id) ?: return call.respond(HttpStatusCode.NotFound,
            DataResponse<Any?>("fail", "Data catatan keuangan tidak tersedia!", null))

        val req = call.receiveNullable<Map<String, Any?>>() ?: emptyMap()
        val errors = mutableMapOf<String, String>()
        val fields = listOf("type", "source", "label", "amount", "description")
        fields.forEach { if (req[it]?.toString().isNullOrBlank()) errors[it] = "Required" }

        if (errors.isNotEmpty()) {
            return call.respond(HttpStatusCode.BadRequest, DataResponse(
                status = "fail",
                message = "Data yang dikirimkan tidak valid!",
                data = errors
            ))
        }

        val updated = existing.copy(
            type = req["type"].toString(), source = req["source"].toString(),
            label = req["label"].toString(), amount = req["amount"].toString().toDouble(),
            description = req["description"].toString(), updatedAt = OffsetDateTime.now().toString()
        )
        cashFlowService.update(id, updated)
        call.respond(HttpStatusCode.OK, DataResponse<Any?>("success", "Berhasil mengubah data catatan keuangan", null))
    }

    suspend fun delete(call: ApplicationCall) {
        val id = call.parameters["id"] ?: ""
        if (!cashFlowService.remove(id)) return call.respond(HttpStatusCode.NotFound,
            DataResponse<Any?>("fail", "Data catatan keuangan tidak tersedia!", null))

        call.respond(HttpStatusCode.OK, DataResponse<Any?>("success", "Berhasil menghapus data catatan keuangan", null))
    }

    suspend fun getTypes(call: ApplicationCall) = call.respond(HttpStatusCode.OK,
        DataResponse("success", "Berhasil mengambil daftar tipe catatan keuangan", mapOf("types" to cashFlowService.getDistinctTypes())))

    suspend fun getSources(call: ApplicationCall) = call.respond(HttpStatusCode.OK,
        DataResponse("success", "Berhasil mengambil daftar source catatan keuangan", mapOf("sources" to cashFlowService.getDistinctSources())))

    suspend fun getLabels(call: ApplicationCall) = call.respond(HttpStatusCode.OK,
        DataResponse("success", "Berhasil mengambil daftar label catatan keuangan", mapOf("labels" to cashFlowService.getDistinctLabels())))
}