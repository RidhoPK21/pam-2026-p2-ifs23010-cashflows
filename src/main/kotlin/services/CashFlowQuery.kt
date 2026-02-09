package org.delcom.services

/**
 * Class ini digunakan untuk menampung parameter pencarian dan filter
 * sesuai dengan kebutuhan skenario pengujian.
 */
class CashFlowQuery(
    val type: String? = null,
    val source: String? = null,
    val labels: String? = null,
    val gteAmount: Double? = null,
    val lteAmount: Double? = null,
    val search: String? = null,
    val startDate: String? = null,
    val endDate: String? = null
)