package hu.bme.aut.android.cryptowallet.data.apiDatabase

import androidx.room.*

@Dao
interface CryptoApiDao {
    @Query("SELECT * FROM cryptoApi2nd")
    fun getAll(): List<CryptoApiItem>

    @Insert
    fun insert(cyptoApiItems: CryptoApiItem): Long

    @Update
    fun update(cyptoApiItems: CryptoApiItem)

    @Delete
    fun deleteItem(cyptoApiItems: CryptoApiItem)

    @Query("DELETE FROM cryptoApi2nd")
    fun deleteAll()

}