package hu.bme.aut.android.cryptowallet.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.cryptowallet.MainActivity
import hu.bme.aut.android.cryptowallet.R
import hu.bme.aut.android.cryptowallet.adapter.CryptoListAdapter
import hu.bme.aut.android.cryptowallet.data.api.CryptoListItem
import hu.bme.aut.android.cryptowallet.data.api.Quote
import hu.bme.aut.android.cryptowallet.data.api.Status_Data
import hu.bme.aut.android.cryptowallet.data.api.USD
import hu.bme.aut.android.cryptowallet.data.apiDatabase.CryptoApiItem
import hu.bme.aut.android.cryptowallet.data.apiDatabase.CryptoApiListDatabase
import hu.bme.aut.android.cryptowallet.data.wallet.CryptoListDatabase
import hu.bme.aut.android.cryptowallet.databinding.FragmentCryptoBinding
import hu.bme.aut.android.cryptowallet.listener.CryptoDialogListener
import hu.bme.aut.android.cryptowallet.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread


class CryptoList : Fragment() , CryptoDialogListener {

    private var _binding: FragmentCryptoBinding? = null


    var networkManager = NetworkManager

    private lateinit var database: CryptoApiListDatabase
    private lateinit var databaseWallet: CryptoListDatabase
    lateinit var myAdapter: CryptoListAdapter

    private val binding get() = _binding!!

    private var walletMoney: Float = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCryptoBinding.inflate(inflater, container, false)

        database = activity?.let { CryptoApiListDatabase.getDatabase(it.applicationContext) }!!
        databaseWallet = activity?.let { CryptoListDatabase.getDatabase(it.applicationContext) }!!

        //csak akkor torol a databaseből ha az activity ondestroy lefut  ergo csak 1x hivja meg az apit, illetve refreshnél
        if(database.cyptoApiItemDao().getAll().size<1){
            getMyData()

        }

        initRecyclerView()



        walletMoney = (activity as MainActivity).getMoney()
        binding.balance.text = walletMoney.toString()

        //ujra meghivja az apit
        binding.refreshLayout.setOnRefreshListener {
            getMyData()
            binding.refreshLayout.isRefreshing = false
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_crypto_to_wallet)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getMyData() {
        database.cyptoApiItemDao().deleteAll()
        networkManager.getCrypto().enqueue(object : Callback<Status_Data?> {
            override fun onResponse(call: Call<Status_Data?>, response: Response<Status_Data?>) {
                val responseBody = response.body()
                if (response.isSuccessful) {
                    if (responseBody != null) {
                        myAdapter.update(responseBody.data)
                        for (i in 0..responseBody.data.size-1) {
                            val newItem = CryptoApiItem(name=responseBody.data.get(i).name,
                                symbol=responseBody.data.get(i).symbol,
                                currentPrice=responseBody.data.get(i).quote.USD.price,
                                percent_change_1h=responseBody.data.get(i).quote.USD.percent_change_1h,
                                percent_change_24h=responseBody.data.get(i).quote.USD.percent_change_24h,
                                percent_change_7d=responseBody.data.get(i).quote.USD.percent_change_7d)
                            val insertId = database.cyptoApiItemDao().insert(newItem)
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





    override fun onCryptoTraded() {
        walletMoney = (activity as MainActivity).getMoney()
        binding.balance.text = walletMoney.toString()
    }

    private fun initRecyclerView() {
        myAdapter = CryptoListAdapter(this)
        binding.rvCryptoList.layoutManager = LinearLayoutManager(activity)
        binding.rvCryptoList.adapter = myAdapter
        loadCryptosInBackground()
    }

    private fun loadCryptosInBackground() {
        thread {
            val cryptos = database.cyptoApiItemDao().getAll()
            var convertedCrypto : MutableList<CryptoListItem> = mutableListOf()
            for (i in 0..cryptos.size-1) {
                var usd = USD(
                    cryptos.get(i).currentPrice,
                    cryptos.get(i).percent_change_1h,
                    cryptos.get(i).percent_change_24h,
                    cryptos.get(i).percent_change_7d
                )
                var quote = Quote(usd)
                var newCrypto = CryptoListItem(
                    id=i.toString(),
                    name = cryptos.get(i).name, symbol = cryptos.get(i).symbol, quote = quote
                )
                convertedCrypto.add(newCrypto)
            }
            runOnUiThread {
                myAdapter.update(convertedCrypto)
            }
        }
    }

    fun updateWallet(){
        var cryptos = databaseWallet.cryptoItemDao().getAll()
        val freshCryptos = database.cyptoApiItemDao().getAll()

        for (i in 0..cryptos.size-1) {
            for (j in 0..freshCryptos.size-1) {
                if (cryptos.get(i).name == freshCryptos.get(j).name) {
                    databaseWallet.cryptoItemDao().updatePrice(freshCryptos.get(j).currentPrice, cryptos.get(i).id)
                    break
                }
            }
        }

    }

    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }


}