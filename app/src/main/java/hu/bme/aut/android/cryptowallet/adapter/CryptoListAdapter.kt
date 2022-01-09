package hu.bme.aut.android.cryptowallet.adapter

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import hu.bme.aut.android.cryptowallet.data.api.CryptoListItem
import hu.bme.aut.android.cryptowallet.databinding.FragmentCryptoListBinding

import hu.bme.aut.android.cryptowallet.fragments.dialog.BuyCryptoDialogFragment
import hu.bme.aut.android.cryptowallet.MainActivity
import hu.bme.aut.android.cryptowallet.fragments.CryptoList


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
*/
class CryptoListAdapter(
    listFragment:CryptoList
    ) :
    RecyclerView.Adapter<CryptoListAdapter.CryptoDataViewHolder>(){

    private val items = mutableListOf<CryptoListItem>()

    private var fragment=listFragment

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoDataViewHolder {
        context=parent.context
        return CryptoDataViewHolder(
            FragmentCryptoListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CryptoDataViewHolder, position: Int) {
        val item = items[position]

        val usd = item.quote.USD

        holder.coinSymbol.text  = item.symbol
        holder.coinName.text  = item.name
        holder.coinPrice.text  = usd.price.toString()
        holder.oneHour.text  = String.format("%.2f", usd.percent_change_1h)
        holder.oneDay.text  = String.format("%.2f",usd.percent_change_24h)
        holder.oneWeek.text  = String.format("%.2f",usd.percent_change_7d)

        if (usd.percent_change_1h>0){
            holder.oneHour.setTextColor(Color.GREEN);
        }
        else{
            holder.oneHour.setTextColor(Color.RED);
        }
        if (usd.percent_change_24h>0){
            holder.oneDay.setTextColor(Color.GREEN);
        }
        else{
            holder.oneDay.setTextColor(Color.RED);
        }
        if (usd.percent_change_7d>0){
            holder.oneWeek.setTextColor(Color.GREEN);
        }
        else{
            holder.oneWeek.setTextColor(Color.RED);
        }

        holder.itemView.setOnClickListener {
            var walletMoney = (context as MainActivity).getMoney()
            if(walletMoney>0){
                var dialog = BuyCryptoDialogFragment(item.name,item.symbol,usd.price,fragment)
                dialog.show(
                    (context as MainActivity).supportFragmentManager,null
                )
            }

        }


    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: CryptoListItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(cryptoItems: List<CryptoListItem>) {
        items.clear()
        items.addAll(cryptoItems)
        notifyDataSetChanged()
    }

    inner class CryptoDataViewHolder(binding: FragmentCryptoListBinding) : RecyclerView.ViewHolder(binding.root) {
        val coinSymbol: TextView = binding.coinSymbol
        val coinName: TextView = binding.coinName
        val coinPrice: TextView = binding.coinPrice
        val oneHour: TextView = binding.oneHour
        val oneDay: TextView = binding.oneDay
        val oneWeek: TextView = binding.oneWeek
    }



}