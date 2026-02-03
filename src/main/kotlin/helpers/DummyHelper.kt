package org.delcom.helpers

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class CashFlowsContainer(
    val cashFlows: List<CashFlow>
)

fun loadInitialData(): List<CashFlow> {
    val jsonFormat = Json { ignoreUnknownKeys = true }
    return try {
        val jsonFile = File("data-awal.json")
        val jsonText = if (!jsonFile.exists()) {
            val resource = object {}.javaClass.classLoader.getResource("data-awal.json")
                ?: throw IllegalStateException("File tidak ditemukan!")
            resource.readText()
        } else {
            jsonFile.readText()
        }
        jsonFormat.decodeFromString<CashFlowsContainer>(jsonText).cashFlows
    } catch (e: Exception) {
        println("Error loading JSON: ${e.message}")
        emptyList()
    }
}