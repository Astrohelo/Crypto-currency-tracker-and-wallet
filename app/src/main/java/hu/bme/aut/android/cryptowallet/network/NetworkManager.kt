package hu.bme.aut.android.cryptowallet.network

import hu.bme.aut.android.cryptowallet.data.api.Status_Data
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
    private val retrofit: Retrofit
    private val cryptoApi: CryptoApi

    private const val SERVICE_URL = "https://pro-api.coinmarketcap.com"
    private const val APP_ID = "45442aaa-b9bc-41e1-99d2-ba97a37cf8be"

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(SERVICE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        cryptoApi = retrofit.create(CryptoApi::class.java)

    }

    fun getCrypto(): Call<Status_Data>{
        return cryptoApi.getCrypto(APP_ID)
    }
}