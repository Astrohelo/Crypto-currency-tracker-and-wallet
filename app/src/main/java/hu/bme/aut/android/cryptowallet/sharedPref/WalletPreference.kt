package hu.bme.aut.android.cryptowallet.sharedPref

import android.content.Context
import android.content.SharedPreferences

class WalletPreference(c: Context) {

    private var fileName:String = "walletMoney"
    var prefDb: SharedPreferences = c.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    private var fName:String = "nightMode"
    var db: SharedPreferences = c.getSharedPreferences(fName, Context.MODE_PRIVATE)

    fun changeMoney(money: Float) {
        val spEditor = prefDb.edit()
        spEditor.putFloat("money", money)
        spEditor.apply()
    }

    fun getMoney(): Float {
        return prefDb.getFloat("money",-1F)
    }

    fun setNightMode(isNight: Boolean){
        val spEditor = db.edit()
        spEditor.putBoolean("nightMode",isNight)
        spEditor.apply()
    }

    fun getNightMode():Boolean{
        return db.getBoolean("nightMode",false)
    }


}