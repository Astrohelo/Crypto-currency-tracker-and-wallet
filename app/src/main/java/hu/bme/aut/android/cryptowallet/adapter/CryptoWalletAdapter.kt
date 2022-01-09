package hu.bme.aut.android.cryptowallet.adapter

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import hu.bme.aut.android.cryptowallet.MainActivity
import hu.bme.aut.android.cryptowallet.data.wallet.CryptoItem
import hu.bme.aut.android.cryptowallet.data.api.CryptoListItem
import hu.bme.aut.android.cryptowallet.databinding.FragmentWalletListBinding
import hu.bme.aut.android.cryptowallet.fragments.Wallet
import hu.bme.aut.android.cryptowallet.fragments.dialog.SellCryptoDialogFragment

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class CryptoWalletAdapter(walletFragment: Wallet
) :
    RecyclerView.Adapter<CryptoWalletAdapter.CryptoDataViewHolder>() {

    private val items = mutableListOf<CryptoItem>()

    private var fragment=walletFragment

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoDataViewHolder {
        context=parent.context
        return CryptoDataViewHolder(
            FragmentWalletListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CryptoDataViewHolder, position: Int) {
        val item = items[position]


//bonus means old price
        holder.coinSymbol.text  = item.symbol
        holder.coinName.text  = item.name
        holder.coinCurrentPrice.text  = (item.currentPrice*item.amount).toString()
        holder.bonus.text = ((item.currentPrice/item.bonus)-1).toString()
        holder.amount.text = item.amount.toString()

        if ((item.currentPrice/item.bonus)-1>0.0001F){
            holder.bonus.setTextColor(Color.GREEN);
        }
        else if((item.currentPrice/item.bonus)-1<-0.0001F){
            holder.bonus.setTextColor(Color.RED);
        }else{
            holder.bonus.setTextColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener {
            var dialog = SellCryptoDialogFragment(item,this,fragment)
            dialog.show(
                (context as MainActivity).supportFragmentManager,null)
        }


    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: CryptoItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(cryptoItems: List<CryptoItem>) {
        items.clear()
        items.addAll(cryptoItems)
        notifyDataSetChanged()
    }

    fun onCryptoSold(item: CryptoItem, amount: Float):Boolean{
        if(amount==item.amount){
            val position: Int= items.indexOf(item)
            items.remove(item)
            notifyItemRemoved(position)
            return true
        }
        else{
            item.amount-=amount
            notifyDataSetChanged()
            return false
        }
    }




    inner class CryptoDataViewHolder(binding: FragmentWalletListBinding) : RecyclerView.ViewHolder(binding.root) {
        val coinSymbol: TextView = binding.coinSymbol
        val coinName: TextView = binding.coinName
        val coinCurrentPrice: TextView = binding.coinCurrentPrice
        val bonus: TextView = binding.coinBonus
        val amount: TextView = binding.coinAmount
    }



}