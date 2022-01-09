package hu.bme.aut.android.cryptowallet.data.apiDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cryptoApi2nd")
data class CryptoApiItem (
    @ColumnInfo(name = "id")@PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "symbol") val symbol: String,
    @ColumnInfo(name = "currentPrice") var currentPrice: Float,
    @ColumnInfo(name = "percent_change_1h") var percent_change_1h: Float,
    @ColumnInfo(name = "percent_change_24h") var percent_change_24h: Float,
    @ColumnInfo(name = "percent_change_7d") var percent_change_7d: Float

)