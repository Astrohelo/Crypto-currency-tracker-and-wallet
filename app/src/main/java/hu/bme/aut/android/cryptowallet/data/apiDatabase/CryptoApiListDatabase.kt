package hu.bme.aut.android.cryptowallet.data.apiDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CryptoApiItem::class], version=1)
abstract class CryptoApiListDatabase: RoomDatabase() {
    abstract fun cyptoApiItemDao(): CryptoApiDao
    companion object{
        fun getDatabase(applicationcContext: Context): CryptoApiListDatabase {
            return Room.databaseBuilder(
                applicationcContext,
                CryptoApiListDatabase::class.java,
                "crypto-apilist"
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}

