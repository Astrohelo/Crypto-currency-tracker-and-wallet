package hu.bme.aut.android.cryptowallet.data.wallet

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CryptoItem::class], version=1)
abstract class CryptoListDatabase: RoomDatabase() {
    abstract fun cryptoItemDao(): CryptoItemDao
    companion object{
        fun getDatabase(applicationcContext: Context): CryptoListDatabase {
            return Room.databaseBuilder(
                applicationcContext,
                CryptoListDatabase::class.java,
                "crypto-wallet2nd"
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}

