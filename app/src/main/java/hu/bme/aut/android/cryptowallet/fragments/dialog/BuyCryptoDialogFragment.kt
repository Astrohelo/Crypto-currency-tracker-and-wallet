package hu.bme.aut.android.cryptowallet.fragments.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import hu.bme.aut.android.cryptowallet.MainActivity
import hu.bme.aut.android.cryptowallet.data.wallet.CryptoItem
import hu.bme.aut.android.cryptowallet.data.wallet.CryptoListDatabase
import hu.bme.aut.android.cryptowallet.databinding.DialogBuyCryptoBinding
import hu.bme.aut.android.cryptowallet.fragments.CryptoList
import kotlin.concurrent.thread


class BuyCryptoDialogFragment(name: String, symbol: String, price: Float, listener: CryptoList) : DialogFragment() {


    private var listener=listener

    private lateinit var binding: DialogBuyCryptoBinding
    private var coinName = name
    private var coinSymbol = symbol
    private var coinPrice = price

    private  var walletMoney : Float = 0.0f

    //walletDb
    private lateinit var database: CryptoListDatabase


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogBuyCryptoBinding.inflate(LayoutInflater.from(context))
        database = activity?.let { CryptoListDatabase.getDatabase(it.applicationContext) }!!

        walletMoney=(activity as MainActivity).getMoney()
        val max = walletMoney/coinPrice
        val min = 0
        val total = max - min

        val slider = binding.buySlider
        slider.positionListener = { pos -> slider.bubbleText = "${min + (total  * pos)}" }
        slider.position = 0.3f
        slider.startText ="$min"
        slider.endText = "$max"

        binding.buyButton.setOnClickListener {view ->
            onCryptoItemCreated(CryptoItem(name=coinName, bonus = coinPrice, currentPrice=coinPrice,symbol = coinSymbol,amount = total  *slider.position))
            (activity as MainActivity).setMoney(walletMoney-coinPrice*slider.position*total)
            listener.onCryptoTraded()
            dialog?.dismiss()
        }
        binding.cancelButton.setOnClickListener{
            dialog?.dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("How many ${coinName} are you buying?")
            .setView(binding.root)
            .create()
    }


    fun onCryptoItemCreated(newItem: CryptoItem) {
        thread {
             database.cryptoItemDao().insert(newItem)
        }
    }

}