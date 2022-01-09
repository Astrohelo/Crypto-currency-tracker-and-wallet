package hu.bme.aut.android.cryptowallet.data.wallet

import androidx.room.*

@Dao
interface CryptoItemDao {
    @Query ("SELECT * FROM mycryptoitem")
    fun getAll(): List<CryptoItem>

    @Insert
    fun insert(cryptoItems: CryptoItem): Long

    @Update
    fun update(cryptoItems: CryptoItem)

    @Delete
    fun deleteItem(cryptoItems: CryptoItem)

    @Query("UPDATE mycryptoitem SET amount=:amount WHERE id= :id")
    fun updateAmount(amount: Float?, id: Long?)

    @Query("UPDATE mycryptoitem SET currentPrice=:currentPrice WHERE id= :id")
    fun updatePrice(currentPrice: Float?, id: Long?)
}
