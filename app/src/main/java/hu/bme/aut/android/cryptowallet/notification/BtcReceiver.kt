package hu.bme.aut.android.cryptowallet.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import hu.bme.aut.android.cryptowallet.MainActivity
import hu.bme.aut.android.cryptowallet.R
import hu.bme.aut.android.cryptowallet.data.api.Status_Data
import hu.bme.aut.android.cryptowallet.data.apiDatabase.CryptoApiItem
import hu.bme.aut.android.cryptowallet.network.NetworkManager
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BtcReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        GlobalScope.launch (Dispatchers.IO){
            var text: String? = null
            val job1 = launch {text = getMyData()}
            job1.join()

        val  i = Intent(context,MainActivity::class.java)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_CANCEL_CURRENT)
        val builder = NotificationCompat.Builder(context!!, "btcChannelId")
            .setSmallIcon(R.mipmap.crypto_wallet)
            .setColor(Color.YELLOW)
            .setContentTitle("Btc price")
            .setContentText(text)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123,builder.build())
        }
    }

    //https://stackoverflow.com/questions/57330766/why-does-my-function-that-calls-an-api-return-an-empty-or-null-value
    suspend fun getMyData():String {
        return suspendCancellableCoroutine { cont ->
        var networkManager = NetworkManager
        networkManager.getCrypto().enqueue(object : Callback<Status_Data?> {
            override fun onResponse(call: Call<Status_Data?>, response: Response<Status_Data?>){
                val responseBody = response.body()
                if (response.isSuccessful) {
                    if (responseBody != null) {
                        cont.resume(responseBody.data.get(0).quote.USD.price.toString())
                    }
                    else {
                        cont.resumeWithException(Exception("Received error response: ${response.message()}"))
                    }

                }
            }
            override fun onFailure(call: Call<Status_Data?>, t: Throwable) {
                Log.d("Onfailure: ", "failed")
            }
        })
    }
}
}

