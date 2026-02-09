package org.delcom.services

import org.delcom.entities.CashFlow
import org.delcom.repositories.CashFlowRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CashFlowService(private val repository: CashFlowRepository) {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    /**
     * Mengambil daftar catatan keuangan dengan menerapkan filter yang sangat ketat.
     * Logika ini memastikan jumlah data pas dengan ekspektasi filter.test.js.
     */
    fun getAllCashFlows(query: CashFlowQuery): List<CashFlow> {
        return repository.getAll().filter { cf ->
            // Filter Tipe (Pemasukan/Pengeluaran) - Case Insensitive
            val matchType = query.type?.let { cf.type.equals(it, true) } ?: true

            // Filter Sumber
            val matchSource = query.source?.let { cf.source.equals(it, true) } ?: true

            // Filter Label - Harus mengandung SEMUA label yang diminta di query
            val matchLabels = query.labels?.split(",")?.filter { it.isNotBlank() }?.all { searchLabel ->
                cf.label.split(",").map { it.trim() }.contains(searchLabel.trim())
            } ?: true

            // Filter nominal uang
            val matchGte = query.gteAmount?.let { cf.amount >= it } ?: true
            val matchLte = query.lteAmount?.let { cf.amount <= it } ?: true

            // Filter pencarian deskripsi
            val matchSearch = query.search?.let { cf.description.contains(it, true) } ?: true

            // Filter Tanggal (Penting: dd-MM-yyyy vs ISO format)
            val cfDate = try {
                LocalDate.parse(cf.createdAt.substring(0, 10))
            } catch (e: Exception) {
                null
            }

            val matchStart = query.startDate?.let {
                val start = LocalDate.parse(it, dateFormatter)
                cfDate?.let { date -> !date.isBefore(start) } ?: false
            } ?: true

            val matchEnd = query.endDate?.let {
                val end = LocalDate.parse(it, dateFormatter)
                cfDate?.let { date -> !date.isAfter(end) } ?: false
            } ?: true

            matchType && matchSource && matchLabels && matchGte && matchLte && matchSearch && matchStart && matchEnd
        }
    }

    // Metadata untuk filter.test.js
    fun getDistinctTypes() = repository.getAll().map { it.type }.distinct()
    fun getDistinctSources() = repository.getAll().map { it.source }.distinct()
    fun getDistinctLabels(): List<String> {
        return repository.getAll()
            .flatMap { it.label.split(",") }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
    }

    // Fungsi CRUD dasar yang dipanggil oleh Controller
    fun findById(id: String) = repository.findById(id)
    fun create(cf: CashFlow) = repository.add(cf)
    fun update(id: String, cf: CashFlow) = repository.update(id, cf)

    // Pastikan nama fungsi ini 'remove' agar match dengan Controller Sasya
    fun remove(id: String): Boolean = repository.removeById(id)

    // Alias untuk setupData
    fun removeCashFlow(id: String) { repository.removeById(id) }

    fun createRawCashFlow(
        id: String, type: String, source: String, label: String,
        amount: Double, createdAt: String, updatedAt: String, description: String
    ) {
        val newCashFlow = CashFlow(
            id = id, type = type, source = source, label = label,
            amount = amount, description = description,
            createdAt = createdAt, updatedAt = updatedAt
        )
        repository.add(newCashFlow)
    }
}