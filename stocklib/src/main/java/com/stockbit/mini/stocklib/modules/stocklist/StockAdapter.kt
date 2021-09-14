package com.stockbit.mini.stocklib.modules.stocklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stockbit.mini.stocklib.databinding.StockRowBinding
import com.stockbit.mini.stocklib.repositories.cache.storage.entity.Stock

class StockAdapter :
    RecyclerView.Adapter<StockAdapter.ViewHolder>() {
    var stocks = mutableListOf<Stock>()
    
    class ViewHolder(private val binding: StockRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stock: Stock) {
            binding.tvName.text = stock.name
            binding.tvPrice.text = stock.price
            binding.tvFullname.text = stock.fullname
            binding.tvStatus.text = stock.status
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = StockRowBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(stocks[position])
    }

    override fun getItemCount() = stocks.size
}