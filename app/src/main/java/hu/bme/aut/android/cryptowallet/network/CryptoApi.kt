package hu.bme.aut.android.cryptowallet.network

import hu.bme.aut.android.cryptowallet.data.api.Status_Data
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoApi {


    @GET("/v1/cryptocurrency/listings/latest")
    fun getCrypto(@Query("CMC_PRO_API_KEY") key: String) : Call<Status_Data>
}

