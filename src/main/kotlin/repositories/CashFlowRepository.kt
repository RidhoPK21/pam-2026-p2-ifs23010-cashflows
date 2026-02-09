package org.delcom.repositories

import org.delcom.entities.CashFlow

class CashFlowRepository {
    // Penyimpanan data di memori sesuai instruksi praktikum [cite: 105, 156]
    private val cashFlows = mutableListOf<CashFlow>()

    /**
     * Mengambil semua daftar catatan keuangan[cite: 154, 219].
     */
    fun getAll(): List<CashFlow> = cashFlows

    /**
     * Menambahkan catatan keuangan baru[cite: 171, 226].
     */
    fun add(cashFlow: CashFlow) {
        cashFlows.add(cashFlow)
    }

    /**
     * Mencari data berdasarkan ID untuk validasi[cite: 268, 275].
     */
    fun findById(id: String): CashFlow? = cashFlows.find { it.id == id }

    /**
     * Memperbarui data yang sudah ada[cite: 275].
     * Mengembalikan 'true' jika berhasil, 'false' jika ID tidak ditemukan.
     */
    fun update(id: String, updatedCashFlow: CashFlow): Boolean {
        val index = cashFlows.indexOfFirst { it.id == id }
        return if (index != -1) {
            cashFlows[index] = updatedCashFlow
            true
        } else {
            false
        }
    }

    /**
     * Menghapus data berdasarkan ID[cite: 158, 293].
     * Mengembalikan 'true' jika ada data yang dihapus.
     */
    fun removeById(id: String): Boolean {
        return cashFlows.removeIf { it.id == id }
    }
}