package hu.bme.aut.android.cryptowallet.data.wallet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mycryptoitem")
data class CryptoItem (
    @ColumnInfo(name = "id")@PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "symbol") val symbol: String,
    @ColumnInfo(name = "boughtPrice") var bonus: Float,
    @ColumnInfo(name = "currentPrice") var currentPrice: Float,
    @ColumnInfo(name = "amount") var amount: Float
)