package hu.bme.aut.android.cryptowallet.fragments.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import hu.bme.aut.android.cryptowallet.MainActivity
import hu.bme.aut.android.cryptowallet.adapter.CryptoWalletAdapter
import hu.bme.aut.android.cryptowallet.data.wallet.CryptoItem
import hu.bme.aut.android.cryptowallet.data.wallet.CryptoListDatabase
import hu.bme.aut.android.cryptowallet.databinding.DialogSellCryptoBinding
import hu.bme.aut.android.cryptowallet.listener.CryptoDialogListener
import kotlin.concurrent.thread




class SellCryptoDialogFragment(crypto: CryptoItem, adapter: CryptoWalletAdapter, listener: CryptoDialogListener) : DialogFragment() {


    private var listener=listener

    private lateinit var binding: DialogSellCryptoBinding
    private var crypto= crypto
    private var coinName = crypto.name
    private var coinPrice = crypto.currentPrice
    private var coinAmount = crypto.amount

    private  var walletMoney : Float = 0.0f


    //walletDb
    private var adapter=adapter
    private lateinit var database: CryptoListDatabase



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogSellCryptoBinding.inflate(LayoutInflater.from(context))
        database = activity?.let { CryptoListDatabase.getDatabase(it.applicationContext) }!!

        walletMoney=(activity as MainActivity).getMoney()
        val max = crypto.amount
        val min = 0
        val total = max - min

        val slider = binding.sellSlider
        slider.positionListener = { pos -> slider.bubbleText = "${min + (total  * pos)}" }
        slider.position = 0.3f
        slider.startText ="$min"
        slider.endText = "$max"

        binding.sellButton.setOnClickListener {view ->
            thread{
                runOnUiThread {


                if(adapter.onCryptoSold( item=crypto,amount = total  * slider.position)){
                    database.cryptoItemDao().deleteItem(crypto)
                }
                else{
                    database.cryptoItemDao().updateAmount(crypto.amount,crypto.id)
                }

            }
            }
            (activity as MainActivity).setMoney(walletMoney+coinPrice*slider.position*max)
            listener.onCryptoTraded()
            dialog?.dismiss()
        }
        binding.cancelButton.setOnClickListener{
            dialog?.dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("How many ${coinName} are you selling?")
            .setView(binding.root)
            .create()
    }


    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }




}