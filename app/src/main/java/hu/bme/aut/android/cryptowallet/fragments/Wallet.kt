package hu.bme.aut.android.cryptowallet.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import hu.bme.aut.android.cryptowallet.MainActivity
import hu.bme.aut.android.cryptowallet.R
import hu.bme.aut.android.cryptowallet.adapter.CryptoWalletAdapter
import hu.bme.aut.android.cryptowallet.data.api.Status_Data
import hu.bme.aut.android.cryptowallet.data.apiDatabase.CryptoApiItem
import hu.bme.aut.android.cryptowallet.data.apiDatabase.CryptoApiListDatabase
import hu.bme.aut.android.cryptowallet.data.wallet.CryptoItem
import hu.bme.aut.android.cryptowallet.data.wallet.CryptoListDatabase
import hu.bme.aut.android.cryptowallet.databinding.FragmentWalletBinding
import hu.bme.aut.android.cryptowallet.listener.CryptoDialogListener
import hu.bme.aut.android.cryptowallet.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

/**
 * A fragment representing a list of Items.
 */
class Wallet : Fragment(), CryptoDialogListener {

    private var _binding: FragmentWalletBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var networkManager = NetworkManager

    private lateinit var database: CryptoListDatabase
    private lateinit var databaseOfAll: CryptoApiListDatabase
    private lateinit var adapter: CryptoWalletAdapter


    private  var walletMoney : Float = 0.0f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)

        database = activity?.let { CryptoListDatabase.getDatabase(it.applicationContext) }!!
        databaseOfAll = activity?.let { CryptoApiListDatabase.getDatabase(it.applicationContext) }!!

        initRecyclerView()



        walletMoney=(activity as MainActivity).getMoney()
        binding.balance.text= walletMoney.toString()

        binding.refreshLayoutWallet.setOnRefreshListener {
            getMyData()
            binding.refreshLayoutWallet.isRefreshing = false
        }

        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_wallet_to_crypto)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        adapter = CryptoWalletAdapter(this)
        binding.rvMain.layoutManager = LinearLayoutManager(this.context)
        binding.rvMain.adapter = adapter
        loadCryptosInBackground()
    }

    private fun loadCryptosInBackground() {
        thread {
            val cryptos = database.cryptoItemDao().getAll()
            runOnUiThread {
                adapter.update(cryptos)
            }
        }
    }

    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }


    override fun onCryptoTraded() {
        walletMoney=(activity as MainActivity).getMoney()
        binding.balance.text= walletMoney.toString()
    }


    private fun getMyData() {
        databaseOfAll.cyptoApiItemDao().deleteAll()
        networkManager.getCrypto().enqueue(object : Callback<Status_Data?> {
            override fun onResponse(call: Call<Status_Data?>, response: Response<Status_Data?>) {
                val responseBody = response.body()
                if (response.isSuccessful) {
                    if (responseBody != null) {
                        for (i in 0..responseBody.data.size-1) {
                            val newItem = CryptoApiItem(name=responseBody.data.get(i).name,
                                symbol=responseBody.data.get(i).symbol,
                                currentPrice=responseBody.data.get(i).quote.USD.price,
                                percent_change_1h=responseBody.data.get(i).quote.USD.percent_change_1h,
                                percent_change_24h=responseBody.data.get(i).quote.USD.percent_change_24h,
                                percent_change_7d=responseBody.data.get(i).quote.USD.percent_change_7d)
                            val insertId = databaseOfAll.cyptoApiItemDao().insert(newItem)
                            newItem.id = insertId
                        }

                    }
                }
            }

            override fun onFailure(call: Call<Status_Data?>, t: Throwable) {
                Log.d("Onfailure: ", "failed")
            }
        })

        updateWallet()
    }

    fun updateWallet(){
        var cryptos = database.cryptoItemDao().getAll()
        val freshCryptos = databaseOfAll.cyptoApiItemDao().getAll()

        for (i in 0..cryptos.size-1) {
            for (j in 0..freshCryptos.size-1) {
                if (cryptos.get(i).name == freshCryptos.get(j).name) {
                    database.cryptoItemDao().updatePrice(freshCryptos.get(j).currentPrice, cryptos.get(i).id)
                     break
                }
            }
        }

        cryptos = database.cryptoItemDao().getAll()
        runOnUiThread {
            adapter.update(cryptos)
    }
    }

}
