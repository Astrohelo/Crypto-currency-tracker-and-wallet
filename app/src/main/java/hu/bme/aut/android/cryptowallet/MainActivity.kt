package hu.bme.aut.android.cryptowallet

import android.app.*
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import hu.bme.aut.android.cryptowallet.data.apiDatabase.CryptoApiListDatabase
import hu.bme.aut.android.cryptowallet.databinding.ActivityMainBinding
import hu.bme.aut.android.cryptowallet.notification.BtcReceiver
import hu.bme.aut.android.cryptowallet.sharedPref.WalletPreference

class MainActivity : AppCompatActivity()
{

    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPref: WalletPreference
    private var money: Float = 0.0f

    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref= WalletPreference(this)

        binding.themeSwitch.isChecked=sharedPref.getNightMode()
        when (binding.themeSwitch.isChecked) {
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.themeSwitch.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }



        if(sharedPref.getMoney()==-1F){
            sharedPref.changeMoney(1000F)
        }
        money=sharedPref.getMoney()


        createNotificationChannel()
        setNotificationTime()


    }

    //Set time to when you want to get a message of btc price.
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setNotificationTime(){
        calendar= Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent= Intent(this,BtcReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,calendar.timeInMillis,AlarmManager.INTERVAL_DAY,pendingIntent
        )
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "btcChannelId",
                "Btc Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        lateinit var database: CryptoApiListDatabase
        sharedPref.setNightMode( binding.themeSwitch.isChecked)
        database =  CryptoApiListDatabase.getDatabase(applicationContext)
        database.cyptoApiItemDao().deleteAll()
    }

    fun getMoney():Float{
        return money
    }

    fun setMoney(newMoney: Float){
        money=newMoney
        sharedPref.changeMoney(newMoney)
    }




}