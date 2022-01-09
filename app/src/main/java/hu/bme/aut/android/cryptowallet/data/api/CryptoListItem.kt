package hu.bme.aut.android.cryptowallet.data.api

data class CryptoListItem(
    val id: String,
    val name: String,
    val symbol: String,
    val quote: Quote
)
