package hu.bme.aut.android.cryptowallet.data.api

data class Status (
    val timestamp: String,

    val error_code: Long,

    val error_message: Any? = null,

    val elapsed: Long,

    val credit_count: Long,

    val notice: Any? = null,

    val total_count: Long
)